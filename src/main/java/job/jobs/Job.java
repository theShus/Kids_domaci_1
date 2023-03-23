package job.jobs;

import job.ScanType;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public interface Job {
    ScanType getScanType();
}
