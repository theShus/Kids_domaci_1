package job;

import app.App;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;

public class JobDispatcher extends Thread {

    private boolean running = true;


    @Override
    public void run() {

        while (running) {
            try {
                Job job = App.jobQueue.take();

                if (job.getScanType() == ScanType.FILE) {
                    System.err.println(((DirectoryJob) job).getCorpusName() + " dodat u FJqueue");
//                    App.directoryJobQueue.put((DirectoryJob) job);
                }
                else if (job.getScanType() == ScanType.WEB) {//todo proveri dal je ovo dobro
//                    System.err.println(((WebJob) job).getUrl() + " dodat u WBqueue");
                    App.webJobQueue.put((WebJob) job);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        running = false;
    }
}
