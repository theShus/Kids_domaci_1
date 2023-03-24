package result.results;

import job.ScanType;

import java.util.Map;

public interface Result {
    ScanType getScanType();

    Map<String, Integer> getResult();
    Map<String, Integer> getQueryResult();
}
