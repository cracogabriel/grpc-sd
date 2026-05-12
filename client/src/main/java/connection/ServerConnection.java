// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

package connection;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import movies.MovieServiceGrpc;

public class ServerConnection {

    private final String host;
    private final int port;
    private final ManagedChannel channel;
    private final MovieServiceGrpc.MovieServiceBlockingStub stub;

    /**
     * Creates a new connection to the gRPC server.
     *
     * @param host The hostname or IP address of the server
     * @param port The port number of the server
     */
    public ServerConnection(String host, int port) {
        this.host = host;
        this.port = port;

        // Create the gRPC communication channel
        // usePlaintext() is used because we are not using SSL/TLS in the local environment
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        // Create the blocking stub for synchronous RPC calls
        this.stub = MovieServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Gets the connected server host.
     *
     * @return The server host string
     */
    public String getHost() { return host; }

    /**
     * Gets the connected server port.
     *
     * @return The server port integer
     */
    public int getPort() { return port; }

    /**
     * Returns the blocking stub so GUI dialogs can invoke remote methods directly.
     */
    public MovieServiceGrpc.MovieServiceBlockingStub getStub() {
        return stub;
    }

    /**
     * Properly shuts down the gRPC channel.
     */
    public void close() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}