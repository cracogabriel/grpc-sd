// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

package gui;

import connection.ServerConnection;
import gui.dialogs.AddMovieDialog;
import gui.dialogs.SearchGenreDialog;
import gui.dialogs.GetMovieDialog;
import gui.dialogs.UpdateMovieDialog;
import gui.dialogs.DeleteMovieDialog;
import gui.dialogs.SearchActorDialog;

import javax.swing.*;
import java.awt.*;

public class MainWindow {

    private final JFrame frame;
    private final ServerConnection conn;

    /**
     * Initializes the main window of the application.
     *
     * @param conn The active gRPC server connection
     */
    public MainWindow(ServerConnection conn) {
        this.conn = conn;

        frame = new JFrame("mflix client");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel label = new JLabel("connected to server at " + conn.getHost() + ":" + conn.getPort(), SwingConstants.CENTER);
        frame.add(label, BorderLayout.CENTER);

        frame.add(buildButtonPanel(), BorderLayout.SOUTH);
    }

    /**
     * Builds the panel containing action buttons for movie operations.
     *
     * @return JPanel containing the configured action buttons
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JButton btnCreate      = new JButton("add movie");
        JButton btnGet         = new JButton("get movie");
        JButton btnUpdate      = new JButton("update movie");
        JButton btnDelete      = new JButton("delete movie");
        JButton btnSearchGenre = new JButton("search by genre");
        JButton btnSearchActor = new JButton("search by actor");

        btnCreate.addActionListener(e ->
            new AddMovieDialog(frame, conn).show()
        );

        btnGet.addActionListener(e ->
            new GetMovieDialog(frame, conn).show()
        );

        btnUpdate.addActionListener(e ->
            new UpdateMovieDialog(frame, conn).show()
        );

        btnDelete.addActionListener(e ->
            new DeleteMovieDialog(frame, conn).show()
        );

        btnSearchGenre.addActionListener(e ->
            new SearchGenreDialog(frame, conn).show()
        );

        btnSearchActor.addActionListener(e ->
            new SearchActorDialog(frame, conn).show()
        );

        panel.add(btnCreate);
        panel.add(btnGet);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnSearchGenre);
        panel.add(btnSearchActor);
        return panel;
    }

    /**
     * Displays the main window to the user.
     */
    public void show() {
        frame.setVisible(true);
    }
}