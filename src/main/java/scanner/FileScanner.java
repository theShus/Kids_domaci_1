package scanner;

import app.App;
import app.PropertyStorage;
import job.jobs.DirectoryJob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileScanner extends Thread {

    //https://www.baeldung.com/java-executor-service-tutorial
    private final ExecutorCompletionService<Map<String, Integer>> completionService;//koristimo ga jer ima queue u sebi i daje nam rezulate cim se zavrsi task

    public FileScanner() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }


    @Override
    public void run() {
        while (true) {
            try {
                DirectoryJob directoryJob = App.directoryJobQueue.take();

                //todo if poisonus


                divideFiles(directoryJob.getCorpusName(), directoryJob.getPath());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private void divideFiles(String corpusDirName, String dirPath) {
        List<File> dividedFiles = new ArrayList<>();
        long limit = PropertyStorage.getInstance().getFile_scanning_size_limit();
        long fileLengthSum = 0;
        File[] filesToDivide = new File(dirPath).listFiles();

        assert filesToDivide != null;
        for (File file : filesToDivide) {
            fileLengthSum += file.length();
            dividedFiles.add(file);

            if (fileLengthSum > limit) {
                countWords(dividedFiles);

                fileLengthSum = 0;
                dividedFiles.clear();
            }
        }
        if (!dividedFiles.isEmpty()) {
            countWords(dividedFiles);
        }
            //todo results
    }


    private void countWords (List<File> files/*, List<Future<Map<String, Integer>>> results*/) {

        FileScannerWorker fileScannerWorker = new FileScannerWorker(files);
        Future<Map<String, Integer>> result = this.completionService.submit(fileScannerWorker);


//        results.add(result);
    }

}
