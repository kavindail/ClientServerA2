import java.net.*;
import java.io.*;

public class UploadServerThread extends Thread {
    private Socket socket = null;

    public UploadServerThread(Socket socket) {
        super("UploadServerThread");
        this.socket = socket;
    }

    public void run() {
        try {
            // OutputStream out = socket.getOutputStream();

            // String htmlResponse = "<!DOCTYPE html>"
            // + "<html>"
            // + "<body>"
            // + "<p>Please fill out the form below to upload your file.</p>"
            // + "<form action='/upload' method='post' enctype='multipart/form-data'>"
            // + "Caption: <input type='text' name='caption'><br><br>"
            // + "Date: <input type='date' name='date'><br><br>"
            // + "File: <input type='file' name='file'><br><br>"
            // + "<input type='submit' value='Upload'>"
            // + "</form>"
            // + "</body>"
            // + "</html>";
            //
            //
            // String httpResponse = "HTTP/1.1 200 OK\r\n"
            // + "Content-Length: " + htmlResponse.length() + "\r\n"
            // + "Content-Type: text/html\r\n\r\n"
            // + htmlResponse;

            // out.write(httpResponse.getBytes());
            InputStream in = socket.getInputStream();
            HttpServletRequest req = new HttpServletRequest(in);
            OutputStream baos = new ByteArrayOutputStream();
            HttpServletResponse res = new HttpServletResponse(baos);
            HttpServlet httpServlet = new UploadServlet();
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(in));

            String requestLine = inputReader.readLine();

            if (requestLine != null && requestLine.startsWith("POST /")) {
                // This is an HTTP POST request to the /upload endpoint

                // Create an instance of ProcessRequest and pass the input stream to it
                System.out.print("is POST");
                httpServlet.doPost(req, res);
                
            } else {

                try {
                    httpServlet.doGet(req, res);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                OutputStream out = socket.getOutputStream();
                out.write(((ByteArrayOutputStream) baos).toByteArray());

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}