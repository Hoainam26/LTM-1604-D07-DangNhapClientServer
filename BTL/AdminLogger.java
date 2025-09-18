package BTL;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminLogger {
    private final JTextArea ta;

    public AdminLogger(JTextArea ta) {
        this.ta = ta;
    }

    public void log(String msg) {
        String t = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        SwingUtilities.invokeLater(() -> {
            ta.append("[" + t + "] " + msg + "\n");
            ta.setCaretPosition(ta.getDocument().getLength());
        });
        System.out.println("[" + t + "] " + msg);
    }
}
