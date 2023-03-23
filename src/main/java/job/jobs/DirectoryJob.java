package job.jobs;


import app.App;
import job.ScanType;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class DirectoryJob implements Job{

    private final ScanType scanType;
    private final String path;
    private final String corpusName;

//    private boolean isPoison;
//    private Future<Map<String,Integer>> jobResult;


    public DirectoryJob(ScanType scanType, String path, String corpusName) {
        this.scanType = scanType;
        this.path = path;
        this.corpusName = corpusName;
    }

    @Override
    public ScanType getScanType() {
        return scanType;
    }

    @Override
    public String getQuery() {
        return null; //todo getQuery u file job
    }


    public String getPath() {
        return path;
    }

    public String getCorpusName() {
        return corpusName;
    }

//    public boolean isPoison() {
//        return isPoison;
//    }

//    public Future<Map<String, Integer>> getJobResult() {
//        return jobResult;
//    }
}