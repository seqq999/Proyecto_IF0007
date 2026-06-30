package cr.ac.ucr.dds3.paraiso.db_proyecto.view;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.ForeignKeyOption;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.TableRepository;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ColumnDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ColumnType;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.ForeignKeyDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.service.SqlErrorTranslator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableCrudPanel extends JPanel {

    private final JComboBox<TableDefinition> tableSelector = new JComboBox<>();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable dataTable = new JTable(tableModel);
    private final JPanel formPanel = new JPanel(new GridBagLayout());
    private final Map<String, FieldEditor> fieldEditors = new LinkedHashMap<>();
    private final Map<String, String> selectedPrimaryKeys = new LinkedHashMap<>();

    private List<TableDefinition> tables = List.of();
    private MainController controller;

    public TableCrudPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                loadSelectedRowIntoForm();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        topPanel.add(new JLabel("Tabla:"), BorderLayout.WEST);
        topPanel.add(tableSelector, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refrescar");
        refreshButton.addActionListener(event -> refreshTableData());
        topPanel.add(refreshButton, BorderLayout.EAST);

        tableSelector.addActionListener(event -> onTableChanged());

        JPanel buttonPanel = new JPanel();
        JButton newButton = new JButton("Limpiar");
        JButton insertButton = new JButton("Insertar");
        JButton updateButton = new JButton("Actualizar");
        JButton deleteButton = new JButton("Eliminar");
        newButton.addActionListener(event -> clearForm());
        insertButton.addActionListener(event -> insertRecord());
        updateButton.addActionListener(event -> updateRecord());
        deleteButton.addActionListener(event -> deleteRecord());
        buttonPanel.add(newButton);
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(dataTable),
                new JScrollPane(formPanel)
        );
        splitPane.setResizeWeight(0.55);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void initialize(List<TableDefinition> tableDefinitions) {
        this.tables = tableDefinitions;
        tableSelector.removeAllItems();
        for (TableDefinition table : tableDefinitions) {
            tableSelector.addItem(table);
        }
        if (!tableDefinitions.isEmpty()) {
            onTableChanged();
        }
    }

    public void bindController(MainController mainController) {
        this.controller = mainController;
    }

    public void refreshTableData() {
        TableDefinition selectedTable = getSelectedTable();
        if (selectedTable == null || controller == null) {
            return;
        }

        try {
            List<Map<String, Object>> rows = controller.loadTableRows(selectedTable.tableName());
            tableModel.setColumnIdentifiers(selectedTable.columns().stream()
                    .map(ColumnDefinition::name)
                    .toArray());
            tableModel.setRowCount(0);
            for (Map<String, Object> row : rows) {
                Object[] values = selectedTable.columns().stream()
                        .map(column -> TableRepository.formatValue(row.get(column.name())))
                        .toArray();
                tableModel.addRow(values);
            }
            clearForm();
        } catch (SQLException exception) {
            showError(SqlErrorTranslator.translate(exception));
        }
    }

    private void onTableChanged() {
        buildForm(getSelectedTable());
        refreshTableData();
    }

    private void buildForm(TableDefinition table) {
        formPanel.removeAll();
        fieldEditors.clear();
        selectedPrimaryKeys.clear();

        if (table == null) {
            formPanel.revalidate();
            formPanel.repaint();
            return;
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        int rowIndex = 0;
        for (ColumnDefinition column : table.columns()) {
            constraints.gridx = 0;
            constraints.gridy = rowIndex;
            formPanel.add(new JLabel(column.label() + ":"), constraints);

            FieldEditor editor = createEditor(table, column);
            fieldEditors.put(column.name(), editor);
            constraints.gridx = 1;
            formPanel.add(editor.component(), constraints);
            rowIndex++;
        }

        formPanel.revalidate();
        formPanel.repaint();
    }

    private FieldEditor createEditor(TableDefinition table, ColumnDefinition column) {
        ForeignKeyDefinition foreignKey = table.findForeignKey(column.name());
        if (foreignKey != null) {
            JComboBox<ForeignKeyOption> comboBox = new JComboBox<>();
            loadForeignKeyOptions(table.tableName(), column.name(), comboBox);
            return new FieldEditor(comboBox, FieldKind.FOREIGN_KEY);
        }
        if (column.hasAllowedValues()) {
            JComboBox<String> comboBox = new JComboBox<>(column.allowedValues().toArray(String[]::new));
            return new FieldEditor(comboBox, FieldKind.ENUM);
        }

        JTextField textField = new JTextField(24);
        textField.setToolTipText(switch (column.type()) {
            case DATE -> "Formato: yyyy-MM-dd";
            case DATETIME -> "Formato: yyyy-MM-dd HH:mm:ss";
            case TIME -> "Formato: HH:mm:ss";
            default -> "";
        });
        return new FieldEditor(textField, FieldKind.TEXT);
    }

    private void loadForeignKeyOptions(String tableName, String columnName, JComboBox<ForeignKeyOption> comboBox) {
        if (controller == null) {
            return;
        }
        try {
            comboBox.removeAllItems();
            for (ForeignKeyOption option : controller.loadForeignKeyOptions(tableName, columnName)) {
                comboBox.addItem(option);
            }
        } catch (SQLException exception) {
            showError(SqlErrorTranslator.translate(exception));
        }
    }

    private void loadSelectedRowIntoForm() {
        int selectedRow = dataTable.getSelectedRow();
        TableDefinition table = getSelectedTable();
        if (selectedRow < 0 || table == null) {
            return;
        }

        selectedPrimaryKeys.clear();
        for (ColumnDefinition column : table.columns()) {
            Object value = tableModel.getValueAt(selectedRow, table.columns().indexOf(column));
            String formattedValue = value == null ? "" : value.toString();
            FieldEditor editor = fieldEditors.get(column.name());
            if (editor == null) {
                continue;
            }
            editor.setValue(formattedValue, table, column);
            if (column.primaryKey()) {
                selectedPrimaryKeys.put(column.name(), formattedValue);
            }
        }
    }

    private void clearForm() {
        TableDefinition table = getSelectedTable();
        if (table == null) {
            return;
        }
        selectedPrimaryKeys.clear();
        dataTable.clearSelection();
        for (ColumnDefinition column : table.columns()) {
            FieldEditor editor = fieldEditors.get(column.name());
            if (editor != null) {
                editor.clear(table, column);
            }
        }
    }

    private void insertRecord() {
        if (controller == null) {
            return;
        }
        TableDefinition table = getSelectedTable();
        if (table == null) {
            return;
        }
        controller.insertRecord(table.tableName(), readFormValues(table));
    }

    private void updateRecord() {
        if (controller == null || selectedPrimaryKeys.isEmpty()) {
            showError("Seleccione un registro de la tabla para actualizar.");
            return;
        }
        TableDefinition table = getSelectedTable();
        controller.updateRecord(table.tableName(), readFormValues(table), new LinkedHashMap<>(selectedPrimaryKeys));
    }

    private void deleteRecord() {
        if (controller == null || selectedPrimaryKeys.isEmpty()) {
            showError("Seleccione un registro de la tabla para eliminar.");
            return;
        }
        TableDefinition table = getSelectedTable();
        controller.deleteRecord(table.tableName(), new LinkedHashMap<>(selectedPrimaryKeys));
    }

    private Map<String, String> readFormValues(TableDefinition table) {
        Map<String, String> values = new LinkedHashMap<>();
        for (ColumnDefinition column : table.columns()) {
            FieldEditor editor = fieldEditors.get(column.name());
            values.put(column.name(), editor == null ? "" : editor.getValue());
        }
        return values;
    }

    private TableDefinition getSelectedTable() {
        return (TableDefinition) tableSelector.getSelectedItem();
    }

    private void showError(String message) {
        javax.swing.JOptionPane.showMessageDialog(this, message, "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    private enum FieldKind {
        TEXT,
        ENUM,
        FOREIGN_KEY
    }

    private static final class FieldEditor {
        private final java.awt.Component component;
        private final FieldKind kind;

        private FieldEditor(java.awt.Component component, FieldKind kind) {
            this.component = component;
            this.kind = kind;
        }

        private java.awt.Component component() {
            return component;
        }

        private String getValue() {
            return switch (kind) {
                case TEXT -> ((JTextField) component).getText();
                case ENUM -> ((JComboBox<?>) component).getSelectedItem().toString();
                case FOREIGN_KEY -> {
                    ForeignKeyOption selected = (ForeignKeyOption) ((JComboBox<?>) component).getSelectedItem();
                    yield selected == null || selected.value() == null ? "" : selected.value().toString();
                }
            };
        }

        private void setValue(String value, TableDefinition table, ColumnDefinition column) {
            switch (kind) {
                case TEXT -> ((JTextField) component).setText(value);
                case ENUM -> ((JComboBox<String>) component).setSelectedItem(value);
                case FOREIGN_KEY -> selectForeignKeyValue(table, column, value);
                default -> {
                }
            }
        }

        private void clear(TableDefinition table, ColumnDefinition column) {
            switch (kind) {
                case TEXT -> ((JTextField) component).setText("");
                case ENUM -> ((JComboBox<String>) component).setSelectedIndex(0);
                case FOREIGN_KEY -> {
                    JComboBox<ForeignKeyOption> comboBox = (JComboBox<ForeignKeyOption>) component;
                    if (column.nullable()) {
                        comboBox.setSelectedIndex(0);
                    } else if (comboBox.getItemCount() > 0) {
                        comboBox.setSelectedIndex(0);
                    }
                }
                default -> {
                }
            }
        }

        @SuppressWarnings("unchecked")
        private void selectForeignKeyValue(TableDefinition table, ColumnDefinition column, String value) {
            JComboBox<ForeignKeyOption> comboBox = (JComboBox<ForeignKeyOption>) component;
            for (int index = 0; index < comboBox.getItemCount(); index++) {
                ForeignKeyOption option = comboBox.getItemAt(index);
                if (option.value() != null && option.value().toString().equals(value)) {
                    comboBox.setSelectedIndex(index);
                    return;
                }
            }
            if (value.isBlank() && column.nullable()) {
                comboBox.setSelectedIndex(0);
            }
        }
    }
}
