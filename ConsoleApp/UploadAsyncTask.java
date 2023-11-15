import java.io.File;

public class UploadAsyncTask extends AsyncTask {
   protected void onPostExecute(String result) {
      System.out.println(result);
   }
   protected String doInBackground() {
      String caption = "Async Task Caption";
      String date = "2023-10-17";
      File fileToUpload = new File("C:\\Users\\trist\\OneDrive\\Desktop\\AsyncTestFile.txt");
      return new UploadClient().uploadFileWithFormData(caption, date, fileToUpload);
   }  
}
