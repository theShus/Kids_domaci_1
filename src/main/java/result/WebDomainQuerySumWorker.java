package result;

import app.App;
import result.results.WebScanResult;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class WebDomainQuerySumWorker implements Callable<Map<String, Integer>> {

    String domainUrl;

    public WebDomainQuerySumWorker(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    @Override
    public Map<String, Integer> call() {
        Map<String, Integer> domainQueryResultMap = new ConcurrentHashMap<>();

        //prodjemo kroz sve rezultate i trazimo domen koji nam treba
        for (Map.Entry<String, WebScanResult> singleWebResult : App.webScannerResults.entrySet()) {

            if (!singleWebResult.getValue().getDomain().equals(domainUrl)) continue;//ako nije onaj koji zelimo da sumiramo preskoci ga

            if (singleWebResult.getValue().getQueryResult() == null) {//ako rez nije gotov samo preskocimo sabiranje
                System.err.println("still calcluating for: " + domainUrl);
                continue;
            }

            for (Map.Entry<String, Integer> singlePageResults : singleWebResult.getValue().getQueryResult().entrySet()) {
                int oldValue = domainQueryResultMap.getOrDefault(singlePageResults.getKey(), 0);
                domainQueryResultMap.put(singlePageResults.getKey(), oldValue + singlePageResults.getValue());//sumiramo sve vrednosti istih key word-ova za zeljeni domen
            }
        }
//        System.err.println("Zavrseno query racunanje za domen " + domainUrl);
        return domainQueryResultMap;
    }

}
