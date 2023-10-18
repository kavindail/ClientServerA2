import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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
                BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                // Write the response to the output stream
                writer.write(httpResponse);

                // Flush the stream to ensure all data is sent
                writer.flush();

                // Close the stream
                writer.close();
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) {
                try {
                        // Use a ByteArrayOutputStream to capture the entire POST body as bytes
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = request.getInputStream().read(buffer)) != -1) {
                                baos.write(buffer, 0, bytesRead);
                        }
                        byte[] inputData = baos.toByteArray();

                        // Convert only a portion of the byte data to string for parsing form fields
                        String dataStr = new String(inputData, StandardCharsets.UTF_8);

                        // Split multipart form data into parts using a basic split (this might still be
                        // problematic, see earlier explanation)
                        String[] parts = dataStr.split("--");

                        Map<String, String> formFields = new HashMap<>();
                        String filename = null;
                        byte[] fileData = null;

                        int offset = 0; // Use this offset to locate the byte data of the image

                        for (String part : parts) {
                                if (part.contains("Content-Disposition: form-data; name=\"caption\"")) {
                                        String caption = part.split("\r\n\r\n")[1].trim();
                                        formFields.put("caption", caption);
                                } else if (part.contains("Content-Disposition: form-data; name=\"date\"")) {
                                        String date = part.split("\r\n\r\n")[1].trim();
                                        formFields.put("date", date);
                                } else if (part.contains(
                                                "Content-Disposition: form-data; name=\"file\"; filename=\"")) {
                                        // Extract the file name
                                        filename = part.split("filename=\"")[1].split("\"")[0];

                                        // Determine where the file data starts in the byte array
                                        int dataStart = part.indexOf("\r\n\r\n") + 4;
                                        int headerBytes = dataStr.substring(0, offset + dataStart)
                                                        .getBytes(StandardCharsets.UTF_8).length;

                                        // Extract file data bytes
                                        fileData = Arrays.copyOfRange(inputData, headerBytes, headerBytes
                                                        + part.getBytes(StandardCharsets.UTF_8).length - dataStart);
                                }
                                offset += part.length() + 2; // +2 for the -- boundary
                        }

                        filename = formFields.get("caption") + "_" + formFields.get("date") + "_" + filename;
                        System.out.println(filename);

                        // Write to the specified folder
                        String directoryPath = "./images/";
                        String filePath = directoryPath + filename;
                        if (filename != "null_null_null") {// Write the file data to the specified file
                                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                                        fos.write(fileData);
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                                PrintWriter out = new PrintWriter(response.getOutputStream(), true);

                                // Write the HTTP status line and headers
                                out.println("GET HTTP/1.1 200 OK");
                        }

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}