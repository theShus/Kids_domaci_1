package scanner.web;

import app.App;
import app.PropertyStorage;
import job.jobs.WebJob;
import result.results.WebScanResult;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.*;

public class WebScanner extends Thread {

    private final UrlRefresher urlRefresher;
    private final ExecutorCompletionService<Map<String, Integer>> completionService;
    private final Map<String, Long> scannedUrls = new ConcurrentHashMap<>();
    private final long urlRefreshTime = PropertyStorage.getInstance().getUrl_refresh_time();
    private boolean running = true;


    public WebScanner() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
        urlRefresher = new UrlRefresher(scannedUrls);
        urlRefresher.setDaemon(true); //samo brise url-ove koji su vec skenirani, moze da bude lower priority thread
    }


    @Override
    public void run() {
        urlRefresher.start();

        while (running) {
            try {
                WebJob webJob = App.webJobQueue.take();
                Future<Map<String, Integer>> webScanFuture = this.completionService.submit(new WebScannerWorker(webJob.getUrl(), webJob.getHopCount()));
                if (!scannedUrls.containsKey(webJob.getUrl())) {//ako smo ga vec skenirali (nalazi se u mapi)
                    App.resultQueue.add(new WebScanResult(webJob.getUrl(), webScanFuture));
                    scannedUrls.put(webJob.getUrl(), System.currentTimeMillis() + urlRefreshTime);//poredimo system_time < system_time(old) + wait_time
                }
//                else System.err.println("url already scanned - " + webJob.getUrl());
            }
            catch (InterruptedException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }


    public void terminate(){
        running = false;
    }
    public void terminateRefresher(){
        urlRefresher.terminate();
    }
}
