// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

import connection.ServerConnection;
import gui.MainWindow;

public class MovieClient {
    /**
     * Main entry point for the gRPC movie client.
     * Initializes the connection to the server and starts the GUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5000;

        try {
            ServerConnection conn = new ServerConnection(host, port);
            System.out.println("connected to gRPC server at " + host + ":" + port);

            System.out.println("starting GUI...");
            MainWindow window = new MainWindow(conn);
            window.show();

        } catch (Exception e) {
            System.err.println("error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}