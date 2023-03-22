package job;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class WebJob implements Job{



    @Override
    public ScanType getScanType() {
        return null;
    }

    @Override
    public String getQuery() {
        return null;
    }

    @Override
    public Future<Map<String, Integer>> initiate(RecursiveTask<?> task) {
        return null;
    }

//    @Override //todo ovo mozda skloni
//    public boolean isPoison() {
//        return false;
//    }
}
