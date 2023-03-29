package job.crawler;

import app.App;
import app.PropertyStorage;
import job.ScanType;
import job.jobs.DirectoryJob;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler extends Thread {

    private final HashMap<String, Long> lastModifiedMap;
    private final CopyOnWriteArrayList<String> dirsToCrawl;
    private final HashMap<String, Boolean> scannedDirs;
    private boolean running = true;


    public DirectoryCrawler(CopyOnWriteArrayList<String> dirsToCrawl) {
        scannedDirs = new HashMap<>();
        lastModifiedMap = new HashMap<>();
        this.dirsToCrawl = dirsToCrawl;
    }

    @Override
    public void run() {
        while (running) {
            try {
                for (String path : dirsToCrawl) {
                    crawl(new File(path), path);
                }
                Thread.sleep(PropertyStorage.getInstance().getDir_crawler_sleep_time());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    //nadje corpus direktorijume
    //pretovori ih u File job i prosledi lastModified-checku da stavi na JobQueue za JobDispatcher
    private void crawl(File inputFile, String path) throws InterruptedException {
        File[] listFiles = inputFile.listFiles();//todo napravi listu dirova, tako da ne saljes dir 3 puta ako ima 3 fajla

        if (listFiles == null) {
            System.err.println("Given file can not be found/opened ☠");
            dirsToCrawl.remove(path);
            return;
        }

        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(PropertyStorage.getInstance().getFile_corpus_prefix())) {
                    App.logger.logCrawler("Found corpus directory " + file.getName());
                    addJobToQueue(file);
                }
                crawl(file, path);
            }
        }
    }

    //kada nadjemo file koji nema isti lastModified kao u mapi
    //pravimo ga u job i stavljamo na queue
    private void addJobToQueue(File corpusDir) throws InterruptedException {
        String dirPath = corpusDir.getAbsolutePath();
        scannedDirs.clear();

        long lastModified;

        for (File corpusFile : corpusDir.listFiles()) {
            lastModified = corpusFile.lastModified();

            if (lastModifiedMap.getOrDefault(corpusFile.getName(), 0L) != lastModified) {
                lastModifiedMap.put(corpusFile.getName(), lastModified);
                if (!scannedDirs.containsKey(corpusDir.getName())){//ovaj check je da ne bi slali u jednom for-loop run-u isti dir 'n' puta ako ima 'n' filova u sebi
                    scannedDirs.put(corpusDir.getName(), true);
                    App.jobQueue.put(new DirectoryJob(ScanType.FILE, dirPath, corpusDir.getName()));
                    App.logger.crawlerJobDetection(corpusFile.getName() + " is un-scanned adding directory to job queue");
                }
            }
        }
    }

    public void terminate() {
        System.err.println("Terminating DirectoryCrawler thread");
        running = false;
    }

}
