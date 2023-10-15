import java.io.*;
import java.net.*;
public class UploadClient {
    public UploadClient() { }
    public String uploadFile() {
        String listing = "";
        try {
            Socket socket = new Socket("localhost", 8999);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();
            FileInputStream fis = new FileInputStream("AndroidLogo.png");
            byte[] bytes = fis.readAllBytes();
            out.write(bytes);
            socket.shutdownOutput();
            fis.close();
            System.out.println("Came this far\n");
            String filename = "";
            while ((filename = in.readLine()) != null) {
                listing += filename;
            }
            socket.shutdownInput();
        } catch (Exception e) {
            System.err.println(e);
        }
        return listing;
    }
}