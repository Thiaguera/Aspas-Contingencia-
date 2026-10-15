package br.com.aspas.contingencia.data.projection

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.aspas.contingencia.data.db.AspasDatabase
import br.com.aspas.contingencia.data.db.entity.EventEntity
import br.com.aspas.contingencia.data.db.entity.PrintBatchEntity
import br.com.aspas.contingencia.data.db.entity.PrintJobEntity
import br.com.aspas.contingencia.data.db.entity.ProjectionStateEntity
import br.com.aspas.contingencia.data.db.entity.TableEntity
import br.com.aspas.contingencia.domain.event.EventEnvelope
import br.com.aspas.contingencia.domain.event.EventType
import br.com.aspas.contingencia.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

/** Room integration coverage for projection idempotency and fail-closed rebuilds. */
class ProjectionEngineRoomIntegrationTest {
    private lateinit var db: AspasDatabase
    private lateinit var engine: ProjectionEngine

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AspasDatabase::class.java
        ).allowMainThreadQueries().build()
        engine = ProjectionEngine(db, Json { ignoreUnknownKeys = true }, NoEvents, MaintenanceGate(db))
    }

    @After fun tearDown() = db.close()

    @Test fun repeatedEvent_hasOneMarkerAndOneProjection() = runBlocking {
        db.tableDao().upsert(table())
        val event = tableOpened("event-1")
        engine.apply(event)
        engine.apply(event)
        assertEquals(1, db.appliedEventDao().count())
        assertNotNull(db.orderDao().getById("order-1"))
        assertEquals(1, db.orderDao().listAll().size)
    }

    @Test fun malformedPayload_rollsBackAndDoesNotWriteMarker() = runBlocking {
        db.tableDao().upsert(table())
        val malformed = tableOpened("bad").copy(payload = "{not-json")
        runCatching { engine.apply(malformed) }
        assertFalse(db.appliedEventDao().isApplied("bad"))
        assertEquals(0, db.orderDao().listAll().size)
        assertEquals("free", db.tableDao().getById("table-1")!!.status)
    }

    @Test fun rebuild_preservesTablesAndPrints_skipsPhysicalEvents_andRetainsLegacyQuarantine() = runBlocking {
        db.tableDao().upsert(table())
        db.printJobDao().upsertBatch(PrintBatchEntity("batch-kept", "item-kept", "item", 1L, "u", null))
        db.printJobDao().upsertJob(PrintJobEntity("job-kept", "batch-kept", "printer", "ok", 1, null, null, null, false, null, 1L, 2L))
        db.projectionStateDao().put(ProjectionStateEntity(rebuildInProgress = false, legacyCheckpointHlc = "legacy-hlc", updatedAt = 1L))
        db.eventDao().insert(eventEntity(tableOpened("open")))
        // If replayed, this otherwise creates a new physical job.
        db.eventDao().insert(EventEntity("print-event", "002", "device", "u", EventType.PRINT_BATCH_QUEUED,
            """{"batchId":"new-batch","orderId":"order-1","itemId":"item-1","kind":"item","createdBy":"u","destinations":["printer"],"jobs":[{"printId":"new-job","printerId":"printer"}]}""", 2L))

        assertEquals(2, engine.rebuildFromHistory())

        assertEquals("open", db.tableDao().getById("table-1")!!.status)
        assertNotNull(db.orderDao().getById("order-1"))
        assertEquals("ok", db.printJobDao().getById("job-kept")!!.status)
        assertEquals(null, db.printJobDao().getById("new-job"))
        val state = db.projectionStateDao().get()!!
        assertFalse(state.rebuildInProgress)
        assertEquals("legacy-hlc", state.legacyCheckpointHlc)
    }

    private fun table() = TableEntity("table-1", 1, "Mesa 1", "free", null, null, null)
    private fun tableOpened(id: String) = EventEnvelope(id, "001", "device", "u", EventType.TABLE_OPENED,
        """{"tableId":"table-1","orderId":"order-1","openedBy":"u"}""", 1L)
    private fun eventEntity(e: EventEnvelope) = EventEntity(e.eventId, e.hlc, e.deviceId, e.userId, e.type, e.payload, e.createdAt)

    private object NoEvents : EventRepository {
        override suspend fun append(type: String, payloadJson: String, userId: String?) = error("unused")
        override suspend fun appendRemote(envelope: EventEnvelope) = Unit
        override fun pendingForSync(): Flow<List<EventEnvelope>> = emptyFlow()
        override suspend fun eventsSince(sinceHlc: br.com.aspas.contingencia.common.Hlc?, limit: Int) = emptyList<EventEnvelope>()
        override suspend fun markSynced(eventIds: List<String>) = Unit
        override suspend fun exists(eventId: String) = false
        override fun observeAll(): Flow<EventEnvelope> = emptyFlow()
    }
}
