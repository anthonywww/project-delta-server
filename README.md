# Project Delta Synchronization Server
A server for the project-delta clients to connect and synchronize with.

## Configuration
The `server.conf` configuration file is used to determine the server's properties.
- `server-address` The address to bind to. Type: String, Example: `127.0.0.1` or `localhost`
- `server-port` The port number on the address to bind to. Type: Integer, Example: `11234`
- `server-heartbeat` How often in seconds should the server send a heartbeat to each client. Type: Integer, Example: `3`
- `max-clients` The maximum number of valid connections (clients) the server will accept. Type: Integer, Example: `60`
- `log-level` The console log filter level. Type: String, Example: `FINEST`, `FINE`, `INFO`, `WARNING`.

## Commands
While the server is running you will have access to an interactive REPL console for issuing commands.
- `exit` Gracefully disconnect all clients and shutdown the server.

## Updates
- All development and testing will be done in GNU/Linux (A proper OS for running servers).
