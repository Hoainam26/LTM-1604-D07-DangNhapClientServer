package BTL;

import java.io.*;
import java.net.*;

public class NetworkUtil {
    public static final String SERVER_HOST = "127.0.0.1";
    public static final int SERVER_PORT = 5000;

    public static String sendRequest(String request) {
        try (Socket s = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true)) {

            out.println(request);
            // read one line response (server uses one-line responses)
            StringBuilder sb = new StringBuilder();
            String line;
            // read until socket closed or no more lines (server typically writes one line)
            if ((line = in.readLine()) != null) {
                sb.append(line);
                // For some responses (like USERS) server returns one line only
            }
            return sb.toString();
        } catch (IOException e) {
            return "ERROR;"+e.getMessage();
        }
    }
}

