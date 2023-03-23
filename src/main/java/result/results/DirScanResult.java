package result.results;

import job.ScanType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DirScanResult implements Result {

    private final String corpusName;
    private final List<Future<Map<String, Integer>>> futureResults;

    private Map<String, Integer> cachedResults = new HashMap<>();


    public DirScanResult(String corpusName, List<Future<Map<String, Integer>>> futureResults) {
        this.corpusName = corpusName;
        this.futureResults = futureResults;
    }

    public DirScanResult() {
        this.corpusName = null;
        this.futureResults = null;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.FILE;
    }

    @Override
    public Map<String, Integer> getResult() {
        Map<String, Integer> totalScanResult = new HashMap<>();

        if (!cachedResults.isEmpty()) return cachedResults; //ako imamo cash vratimo ga
        if (futureResults == null) return null;


        //prodjemo kroz listu rezultata (lista jer podeljeni filovi u fragmente)
        //svaki element liste je mapa, prodjemo kroz njih i stavimo sve vrednosti u jednu Total mapu
        for (Future<Map<String, Integer>> futureResult : futureResults) {
            try {
                Map<String, Integer> resultFragment = futureResult.get();

                //https://stackoverflow.com/questions/46898/how-do-i-efficiently-iterate-over-each-entry-in-a-java-map
                for (Map.Entry<String, Integer> entry : resultFragment.entrySet()) {
                    int oldValue = totalScanResult.getOrDefault(entry.getKey(), 0);
                    int newValue = entry.getValue();

                    totalScanResult.put(entry.getKey(), oldValue + newValue);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (!totalScanResult.isEmpty()) cachedResults = totalScanResult; //ako nemamo cahse stavimo upravo izracunati

        return totalScanResult;
    }

    @Override
    public Map<String, Integer> getQueryResult() {
        boolean scanningFinished = true;

        if (!cachedResults.isEmpty()) return cachedResults;
        if (futureResults == null) return null;

        for (Future<Map<String, Integer>> futureResult: futureResults) { //prodjemo kroz sve rezultate koji se racunaju, ako nisu gotovi vracamo null
            if (!futureResult.isDone()) {
                scanningFinished = false;
                break;
            }
        }

        if (scanningFinished) return getResult();

        return null;
    }

    public String getCorpusName() {
        return corpusName;
    }
}
