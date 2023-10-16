import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ProcessRequest {
    public static void handleRequest(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Read the HTTP request headers
            String line;
            
            // Read and process the POST body (multipart form data)

            StringBuilder requestBody = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                requestBody.append(line).append("\r\n");
            }

            // Split multipart form data into parts
            String[] parts = requestBody.toString().split("--"); // Split by boundary, no specific boundary needed

            Map<String, String> formFields = new HashMap<>();
            String filename = null;
            byte[] fileData = null;

            for (String part : parts) {
                if (part.contains("Content-Disposition: form-data; name=\"caption\"")) {
                    String caption = part.split("\r\n\r\n")[1].trim();
                    System.out.println("Caption: " + caption);
                    formFields.put("caption", caption);
                } else if (part.contains("Content-Disposition: form-data; name=\"date\"")) {
                    String date = part.split("\r\n\r\n")[1].trim();
                    formFields.put("date", date);
                } else if (part.contains("Content-Disposition: form-data; name=\"file\"; filename=\"")) {
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
                        System.arraycopy(partBytes, 0, combined, fileData.length, partBytes.length);
                        fileData = combined;
                    }
                }
            }
            System.out
                    .println(formFields.get("caption") + "_" + formFields.get("date") + "_" + filename);

            // Save the uploaded file to a folder
            if (filename != null && fileData != null) {
                String fileNameWithCaption = formFields.get("caption") + "_" + formFields.get("date") + "_" + filename;
                Path filePath = Paths
                        .get("C:\\tomcat\\webapps\\ClientServerA2-1\\UploadServer\\images\\" + fileNameWithCaption);
                System.out.println("Saving to path: " + filePath.toString());
                Files.write(filePath, fileData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
