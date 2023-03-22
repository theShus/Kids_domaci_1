package job;


import app.App;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class FileJob implements Job{

    private ScanType scanType;
    private String path;

    private List<File> filesToScan;
    private boolean isPoison;
    private String corpusName;
    private Future<Map<String,Integer>> jobResult;


    public FileJob(ScanType scanType, String path) {
        this.scanType = scanType;
        this.path = path;
    }

    @Override
    public ScanType getScanType() {
        return scanType;
    }

    @Override
    public String getQuery() {
        return null; //todo getQuery u file job
    }

    @Override
    public Future<Map<String, Integer>> initiate(RecursiveTask task) {
        this.jobResult = App.fileScannerPool.submit(task);
        return jobResult;
    }


//    @Override //todo ovo mozda skloni
//    public boolean isPoison() {
//        return false;
//    }
}
