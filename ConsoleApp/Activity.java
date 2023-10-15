import java.io.*;
public class Activity {
   public static void main(String[] args) throws IOException {
       new Activity().onCreate();
    }
   public Activity() {
   }
   public void onCreate() {
      AsyncTask UploadAsyncTask = new UploadAsyncTask().execute(); 
      System.out.println("Waiting for Callback");
      try { 
         BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
         br.readLine();
      } catch (Exception e) { }
   }
}
