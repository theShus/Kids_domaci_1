package result;

import app.App;
import job.ScanType;
import result.results.DirScanResult;
import result.results.Result;
import result.results.WebScanResult;

import java.util.HashMap;
import java.util.Map;

public class ResultRetriever extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Result result = App.resultQueue.take();

                if(result.getScanType() == ScanType.FILE){
                    System.err.println(((DirScanResult)result).getCorpusName() + " dodat u FS results");
                    DirScanResult dirScanResult = (DirScanResult) result;
                    App.fileScannerResults.put(dirScanResult.getCorpusName(), dirScanResult);
                }
                else if (result.getScanType() == ScanType.WEB){
                    System.err.println(((WebScanResult)result).getUrl() + " dodat u WS results");
                    WebScanResult webScanResult = (WebScanResult) result;
                    App.webScannerResults.put(webScanResult.getUrl(), webScanResult);
//                    App.webScannerResults.put("test", webScanResult);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //todo ulepsaj sve getove (da budu dinamicki)

    public void getWebResult(String url){
        Map<String, Integer> scannerResult = null;
        WebScanResult webScanResult = App.webScannerResults.get(url);

        if (webScanResult != null)
            scannerResult = webScanResult.getResult();

        if (scannerResult == null){
            System.err.println("Error loading results from directory " + url);
            return;
        }
        System.out.println(url + " = " + scannerResult);
    }

    public void getWebSummary(){
        Map<String, WebScanResult> resultsByDirectory = new HashMap<>();

        for (Map.Entry<String, WebScanResult> result: App.webScannerResults.entrySet()) {
            System.out.println(result.getKey() + " = " + result.getValue().getResult());
        }
    }

    //todo sumiraj mape

    public void getFileResult(String corpusDirName){
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.fileScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getResult();

        if (scannerResult == null){
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getFileQueryResult(String corpusDirName){
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.fileScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getQueryResult();

        if (scannerResult == null){
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getFileSummary(){//todo postavi da daje zbir za directory
        for (Map.Entry<String, DirScanResult> result: App.fileScannerResults.entrySet()) {
            System.out.println(result.getKey() + " = " + result.getValue().getResult());
        }
    }

    public void getFileQuerySummary(){
        for (Map.Entry<String, DirScanResult> result: App.fileScannerResults.entrySet()) {
            if (result.getValue().getQueryResult() == null) System.out.println(result.getKey() + " = data not ready yet");
            else System.out.println(result.getKey() + " = " + result.getValue().getQueryResult());
        }
    }

}
