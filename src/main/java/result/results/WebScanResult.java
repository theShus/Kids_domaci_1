package result.results;

import job.ScanType;

import java.util.Map;

public class WebScanResult implements Result{

    private final String corpusName;


    public WebScanResult(String corpusName) {//todo promeni constructor kasnije
        this.corpusName = corpusName;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.WEB;
    }

    @Override
    public Map<String, Integer> getResult() {
        return null;
    }

    @Override
    public Map<String, Integer> getQueryResult() {
        return null;
    }

    public String getCorpusName() {
        return corpusName;
    }
}
