package cr.ac.ucr.dds3.paraiso.db_proyecto;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.view.MainView;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DB_Proyecto {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Keep default look and feel if system L&F is unavailable.
            }

            MainView view = new MainView();
            MainController controller = new MainController(view);
            view.bindController(controller);
            controller.initialize();
            view.setVisible(true);
        });
    }
}
