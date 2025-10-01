package BTL;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class LoggerUtil {
    private static final String LOG_FILE = "logs.csv";

    public synchronized static void log(String user, String action, String detail) {
        File f = new File(LOG_FILE);
        boolean exist = f.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
            if (!exist) {
                pw.println("id,user,action,detail,timestamp");
            }
            long id = System.currentTimeMillis();
            String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println(id + "," + escape(user) + "," + escape(action) + "," + escape(detail) + "," + ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace(",", "&#44;");
    }
}
