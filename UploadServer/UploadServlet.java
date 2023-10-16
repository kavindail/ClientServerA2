import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.io.PrintWriter;
public class UploadServlet extends HttpServlet {

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      // Prepare the HTML content
      String htmlContent = "<!DOCTYPE html>"
              + "<html>"
              + "<body>"
              + "<p>Please fill out the form below to upload your file.</p>"
              + "<form action='/upload' method='post' enctype='multipart/form-data'>"
              + "Caption: <input type='text' name='caption'><br><br>"
              + "Date: <input type='date' name='date'><br><br>"
              + "File: <input type='file' name='file'><br><br>"
              + "<input type='submit' value='Upload'>"
              + "</form>"
              + "</body>"
              + "</html>";

      // Calculate the content length
      int contentLength = htmlContent.getBytes(StandardCharsets.UTF_8).length;

      // Build the response headers
      String httpResponse = "HTTP/1.1 200 OK\r\n"
              + "Content-Type: text/html; charset=UTF-8\r\n"
              + "Content-Length: " + contentLength + "\r\n"
              // HTTP protocol requires an empty line between headers and body
              + "\r\n"
              + htmlContent;

      // Get the output stream
      OutputStream outputStream = response.getOutputStream();
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

      // Write the response to the output stream
      writer.write(httpResponse);

      // Flush the stream to ensure all data is sent
      writer.flush();

      // Close the stream
      writer.close();
   }

}