package job;

import app.App;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;

public class JobDispatcher extends Thread {

    private boolean running = true;


    @Override
    public void run() {

        while (running) {//uzima poslove sa jobQueue i rasporedjuje ih u File ili Web queue
            try {
                Job job = App.jobQueue.take();

                if (job.getScanType() == ScanType.FILE) {
                    App.logger.jobDispatcher(((DirectoryJob) job).getCorpusName() + " added to fileJob queue");
                    App.directoryJobQueue.put((DirectoryJob) job);
                }
                else if (job.getScanType() == ScanType.WEB) {
                    App.logger.jobDispatcher(((WebJob) job).getUrl() + " added to webJob queue");
                    App.webJobQueue.put((WebJob) job);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        System.err.println("Terminating JobDispatcher thread");
        running = false;
    }
}
