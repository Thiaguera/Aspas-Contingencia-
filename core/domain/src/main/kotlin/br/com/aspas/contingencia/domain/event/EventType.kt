package br.com.aspas.contingencia.domain.event
object EventType {
const val CATALOG_IMPORTED = "catalog_imported"
const val CATALOG_ITEM_AVAILABILITY_CHANGED = "catalog_item_availability_changed"
const val TABLE_OPENED = "table_opened"
const val TABLE_CLOSED = "table_closed"
const val TABLE_LEASE_ACQUIRED = "table_lease_acquired"
const val TABLE_LEASE_RENEWED = "table_lease_renewed"
const val TABLE_LEASE_RELEASED = "table_lease_released"
const val TABLE_LEASE_EXPIRED = "table_lease_expired"
const val TABLE_LEASE_FORCED = "table_lease_forced"
const val ORDER_ITEM_ADDED = "order_item_added"
const val ORDER_ITEM_CANCELLED = "order_item_cancelled"
const val ORDER_ITEM_PRICE_ADJUSTED = "order_item_price_adjusted"
const val ORDER_ITEM_COMPED = "order_item_comped"
const val ORDER_VOIDED = "order_voided"
const val ORDER_NOTE_ADDED = "order_note_added"
const val PAYMENT_REGISTERED = "payment_registered"
const val PAYMENT_REVERSED = "payment_reversed"
const val SERVICE_FEE_REMOVED = "service_fee_removed"
const val PRINT_BATCH_QUEUED = "print_batch_queued"
const val PRINT_BATCH_CANCELLATION_QUEUED = "print_batch_cancellation_queued"
const val PRINT_JOB_COMPLETED = "print_job_completed"
const val PRINT_JOB_FAILED = "print_job_failed"
const val PRINT_JOB_FORWARDED = "print_job_forwarded"
const val REPRINT_REQUESTED = "reprint_requested"
const val PRINTER_STATUS_CHANGED = "printer_status_changed"
const val CASH_SESSION_OPENED = "cash_session_opened"
const val CASH_SESSION_CLOSED = "cash_session_closed"
const val CASH_MOVEMENT = "cash_movement"
const val SYNC_CAUGHT_UP = "sync_caught_up"
const val USER_CREATED = "user_created"
const val USER_DEACTIVATED = "user_deactivated"
const val DEVICE_TRUSTED = "device_trusted"
const val AUDIT_ACTION_RECORDED = "audit_action_recorded"
val ALL: Set<String> = setOf(
CATALOG_IMPORTED, CATALOG_ITEM_AVAILABILITY_CHANGED,
TABLE_OPENED, TABLE_CLOSED, TABLE_LEASE_ACQUIRED, TABLE_LEASE_RENEWED,
TABLE_LEASE_RELEASED, TABLE_LEASE_EXPIRED, TABLE_LEASE_FORCED,
ORDER_ITEM_ADDED, ORDER_ITEM_CANCELLED, ORDER_ITEM_PRICE_ADJUSTED,
ORDER_ITEM_COMPED, ORDER_VOIDED, ORDER_NOTE_ADDED,
PAYMENT_REGISTERED, PAYMENT_REVERSED, SERVICE_FEE_REMOVED,
PRINT_BATCH_QUEUED, PRINT_BATCH_CANCELLATION_QUEUED,
PRINT_JOB_COMPLETED, PRINT_JOB_FAILED, PRINT_JOB_FORWARDED, REPRINT_REQUESTED, PRINTER_STATUS_CHANGED,
CASH_SESSION_OPENED, CASH_SESSION_CLOSED, CASH_MOVEMENT,
SYNC_CAUGHT_UP, USER_CREATED, USER_DEACTIVATED, DEVICE_TRUSTED, AUDIT_ACTION_RECORDED)
}
