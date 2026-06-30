package cr.ac.ucr.dds3.paraiso.db_proyecto.view;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.ExecutionResult;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryResult;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.service.SqlErrorTranslator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;

public class SqlConsolePanel extends JPanel {

    private final JTextArea sqlEditor = new JTextArea(10, 60);
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable resultTable = new JTable(tableModel);
    private final JLabel statusLabel = new JLabel("Listo");
    private MainController controller;

    public SqlConsolePanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Configurar editor SQL
        sqlEditor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        sqlEditor.setLineWrap(true);
        sqlEditor.setWrapStyleWord(true);

        // Panel superior con el editor y botones
        JPanel editorPanel = new JPanel(new BorderLayout(8, 8));
        editorPanel.add(new JLabel("Consola SQL:"), BorderLayout.NORTH);
        editorPanel.add(new JScrollPane(sqlEditor), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton runButton = new JButton("Ejecutar (▶)");
        JButton clearButton = new JButton("Limpiar");
        buttonPanel.add(clearButton);
        buttonPanel.add(runButton);
        editorPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Configurar eventos
        runButton.addActionListener(e -> executeSql());
        clearButton.addActionListener(e -> clearConsole());

        // Split pane para editor y resultados
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                editorPanel,
                new JScrollPane(resultTable)
        );
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);

        // Barra de estado
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    public void bindController(MainController controller) {
        this.controller = controller;
    }

    private void clearConsole() {
        sqlEditor.setText("");
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        statusLabel.setText("Listo");
        statusLabel.setForeground(Color.BLACK);
    }

    private void executeSql() {
        if (controller == null) return;
        
        String sql = sqlEditor.getText().trim();
        if (sql.isEmpty()) {
            setStatus("Escriba una consulta SQL antes de ejecutar.", Color.RED);
            return;
        }

        String sqlUpper = sql.toUpperCase();
        boolean isReadQuery = sqlUpper.startsWith("SELECT") || 
                              sqlUpper.startsWith("SHOW") || 
                              sqlUpper.startsWith("DESCRIBE") || 
                              sqlUpper.startsWith("EXPLAIN");

        long startTime = System.currentTimeMillis();
        try {
            if (isReadQuery) {
                QueryResult result = controller.runFreeQuery(sql);
                tableModel.setColumnIdentifiers(result.columnNames().toArray());
                tableModel.setRowCount(0);
                for (Object[] row : result.rows()) {
                    tableModel.addRow(row);
                }
                long duration = System.currentTimeMillis() - startTime;
                setStatus("Filas retornadas: " + result.rows().size() + " (" + duration + " ms)", new Color(0, 100, 0)); // Dark green
            } else {
                ExecutionResult result = controller.runFreeUpdate(sql);
                tableModel.setRowCount(0);
                tableModel.setColumnCount(0); // Limpiar tabla de resultados
                long duration = System.currentTimeMillis() - startTime;
                setStatus("Filas afectadas: " + result.affectedRows() + " (" + duration + " ms)", new Color(0, 100, 0)); // Dark green
            }
        } catch (SQLException e) {
            long duration = System.currentTimeMillis() - startTime;
            setStatus("Error (" + duration + " ms): " + SqlErrorTranslator.translate(e), Color.RED);
        }
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }
}
