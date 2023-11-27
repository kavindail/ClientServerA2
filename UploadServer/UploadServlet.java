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
        public UploadServlet() {

        }

         public void init() {
        System.out.println("Initializing UploadServlet...");
    }

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
                                + "<input type='submit' value='Submit'>"
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

                        System.out.println("in doPOST");

                        // Use a ByteArrayOutputStream to capture the entire POST body as bytes
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = request.getInputStream().read(buffer)) != -1) {
                                baos.write(buffer, 0, bytesRead);
                        }
                        
                        // Send a successful response
                        System.out.println("File processed successfully.");

                        byte[] inputData = baos.toByteArray();

                        // Convert only a portion of the byte data to string for parsing form fields
                        String dataStr = new String(inputData, StandardCharsets.UTF_8);

                        System.out.println(dataStr);

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
                                } else if (part.contains(
                                                "Content-Disposition: form-data; name=\"date\"")) {
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
                                        fileData = Arrays.copyOfRange(inputData, headerBytes,
                                                        headerBytes + part.getBytes(
                                                                        StandardCharsets.UTF_8).length
                                                                        - dataStart);
                                }
                                offset += part.length() + 2; // +2 for the -- boundary
                        }

                        filename = formFields.get("caption") + "_" + formFields.get("date") + "_"
                                        + filename;
                        System.out.println(filename);

                        // Write to the specified folder
                        String directoryPath = "./images/";
                        String filePath = directoryPath + filename;

                        // Write the file data to the specified file
                        try (FileOutputStream fos = new FileOutputStream(filePath)) {
                                fos.write(fileData);

                        } catch (IOException e) {
                                e.printStackTrace();
                        }

                } catch (IOException e) {
                        e.printStackTrace();
                }
                sendDirectoryListing(response);
        }
    private static String getValueFromPart(String part) {
            return part.split("\r\n\r\n")[1].trim();
        }

        private static void saveFile(Map<String, String> formFields, byte[] fileData) throws IOException {
            if (fileData != null) {
                String fileNameWithCaption = formFields.get("caption") + "_" + formFields.get("date")
                        + "_" + formFields.get("filename");
                Path filePath = Paths.get("./images/", fileNameWithCaption);
                System.out.println("Saving to path: " + filePath.toString());
                Files.write(filePath, fileData);
            }
        }

        private static void sendDirectoryListing(HttpServletResponse response) throws IOException {
            File imagesDir = new File("./images/");
            if (!imagesDir.exists() || !imagesDir.isDirectory()) {
                return;
            }

            File[] files = imagesDir.listFiles();
            if (files == null) return;
            
            Arrays.sort(files);

            StringBuilder htmlContent = new StringBuilder();
            htmlContent.append("<!DOCTYPE html><html><head><title>Images</title></head><body>");
            htmlContent.append("<h2>Images Directory Listing</h2>");
            htmlContent.append("<ul>");
            for (File file : files) {
                htmlContent.append("<li>").append(file.getName()).append("</li>");
            }
            htmlContent.append("</ul></body></html>");

            int contentLength = htmlContent.toString().getBytes(StandardCharsets.UTF_8).length;

            String httpResponse = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/html; charset=UTF-8\r\n"
                    + "Content-Length: " + contentLength + "\r\n"
                    + "\r\n"
                    + htmlContent;

            OutputStream outputStream = response.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(httpResponse);
            writer.flush();
            writer.close();
        }
        
}