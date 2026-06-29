package cr.ac.ucr.dds3.paraiso.db_proyecto.view;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.config.DatabaseConfig;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.data.QueryDefinition;
import cr.ac.ucr.dds3.paraiso.db_proyecto.model.metadata.TableDefinition;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.util.List;
import java.util.function.Predicate;

public class MainView extends JFrame {

    private final TableCrudPanel tableCrudPanel = new TableCrudPanel();
    private final QueryPanel queryPanel = new QueryPanel();

    public MainView() {
        super("Sistema de Gestión - bd_proyecto");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Administración de tablas", tableCrudPanel);
        tabs.addTab("Consultas avanzadas", queryPanel);
        add(tabs, BorderLayout.CENTER);
    }

    public void initialize(List<TableDefinition> tables, List<QueryDefinition> queries) {
        tableCrudPanel.initialize(tables);
        queryPanel.initialize(queries);
    }

    public void bindController(MainController controller) {
        tableCrudPanel.bindController(controller);
        queryPanel.bindController(controller);
    }

    public void refreshCurrentTable() {
        tableCrudPanel.refreshTableData();
    }

    public void showConnectionDialog(DatabaseConfig config, Predicate<DatabaseConfig> onSave) {
        ConnectionDialog.show(this, config, onSave);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean confirmAction(String title, String message) {
        int option = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return option == JOptionPane.YES_OPTION;
    }
}
