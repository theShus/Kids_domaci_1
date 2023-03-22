package job;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public interface Job {
    ScanType getScanType();
    String getQuery();
    Future<Map<String, Integer>> initiate(RecursiveTask<?> task);
    //boolean isPoison(); todo ovo mozda skloni
}
