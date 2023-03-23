package result.results;

import job.ScanType;

import java.util.Map;

public interface Result {
    ScanType getScanType();

    public Map<String, Integer> getResult();
    public Map<String, Integer> getQueryResult();
}
