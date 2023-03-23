package result;

import app.App;
import job.ScanType;
import result.results.DirScanResult;
import result.results.Result;

import java.util.Map;

public class ResultRetriever extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                Result result = App.resultQueue.take();

                //todo if poison

                if(result.getScanType() == ScanType.FILE){
                    System.err.println(((DirScanResult)result).getCorpusName() + " dodat u FS results");
                    DirScanResult dirScanResult = (DirScanResult) result;
                    App.corpusScannerResults.put(dirScanResult.getCorpusName(), dirScanResult);
                }
                else if (result.getScanType() == ScanType.WEB){
                    System.err.println(((DirScanResult)result).getCorpusName() + " dodat u WS results");
                    //todo sortiraj web result pravilno
//                    WebScanResult webScanResult = (WebScanResult) result;
//                    fileScannerResults.put(webScanResult.getCorpusName(), webScanResult);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void getResult(String corpusDirName){
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.corpusScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getResult();

        if (scannerResult == null){
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getQueryResult(String corpusDirName){
        Map<String, Integer> scannerResult = null;
        DirScanResult dirScanResult = App.corpusScannerResults.get(corpusDirName);
        if (dirScanResult != null)
            scannerResult = dirScanResult.getQueryResult();

        if (scannerResult == null){
            System.err.println("Error loading results from directory " + corpusDirName);
            return;
        }
        System.out.println(corpusDirName + " = " + scannerResult);
    }

    public void getSummary(){
        for (Map.Entry<String, DirScanResult> result: App.corpusScannerResults.entrySet()) {
            System.out.println(result.getKey() + " = " + result.getValue().getResult());
        }
    }

    public void getQuerySummary(){
        for (Map.Entry<String, DirScanResult> result: App.corpusScannerResults.entrySet()) {
            if (result.getValue().getQueryResult() == null) System.out.println(result.getKey() + " = data not ready yet");
            else System.out.println(result.getKey() + " = " + result.getValue().getQueryResult());
        }
    }

}
