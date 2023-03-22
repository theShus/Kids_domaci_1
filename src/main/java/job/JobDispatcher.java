package job;

import app.App;

public class JobDispatcher extends Thread {


    public JobDispatcher() {
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            try {
                Job job = App.jobQueue.take();
                ScanType scanType = job.getScanType();

                //todo if job is Poison

                if (job.getScanType() == ScanType.FILE) {
                    FileJob fileJob = (FileJob) job;
                    System.out.println("Startovan je file job - ");
                    //todo dodaj file job scanner

                } else if (job.getScanType() == ScanType.WEB) {
                    //todo dodaj web job scanner
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
