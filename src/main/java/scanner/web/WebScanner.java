package scanner.web;

import app.App;
import job.jobs.WebJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import result.results.WebScanResult;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WebScanner extends Thread {

    private final ExecutorCompletionService<Map<String, Integer>> completionService;

    public WebScanner() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }


    @Override
    public void run() {
        while (true) {
            try {
                WebJob webJob = App.webJobQueue.take();
                Future<Map<String, Integer>> webScanFuture = this.completionService.submit(new WebScannerWorker(webJob.getUrl(), webJob.getHopCount()));
                App.resultQueue.add(new WebScanResult(webJob.getUrl(), webScanFuture));
            }
            catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

}
