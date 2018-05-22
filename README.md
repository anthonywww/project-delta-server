# Project Delta Synchronization Server
A server for the [project-delta](https://github.com/anthonywww/project-delta) clients to connect and synchronize with.


Clients have a x (columns) and y (rows) physical location which is set at the client via CLI program parameters, and then sent to the server at the connection handshake. Top left being x=0, y=0.

All development and testing will be done in GNU/Linux (A proper OS for running servers).

## Configuration
The `server.conf` configuration file is used to determine the server's properties.
- `address` The address to bind to. Type: String, Example: `127.0.0.1` or `localhost`
- `port` The port number on the address to bind to. Type: Integer, Example: `11234`
- `max_clients` The maximum number of valid connections (clients) the server will accept. Type: Integer, Example: `60`
- `auth_timeout` Max time in milliseconds for the client to authenticate, otherwise the connection is dropped, Type: Integer, Example: `1000`
- `heartbeat_interval` Time in milliseconds in which a `HEARTBEAT` is sent to the client. Type: Integer, Example: `800`
- `heartbeat_timeout` Max time in milliseconds waiting for a `HEARTBEAT_ACK` packet before the connection is dropped, Type: Integer, Example: `1600`
- `log_level` The console log filter level. Type: String, Example: `FINEST`, `FINER`, `FINE`, `INFO`, `WARNING`.

## Commands
While the server is running you will have access to an interactive REPL console for issuing commands.
- `exit` Gracefully disconnect all clients and shutdown the server.
- `config` View the current configuration settings.
- `status` View the current server status, connected clients and state-machine status.
    - `0,0` View the specific status of client at x=0, y=0.
- `send` Send a data message to a specific client.
    - `0,4 ping` Send the client at x=0, y=4 a 'ping' packet.
    - `1,3 text Hello user!` Send the client at x=1, y=3 a 'text' packet with the payload "Hello user!".
- `logger` View or change the current log level (does not save to config!).
    - `set FINE` Will temporarily change the log_level to `FINE`.
- `debug` Base debug tools command.
    - `broadcast my message` Send a broadcast packet to all connected clients with the message "my message".
