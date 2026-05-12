// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

package gui.dialogs;

import connection.ServerConnection;
import movies.ActorRequest;
import movies.Movie;
import movies.MovieListResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchActorDialog {

    private final JFrame           parent;
    private final ServerConnection conn;

    /**
     * Initializes the search by actor dialog.
     *
     * @param parent The parent JFrame
     * @param conn   The active gRPC server connection
     */
    public SearchActorDialog(JFrame parent, ServerConnection conn) {
        this.parent = parent;
        this.conn   = conn;
    }

    /**
     * Displays the dialog and handles user interactions.
     */
    public void show() {
        JDialog dialog = new JDialog(parent, "search by actor", true);
        dialog.setSize(850, 600);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField actorField = new JTextField(15);
        JButton    searchBtn  = new JButton("search");

        topPanel.add(new JLabel("actor:"));
        topPanel.add(actorField);
        topPanel.add(searchBtn);
        dialog.add(topPanel, BorderLayout.NORTH);

        String[] columns = { "id", "title", "year", "runtime", "rated", "genres" };
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disable cell editing
            }
        };
        JTable table = new JTable(tableModel);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(180); 
        table.getColumnModel().getColumn(1).setPreferredWidth(200); 
        table.getColumnModel().getColumn(2).setPreferredWidth(50);  
        table.getColumnModel().getColumn(3).setPreferredWidth(60);  
        table.getColumnModel().getColumn(4).setPreferredWidth(50);  
        table.getColumnModel().getColumn(5).setPreferredWidth(150); 
        
        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> {
            String actor = actorField.getText().trim();

            if (actor.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "actor cannot be empty!", "validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            tableModel.setRowCount(0); // Clear the table before populating
            tableModel.addRow(new Object[]{"searching...", "", "", "", "", ""});
            fetchMoviesByActor(actor, tableModel);
        });

        dialog.setVisible(true);
    }

    /**
     * Fetches a list of movies featuring the specified actor and populates the table.
     *
     * @param actor      The actor's name to search for
     * @param tableModel The table model to populate with the results
     */
    private void fetchMoviesByActor(String actor, DefaultTableModel tableModel) {
        try {
            System.out.println("searching by actor: " + actor);

            ActorRequest request = ActorRequest.newBuilder().setActor(actor).build();
            MovieListResponse response = conn.getStub().listByActor(request);

            tableModel.setRowCount(0); 

            if (response.getSuccess()) {
                    if (response.getMoviesCount() == 0) {
                        tableModel.addRow(new Object[]{"no movies found.", "", "", "", "", ""});
                    } else {
                        for (Movie m : response.getMoviesList()) {
                            tableModel.addRow(new Object[]{ 
                                m.getId(), 
                                m.getTitle(), 
                                m.getYear(),
                                m.getRuntime() > 0 ? m.getRuntime() + " min" : "n/a",
                                m.getRated().isEmpty() ? "unrated" : m.getRated(),
                                String.join(", ", m.getGenresList())
                            });
                        }
                    }
                } else {
                    System.out.println("server error: " + response.getError());
                    tableModel.addRow(new Object[]{"server error: " + response.getError(), "", "", "", "", ""});
                }

            } catch (Exception ex) {
                System.err.println("error during search: " + ex.getMessage());
                tableModel.setRowCount(0);
                tableModel.addRow(new Object[]{"connection error.", "", "", "", "", ""});
            }
    }
}