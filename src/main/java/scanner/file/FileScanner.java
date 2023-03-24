package scanner.file;

import app.App;
import app.PropertyStorage;
import job.jobs.DirectoryJob;
import result.results.DirScanResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileScanner extends Thread {

    //https://www.baeldung.com/java-executor-service-tutorial
    private final ExecutorCompletionService<Map<String, Integer>> completionService;//koristimo ga jer ima queue u sebi i daje nam rezulate cim se zavrsi task
    private boolean running = true;


    public FileScanner() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }


    @Override
    public void run() {
        while (running) {
            try {
                DirectoryJob directoryJob = App.directoryJobQueue.take();
                divideFiles(directoryJob.getCorpusName(), directoryJob.getPath());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void divideFiles(String corpusDirName, String corpusDirPath) {
        System.out.println("UZELI SMO DIR JOB");
        List<File> dividedFiles = new ArrayList<>();
        List<Future<Map<String, Integer>>> dirScanResults = new ArrayList<>();
        long limit = PropertyStorage.getInstance().getFile_scanning_size_limit();
        long fileLengthSum = 0;
        File[] corpusFilesToDivide = new File(corpusDirPath).listFiles();


        assert corpusFilesToDivide != null;
        for (File file : corpusFilesToDivide) {
            fileLengthSum += file.length();
            dividedFiles.add(file);

            if (fileLengthSum > limit) {
                //u listu rezultata stavimo <- rezultat dir scannera <- koji je dobio listu podeljenih filova
                dirScanResults.add(this.completionService.submit(new FileScannerWorker(dividedFiles)));

                fileLengthSum = 0;
                dividedFiles.clear();
            }
        }
        if (!dividedFiles.isEmpty()) {
            dirScanResults.add(this.completionService.submit(new FileScannerWorker(dividedFiles)));
        }
        System.out.println(corpusDirName + " dodat result queue");
        App.resultQueue.add(new DirScanResult(corpusDirName, dirScanResults));
    }

    public void terminate(){
        running = false;
    }
}
