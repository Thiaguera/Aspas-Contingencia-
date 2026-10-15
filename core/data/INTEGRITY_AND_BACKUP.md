# Stage 2 — integrity and portable backups

## Applied-event ledger and migration

Database schema is **v2**. `AspasMigrations.V1_TO_V2` preserves every v1 table and creates:

- `applied_events(eventId PK, eventHlc, appliedAt, mode)`;
- `projection_state(key PK, rebuildInProgress, legacyCheckpointHlc, updatedAt)`.

The migration checkpoints every existing event as `legacy_checkpoint` **without replaying it**. V1 had materialized projections but no ledger; treating the existing projections as authoritative prevents old `PRINT_BATCH_*` events from recreating jobs or changing completed print state. There is no destructive migration fallback.

`ProjectionEngine.apply` serializes callers and wraps projection writes plus marker insertion in one `RoomDatabase.withTransaction`. A duplicate event observes the marker and is a no-op. If a projection handler fails, the transaction rolls back and no marker is recorded.

## Explicit rebuild only

`rebuildFromHistory()` is a deliberate maintenance operation; startup does not call it. It writes `rebuildInProgress`, serializes application, clears only rebuildable business projections, and replays ordered events. Print batch/job tables are never cleared and print queue creation events are ignored during replay, so completed jobs cannot reset or new physical printing cannot be created.

`PrintScheduler` skips queue processing while `rebuildInProgress` is true. A restore verification opens an isolated database and does not start the scheduler or projection engine.

**Operational limitation:** rebuilding `tables` requires the app's normal table seed/catalog provisioning to run first; rebuild does not invent configuration tables. UI/authorization for invoking rebuild has intentionally not been added in this backend-only stage.

## Portable backup format

`PortableBackupService.export` makes a consistent SQLCipher snapshot using SQLite/SQLCipher `VACUUM INTO` (which takes its own read snapshot and cannot legally run inside a transaction), reads that already encrypted image, then stores it only in an authenticated `EncryptedBackup` envelope:

- AES-256-GCM, 96-bit random nonce and 128-bit tag;
- PBKDF2-HMAC-SHA-256 with a 16-byte random salt and 310,000 iterations;
- schema/format marker, 256 MiB database limit and archive size bounds;
- the SQLCipher key is included only inside AES-GCM ciphertext, allowing a self-contained portable backup; it is never written as a sidecar or plaintext file.

Restore authenticates/decrypts fully before writing, requires a **new non-active database name**, refuses existing targets, opens Room/SQLCipher to validate the key and schema, and deletes the isolated target if validation fails. It never overwrites or closes the active database. Password failures and tampering return `BackupException` before a destination is created.

**Platform limitation:** the current snapshot implementation relies on SQLCipher supporting `VACUUM INTO`; integration testing on the actual SQLCipher Android build is required. It does not export SQLite WAL sidecars because the vacuum output is a consistent standalone snapshot.

## Tests to run

`EncryptedBackupTest` is JVM-oriented and covers round-trip, wrong password, and ciphertext tampering. Add Android instrumentation tests (once Gradle/emulator is available) for:

1. a real encrypted v1 fixture upgraded to v2, asserting all v1 rows survive and event IDs are checkpointed;
2. transaction rollback after an intentionally malformed event (no marker/no partial projection);
3. duplicate application and process restart (one projection row/marker);
4. rebuild preserves a completed print job and does not enqueue any print job;
5. snapshot export/isolated restore against SQLCipher, including an incompatible-schema rejection.
