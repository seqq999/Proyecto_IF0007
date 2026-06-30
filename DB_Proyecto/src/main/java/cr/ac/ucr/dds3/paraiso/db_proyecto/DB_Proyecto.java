package cr.ac.ucr.dds3.paraiso.db_proyecto;

import cr.ac.ucr.dds3.paraiso.db_proyecto.controller.MainController;
import cr.ac.ucr.dds3.paraiso.db_proyecto.view.MainView;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

public class DB_Proyecto {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configuración de bordes redondeados y proporciones
                UIManager.put("Button.arc", 12);
                UIManager.put("Component.arc", 12);
                UIManager.put("ProgressBar.arc", 12);
                UIManager.put("TextComponent.arc", 12);
                
                // Configurar fuente base moderna un poco más grande
                UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
                
                // Instalar el tema Oscuro de FlatLaf (Estilo macOS)
                FlatMacDarkLaf.setup();
            } catch (Exception ignored) {
                // Fallback silencioso si falla
            }

            MainView view = new MainView();
            MainController controller = new MainController(view);
            view.bindController(controller);
            controller.initialize();
            view.setVisible(true);
        });
    }
}
