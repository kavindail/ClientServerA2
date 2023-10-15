public class WorkerThread extends Thread {
   private AsyncTask aTask = null;
   public WorkerThread(AsyncTask asyncTask) {
      this.aTask = asyncTask;
   }
   public void run() {
       String result = aTask.doInBackground();
       aTask.onPostExecute(result);
       
   }
}