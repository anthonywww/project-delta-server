# Project Delta Synchronization Server
A server for the project-delta clients to connect and synchronize with.

## Configuration
The `server.conf` configuration file is used to determine the server's properties.
- `address` The address to bind to. Type: String, Example: `127.0.0.1` or `localhost`
- `port` The port number on the address to bind to. Type: Integer, Example: `11234`
- `max_clients` The maximum number of valid connections (clients) the server will accept. Type: Integer, Example: `60`
- `auth_timeout` Max time in milliseconds for the client to authenticate, otherwise the connection is dropped, Type: Integer, Example: `1000`
- `connection_timeout` Max time in milliseconds between a 'heartbeat' otherwise the connection is dropped, Type: Integer, Example: `3000`
- `log_level` The console log filter level. Type: String, Example: `FINEST`, `FINER`, `FINE`, `INFO`, `WARNING`.

## Commands
While the server is running you will have access to an interactive REPL console for issuing commands.
- `exit` Gracefully disconnect all clients and shutdown the server.
- `config` View the current configuration settings.
- `status` View the current server status, connected clients and state-machine status.
- `logger` View or change the current log level (does not save to config!).
    - `logger FINE` Will temporarily change the log_level to `FINE`.
- `debug` Base debug tools command.
    - `debug` Show all debug sub-commands.
    - `debug broadcast` Send a broadcast packet to all connected clients.

## Updates
- All development and testing will be done in GNU/Linux (A proper OS for running servers).
