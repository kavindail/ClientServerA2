public abstract class AsyncTask {
   AsyncTask execute() { 
      this.onPreExecute();
      new WorkerThread(this).start();
      return this;
   }    
   protected abstract String doInBackground();
   protected void onPreExecute() {
   }
   protected void onPostExecute(String result) {
   }
   protected void onProgressUpdate(String progress) {
   }
   protected void PublishProgress(String progress) {
   }
}