package job;

import app.App;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;

public class JobDispatcher extends Thread {

    @Override
    public void run() {

        while (true) {
            try {
                Job job = App.jobQueue.take();

                //todo if job is Poison

                if (job.getScanType() == ScanType.FILE) {
                    System.err.println(((DirectoryJob) job).getCorpusName() + " dodat u FJqueue");
                    App.directoryJobQueue.put((DirectoryJob) job);
                }
                else if (job.getScanType() == ScanType.WEB) {//todo proveri dal je ovo dobro
                    System.err.println("webJob dodat u WBqueue");
                    App.webJobQueue.put((WebJob) job);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
