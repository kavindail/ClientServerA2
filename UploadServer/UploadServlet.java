import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.io.*;
import java.nio.file.*;
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
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
                try {
                        InputStream inputStream = request.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                        // Read the HTTP request headers
                        String line;

                        // Read and process the POST body (multipart form data)

                        StringBuilder requestBody = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                                requestBody.append(line).append("\r\n");
                        }

                        // Split multipart form data into parts
                        String[] parts = requestBody.toString().split("--"); // Split by boundary, no specific boundary
                                                                             // needed

                        Map<String, String> formFields = new HashMap<>();
                        String filename = null;
                        byte[] fileData = null;

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

                                        // Extract file data
                                        int dataStart = part.indexOf("\r\n\r\n") + 4;
                                        byte[] partBytes = part.substring(dataStart).getBytes();
                                        // Concatenate the part bytes
                                        if (fileData == null) {
                                                fileData = partBytes;
                                        } else {
                                                byte[] combined = new byte[fileData.length + partBytes.length];
                                                System.arraycopy(fileData, 0, combined, 0, fileData.length);
                                                System.arraycopy(partBytes, 0, combined, fileData.length,
                                                                partBytes.length);
                                                fileData = combined;
                                        }
                                }
                        }
                        System.out.println("Caption: " + formFields.get("caption"));
                        System.out.println("Date: " + formFields.get("date"));
                        System.out.println("File name: " + filename);

                        // Save the uploaded file to a folder
                        if (filename != null && fileData != null) {
                                String fileNameWithCaption = formFields.get("caption") + "_" + formFields.get("date")
                                                + "_" + filename;
                                Path filePath = Paths
                                                .get("./images/" + fileNameWithCaption);
                                System.out.println("Saving to path: " + filePath.toString());
                                Files.write(filePath, fileData);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

}