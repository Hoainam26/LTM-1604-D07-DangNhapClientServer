package BTL;

import javax.swing.*;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame();
            lf.setVisible(true);
        });
    }
}
