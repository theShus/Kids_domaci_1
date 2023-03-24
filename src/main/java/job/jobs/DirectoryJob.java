package job.jobs;


import job.ScanType;

public class DirectoryJob implements Job{

    private final ScanType scanType;
    private final String path;
    private final String corpusName;

    public DirectoryJob(ScanType scanType, String path, String corpusName) {
        this.scanType = scanType;
        this.path = path;
        this.corpusName = corpusName;
    }

    @Override
    public ScanType getScanType() {
        return scanType;
    }

    public String getPath() {
        return path;
    }

    public String getCorpusName() {
        return corpusName;
    }

}
