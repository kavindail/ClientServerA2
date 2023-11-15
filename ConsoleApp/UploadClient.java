import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

public class UploadClient {
    public UploadClient() {
    }

    public String uploadFileWithFormData(String caption, String date, File fileToUpload) {
        String response = "";

        try {
            // Create a Socket to connect to the server on localhost and port 8082
            Socket socket = new Socket("localhost", 8081);

            // Set up the input and output streams for communication with the server
            OutputStream outToServer = socket.getOutputStream();
            InputStream inFromServer = socket.getInputStream();

            // Construct the multipart form POST request
            String lines = "--";
            String boundary = "Boundary";
            String lineEnding = "\r\n";

            // Build the request body
            StringBuilder requestBody = new StringBuilder();

            // Add the caption field
            requestBody.append(lines).append(boundary).append(lineEnding);
            requestBody.append("Content-Disposition: form-data; name=\"caption\"").append(lineEnding)
                    .append(lineEnding);
            requestBody.append(caption).append(lineEnding);

            // Add the date field
            requestBody.append(lines).append(boundary).append(lineEnding);
            requestBody.append("Content-Disposition: form-data; name=\"date\"").append(lineEnding).append(lineEnding);
            requestBody.append(date).append(lineEnding);

            byte[] fileBytes = Files.readAllBytes(fileToUpload.toPath());
            String fileString = Base64.getEncoder().encodeToString(fileBytes);

            // Add the file field
            // TODO: Fix the content-type field
            requestBody.append(lines).append(boundary).append(lineEnding);
            requestBody.append("Content-Disposition: form-data; name=\"File\"; filename=\"")
                    .append(fileToUpload.getName()).append("\"").append(lineEnding);
            requestBody.append("Content-Type: text/plain").append(lineEnding).append(lineEnding);
            requestBody.append(fileString).append(lineEnding);

            requestBody.append(lines).append(boundary + "--");

            // Convert the request body to bytes
            byte[] requestBodyBytes = requestBody.toString().getBytes(StandardCharsets.UTF_8);

            // Calculate the content length
            int contentLength = requestBodyBytes.length + fileBytes.length
                    + (lines + boundary + lines + lineEnding).getBytes(StandardCharsets.UTF_8).length;

            // Construct the request headers
            String requestHeaders = "POST /upload HTTP/1.1" + lineEnding
                    + "Host: localhost:8082" + lineEnding
                    + "Content-Type: multipart/form-data; boundary=" + boundary + lineEnding
                    + "Content-Length: " + contentLength + lineEnding
                    + lineEnding;

            String httpRequest = requestHeaders + requestBody.toString();

            // String httpRequest = requestBody.toString();

            System.out.println(httpRequest);

            byte[] byteRequest = httpRequest.getBytes(StandardCharsets.UTF_8);

            // Write the complete request to the server
            outToServer.write(byteRequest);

            // Flush the output stream to ensure all data is sent
            outToServer.flush();
            
            // Close the socket
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}