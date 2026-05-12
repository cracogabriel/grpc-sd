// ========================================================================================================================
// Activity: RPC Activity - Movie Management | Distributed Systems | Universidade Tecnológica Federal do Paraná
// Authors: Gabriel Craco Tasarz, Leonardo Jun'Ity Ozima
// Professor: Prof. Rodrigo Campiolo
// Release Date: May 10, 2026
// Last Change At: May 12, 2026
// ========================================================================================================================

package gui.dialogs;

import connection.ServerConnection;
import movies.GenreRequest;
import movies.Movie;
import movies.MovieListResponse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SearchGenreDialog {

    private final JFrame           parent;
    private final ServerConnection conn;

    /**
     * Initializes the search by genre dialog.
     *
     * @param parent The parent JFrame
     * @param conn   The active gRPC server connection
     */
    public SearchGenreDialog(JFrame parent, ServerConnection conn) {
        this.parent = parent;
        this.conn   = conn;
    }

    /**
     * Displays the dialog and handles user interactions.
     */
    public void show() {
        JDialog dialog = new JDialog(parent, "search by genre", true);
        dialog.setSize(850, 600); 
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JTextField genreField = new JTextField(15);
        JButton    searchBtn  = new JButton("search");

        topPanel.add(new JLabel("genre:"));
        topPanel.add(genreField);
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
            String genre = genreField.getText().trim();

            if (genre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "genre cannot be empty!", "validation error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            tableModel.setRowCount(0); // Clear the table before populating
            tableModel.addRow(new Object[]{"searching...", "", "", "", "", ""});
            fetchMoviesByGenre(genre, tableModel);
        });

        dialog.setVisible(true);
    }

    /**
     * Fetches a list of movies matching the specified genre and populates the table.
     *
     * @param genre      The genre to search for
     * @param tableModel The table model to populate with the results
     */
    private void fetchMoviesByGenre(String genre, DefaultTableModel tableModel) {
        try {
            System.out.println("searching by genre: " + genre);

            GenreRequest request = GenreRequest.newBuilder().setGenre(genre).build();
            MovieListResponse response = conn.getStub().listByGenre(request);

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