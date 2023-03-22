package job;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class WebJob implements Job{



    @Override
    public ScanType getType() {
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

    @Override
    public boolean isPoison() {
        return false;
    }
}
