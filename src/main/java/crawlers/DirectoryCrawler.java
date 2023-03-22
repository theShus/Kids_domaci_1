package crawlers;

import app.App;
import app.PropertyStorage;
import job.FileJob;
import job.ScanType;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryCrawler extends Thread {//todo proveri da li radi klasa pravilno

    private HashMap<String, Long> lastModifiedMap;
    private CopyOnWriteArrayList<String> dirsToCrawl;


    public DirectoryCrawler() {
        lastModifiedMap = new HashMap<>();
        dirsToCrawl = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                for (String path : dirsToCrawl) {
                    crawl(new File(path));
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void crawl(File inputFile) throws InterruptedException {
        File[] listFiles = inputFile.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                if (file.getName().startsWith(PropertyStorage.getInstance().getFile_corpus_prefix()))
                    System.out.println("Corpus dir: -" + file.getName());
                crawl(file);
            } else if (!file.isDirectory() && file.getParentFile().getName().startsWith(PropertyStorage.getInstance().getFile_corpus_prefix())) {
                System.out.println("Corpus file :" + file.getName());
//                addJobToQueue(new File(""));
            }
        }
    }


    private void addJobToQueue(File corpusDir) throws InterruptedException {
        String path = corpusDir.getAbsolutePath();
        long lastModified = corpusDir.lastModified();

        if (lastModifiedMap.containsKey(path)) {//ako smo vec prosli kroz dir
            if (lastModifiedMap.get(path) != lastModified) {//ako se jeste promenio u medjuvremenu
                lastModifiedMap.put(path, lastModified);
                App.jobQueue.put(new FileJob(ScanType.FILE, path));
            }
        } else {//ako nismo prosli kroz dir
            lastModifiedMap.put(path, lastModified);
            App.jobQueue.put(new FileJob(ScanType.FILE, path));
        }
    }


    public HashMap<String, Long> getLastModifiedMap() {
        return lastModifiedMap;
    }

    public void setLastModifiedMap(HashMap<String, Long> lastModifiedMap) {
        this.lastModifiedMap = lastModifiedMap;
    }

    public CopyOnWriteArrayList<String> getDirsToCrawl() {
        return dirsToCrawl;
    }

    public void setDirsToCrawl(CopyOnWriteArrayList<String> dirsToCrawl) {
        this.dirsToCrawl = dirsToCrawl;
    }
}
