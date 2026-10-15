# Native recovery integration

Isolated branch: recovery/native-integration. Never merge into main without functional validation.

Integrated: first manager enrollment, operator-chosen table range, transactional non-destructive catalog initialization, live order projection observation, weighted item input, atomic payment and close event batches, manager-only operator administration, cash movements, current cash session reports, encrypted portable backup export and isolated backup validation.

Limitations: physical printers and LAN P2P are not homologated. Backup validation does not replace the active database. Existing recovery installations retain users and database. Builds do not prove end-to-end behavior. Instrumented tests must be executed, not only compiled.

Recovery application ID remains separate from production. Do not use for restaurant service until device acceptance tests are passed.
