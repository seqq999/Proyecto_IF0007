package cr.ac.ucr.dds3.paraiso.db_proyecto.view;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryResult;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class QueryPanel extends JPanel {

    private final JComboBox<QueryDefinition> querySelector = new JComboBox<>();
    private final JTextArea descriptionArea = new JTextArea(3, 60);
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable resultTable = new JTable(tableModel);
    private MainController controller;

    public QueryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(new JLabel("Seleccione una consulta:"), BorderLayout.NORTH);
        topPanel.add(querySelector, BorderLayout.CENTER);
        topPanel.add(new JScrollPane(descriptionArea), BorderLayout.SOUTH);

        JButton runButton = new JButton("Ejecutar consulta");
        runButton.addActionListener(event -> runSelectedQuery());
        topPanel.add(runButton, BorderLayout.EAST);

        querySelector.addActionListener(event -> updateDescription());

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);
    }

    public void initialize(List<QueryDefinition> queries) {
        querySelector.removeAllItems();
        for (QueryDefinition query : queries) {
            querySelector.addItem(query);
        }
        updateDescription();
    }

    public void bindController(MainController controller) {
        this.controller = controller;
    }

    private void updateDescription() {
        QueryDefinition selected = (QueryDefinition) querySelector.getSelectedItem();
        descriptionArea.setText(selected == null ? "" : selected.description());
    }

    private void runSelectedQuery() {
        QueryDefinition selected = (QueryDefinition) querySelector.getSelectedItem();
        if (selected == null || controller == null) {
            return;
        }
        QueryResult result = controller.runQuery(selected);
        tableModel.setColumnIdentifiers(result.columnNames().toArray());
        tableModel.setRowCount(0);
        for (Object[] row : result.rows()) {
            tableModel.addRow(row);
        }
    }
}
