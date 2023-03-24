package job.crawler;

import app.App;
import app.PropertyStorage;
import job.jobs.DirectoryJob;
import job.ScanType;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler extends Thread {

    private HashMap<String, Long> lastModifiedMap;
    private CopyOnWriteArrayList<String> dirsToCrawl;


    public DirectoryCrawler(CopyOnWriteArrayList<String> dirsToCrawl) {
        lastModifiedMap = new HashMap<>();
        this.dirsToCrawl = dirsToCrawl;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (String path : dirsToCrawl) {
                    crawl(new File(path));
                }
                Thread.sleep(PropertyStorage.getInstance().getDir_crawler_sleep_time());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    //nadje corpus direktorijume i pretovori ih u File job i stavi na queue
    private void crawl(File inputFile) throws InterruptedException {
        File[] listFiles = inputFile.listFiles();
        assert listFiles != null;
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(PropertyStorage.getInstance().getFile_corpus_prefix())) {
//                    System.out.println("Corpus dir: -" + file.getName());
                    addJobToQueue(file);
                }
                crawl(file);
            }
        }
    }

    private void addJobToQueue(File corpusDir) throws InterruptedException {
        String dirPath = corpusDir.getAbsolutePath();
        long lastModified = corpusDir.lastModified();

        if (lastModifiedMap.containsKey(dirPath)) {//ako smo vec prosli jednom kroz dir
            if (lastModifiedMap.get(dirPath) != lastModified) {//ako se jeste promenio u medjuvremenu
                lastModifiedMap.put(dirPath, lastModified);
                App.jobQueue.put(new DirectoryJob(ScanType.FILE, dirPath, corpusDir.getName()));
            }
        } else {//ako dir nemamo u mapi
            lastModifiedMap.put(dirPath, lastModified);
            App.jobQueue.put(new DirectoryJob(ScanType.FILE, dirPath, corpusDir.getName()));
        }
    }

    public CopyOnWriteArrayList<String> getDirsToCrawl() {
        return dirsToCrawl;
    }
}
