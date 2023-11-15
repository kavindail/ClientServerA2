import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class UploadServerThread extends Thread {
    private Socket socket = null;

    public UploadServerThread(Socket socket) {
        super("UploadServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream in = socket.getInputStream();
            HttpServletRequest req = new HttpServletRequest(in);
            OutputStream baos = new ByteArrayOutputStream();
            HttpServletResponse res = new HttpServletResponse(baos);
            HttpServlet httpServlet = new UploadServlet();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(in));

            String requestLine = inputReader.readLine();
            System.out.println(requestLine);
            // String line;
            // while ((line = inputReader.readLine()) != null) {
            //     System.out.println(line);
            // }
            if (requestLine.startsWith("GET /")) {
                System.out.println("GET");
                try {
                    httpServlet.doGet(req, res);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                OutputStream out = socket.getOutputStream();
                out.write(((ByteArrayOutputStream) baos).toByteArray());

                socket.close();
            }
            if (requestLine.startsWith("POST /")) {
                // This is an HTTP POST request to the /upload endpoint
                System.out.println("POST");
                // Create an instance of ProcessRequest and pass the input strseam to it
                try {
                    httpServlet.doPost(req, res);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}