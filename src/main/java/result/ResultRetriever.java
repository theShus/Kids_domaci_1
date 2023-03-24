package result;

import app.App;
import job.ScanType;
import result.results.DirScanResult;
import result.results.Result;
import result.results.WebScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ResultRetriever extends Thread {

    private final List<String> allDomains = new ArrayList<>();
    private final ExecutorCompletionService<Map<String, Integer>> completionService;
    private final Map<String, Map<String, Integer>> webDomainResultsCash = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Integer>> webDomainQueryResultsCash = new ConcurrentHashMap<>();
    private boolean running = true;

    public ResultRetriever() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        this.completionService = new ExecutorCompletionService<>(threadPool);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Result result = App.resultQueue.take();

                if (result.getScanType() == ScanType.FILE) {
//                    System.err.println(((DirScanResult) result).getCorpusName() + " dodat u FS results");
                    DirScanResult dirScanResult = (DirScanResult) result;
                    App.fileScannerResults.put(dirScanResult.getCorpusName(), dirScanResult);
                } else if (result.getScanType() == ScanType.WEB) {
//                    System.err.println(((WebScanResult) result).getUrl() + " dodat u WS results");
                    WebScanResult webScanResult = (WebScanResult) result;
                    App.webScannerResults.put(webScanResult.getUrl(), webScanResult);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    //RESULT FUNCTIONS FOR WEB

    @Deprecated
    public void getSingleUrlResult(String url) {
        Map<String, Integer> scannerResult = null;
        WebScanResult webScanResult = App.webScannerResults.get(url);

        if (webScanResult != null)
            scannerResult = webScanResult.getResult();

        if (scannerResult == null) {
            System.err.println("Error loading results from directory " + url);
            return;
        }
        System.out.println(url + " = " + scannerResult);
    }

    public void getDomainResult(String domainUrl) {
        getAllDomains();
        try {

            if (!allDomains.contains(domainUrl)) {
                System.err.println("Domain with entered url not found in results - " + domainUrl);
                return;
            }

            if (webDomainResultsCash.containsKey(domainUrl)) {
                System.out.println("Cash rezultat za: " + domainUrl + " = " + webDomainResultsCash.get(domainUrl));
                return;
            }

            App.webDomainResults.put(domainUrl, this.completionService.submit(new WebDomainSumWorker(domainUrl)));
            webDomainResultsCash.put(domainUrl, App.webDomainResults.get(domainUrl).get());
            System.out.println("Rezultat za: " + domainUrl + " = " + App.webDomainResults.get(domainUrl).get());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getWebDomainSummary() {
        getAllDomains();

        for (String dUrl : allDomains)
            App.webDomainResults.put(dUrl, this.completionService.submit(new WebDomainSumWorker(dUrl)));
        System.out.println(">> submitted " + allDomains.size() + " domains to calculate results");

        try {
            for (Map.Entry<String, Future<Map<String, Integer>>> domainRes : App.webDomainResults.entrySet()) {
                System.out.println("Rezultat za: " + domainRes.getKey() + " = " + domainRes.getValue().get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void getWebDomainQueryResult(String domainUrl) {
        getAllDomains();
        try {

            if (!allDomains.contains(domainUrl)) {
                System.err.println("Domain with entered url not found in results - " + domainUrl);
                return;
            }

            if (webDomainQueryResultsCash.containsKey(domainUrl)) {
                System.out.println("Cash query rezultat za: " + domainUrl + " = " + webDomainQueryResultsCash.get(domainUrl));
                return;
            }

            App.webDomainResults.put(domainUrl, this.completionService.submit(new WebDomainQuerySumWorker(domainUrl)));
            webDomainQueryResultsCash.put(domainUrl, App.webDomainResults.get(domainUrl).get());
            System.out.println("Rezultat za: " + domainUrl + " = " + App.webDomainResults.get(domainUrl).get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void getWebDomainQuerySummary() {
        getAllDomains();

        for (String dUrl : allDomains)
            App.webDomainResults.put(dUrl, this.completionService.submit(new WebDomainQuerySumWorker(dUrl)));
        System.out.println(">> submitted " + allDomains.size() + " domains to query calculate results");

        try {
            for (Map.Entry<String, Future<Map<String, Integer>>> domainRes : App.webDomainResults.entrySet()) {
                System.out.println("Rezultat za: " + domainRes.getKey() + " = " + domainRes.getValue().get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    //RESULT FUNCTIONS FOR FILES
    public void getFileResult(String corpusDirName) {
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.fileScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getResult();

        if (scannerResult == null) {
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getFileQueryResult(String corpusDirName) {
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.fileScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getQueryResult();

        if (scannerResult == null) {
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getFileSummary() {//todo postavi da daje zbir za directory
        for (Map.Entry<String, DirScanResult> result : App.fileScannerResults.entrySet()) {
            System.out.println(result.getKey() + " = " + result.getValue().getResult());
        }
    }

    public void getFileQuerySummary() {
        for (Map.Entry<String, DirScanResult> result : App.fileScannerResults.entrySet()) {
            if (result.getValue().getQueryResult() == null)
                System.out.println(result.getKey() + " = data not ready yet");
            else System.out.println(result.getKey() + " = " + result.getValue().getQueryResult());
        }
    }

    private void getAllDomains() {
        allDomains.clear();
        for (Map.Entry<String, WebScanResult> result : App.webScannerResults.entrySet()) {
            if (result.getValue().getResult().isEmpty() || result.getValue().getResult() == null) continue;
            if (!allDomains.contains(result.getValue().getDomain())) allDomains.add(result.getValue().getDomain());
        }
    }

    public void clearCashStorage(ClearType clearType, String domain){
        if (clearType == ClearType.ALL){
            webDomainResultsCash.clear();
            webDomainQueryResultsCash.clear();
        }
        else if (clearType == ClearType.DOMAIN){
            webDomainResultsCash.remove(domain);
            webDomainQueryResultsCash.remove(domain);
        }
    }

    public void terminate(){
        running = false;
    }

}
