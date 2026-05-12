// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

package gui.dialogs;

import connection.ServerConnection;
import movies.DeleteResponse;
import movies.MovieIdRequest;

import javax.swing.*;
import java.awt.*;

public class DeleteMovieDialog {

    private final JFrame           parent;
    private final ServerConnection conn;

    /**
     * Initializes the delete movie dialog.
     *
     * @param parent The parent JFrame
     * @param conn   The active gRPC server connection
     */
    public DeleteMovieDialog(JFrame parent, ServerConnection conn) {
        this.parent = parent;
        this.conn   = conn;
    }

    /**
     * Displays the dialog and handles user interactions.
     */
    public void show() {
        JDialog dialog = new JDialog(parent, "delete movie", true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        JTextField idField = new JTextField(20);

        formPanel.add(new JLabel("movie id:"));
        formPanel.add(idField);
        dialog.add(formPanel, BorderLayout.CENTER);

        JButton deleteBtn = new JButton("delete");
        dialog.add(deleteBtn, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> {
            String id = idField.getText().trim();

            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "id cannot be empty!", "validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "are you sure you want to delete movie " + id + "?",
                    "confirm delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                MovieIdRequest request = MovieIdRequest.newBuilder().setId(id).build();
                DeleteResponse response = conn.getStub().deleteMovie(request);

                if (response.getSuccess()) {
                    System.out.println("movie deleted: " + id);
                    JOptionPane.showMessageDialog(dialog, "movie deleted!", "success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    System.out.println("server error: " + response.getError());
                    JOptionPane.showMessageDialog(dialog, "error: " + response.getError(), "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                System.err.println("error during delete: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, "connection error.", "error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }
}