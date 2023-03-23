package job.jobs;

import job.ScanType;

public class WebJob implements Job{

    private final ScanType scanType;
    private final String url;
    private final int hopCount;

    public WebJob(ScanType scanType, String url, int hopCount) {
        this.scanType = scanType;
        this.url = url;
        this.hopCount = hopCount;
    }

    @Override
    public ScanType getScanType() {
        return scanType;
    }

    public String getUrl() {
        return url;
    }

    public int getHopCount() {
        return hopCount;
    }
}
