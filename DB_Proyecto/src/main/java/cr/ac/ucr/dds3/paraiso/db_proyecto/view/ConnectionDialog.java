package cr.ac.ucr.dds3.paraiso.db_proyecto.view;

import cr.ac.ucr.dds3.paraiso.db_proyecto.model.config.DatabaseConfig;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Predicate;

final class ConnectionDialog {

    private ConnectionDialog() {
    }

    static void show(java.awt.Component parent, DatabaseConfig config, Predicate<DatabaseConfig> onSave) {
        JDialog dialog = new JDialog((java.awt.Frame) parent, "Conexión a base de datos", true);
        JTextField urlField = new JTextField(config.getUrl(), 40);
        JTextField userField = new JTextField(config.getUser(), 20);
        JPasswordField passwordField = new JPasswordField(config.getPassword(), 20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(4, 4, 4, 4);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("URL JDBC:"), constraints);
        constraints.gridx = 1;
        panel.add(urlField, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Usuario:"), constraints);
        constraints.gridx = 1;
        panel.add(userField, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Contraseña:"), constraints);
        constraints.gridx = 1;
        panel.add(passwordField, constraints);

        JButton saveButton = new JButton("Conectar");
        saveButton.addActionListener(event -> {
            DatabaseConfig updatedConfig = new DatabaseConfig();
            updatedConfig.setUrl(urlField.getText().trim());
            updatedConfig.setUser(userField.getText().trim());
            updatedConfig.setPassword(new String(passwordField.getPassword()));
            if (onSave.test(updatedConfig)) {
                dialog.dispose();
            }
        });

        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(saveButton, constraints);

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
