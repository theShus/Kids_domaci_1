package result;

import app.App;
import result.results.WebScanResult;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class WebDomainSumWorker implements Callable<Map<String, Integer>> {

    String domainUrl;

    public WebDomainSumWorker(String domainUrl) {
        this.domainUrl = domainUrl;
    }

    @Override
    public Map<String, Integer> call() {
        Map<String, Integer> domainResultMap = new ConcurrentHashMap<>();

        //prodjemo kroz sve rezultate i trazimo domen koji nam treba
        for (Map.Entry<String, WebScanResult> singleWebResult : App.webScannerResults.entrySet()) {

            if (!singleWebResult.getValue().getDomain().equals(domainUrl)) continue;//ako nije onaj koji zelimo da sumiramo preskoci ga

            for (Map.Entry<String, Integer> singlePageResults : singleWebResult.getValue().getResult().entrySet()) {
                int oldValue = domainResultMap.getOrDefault(singlePageResults.getKey(), 0);
                domainResultMap.put(singlePageResults.getKey(), oldValue + singlePageResults.getValue());//sumiramo sve vrednosti istih key word-ova za zeljeni domen
            }
        }
//        System.err.println("Zavrseno racunanje za domen " + domainUrl);
        return domainResultMap;
    }

}
