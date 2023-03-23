package result.results;

import job.ScanType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WebScanResult implements Result{

    private final String url;
    private String domain;
    private final Future<Map<String, Integer>> futureResult;
    private Map<String, Integer> cachedResults = new HashMap<>();

    public WebScanResult(String url, Future<Map<String, Integer>> futureResult) throws URISyntaxException {
        this.url = url;
        this.futureResult = futureResult;

        this.domain = getDomainName(url);
    }


    @Override
    public Map<String, Integer> getResult() {
        Map<String, Integer> result = new HashMap<>();

        if (!cachedResults.isEmpty()) return cachedResults; //ako imamo cash vratimo ga
        if (futureResult == null) return null;

        try {
            result = futureResult.get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (!result.isEmpty()) cachedResults = result;

        return result;
    }

    @Override
    public Map<String, Integer> getQueryResult() {
        if (!cachedResults.isEmpty()) return cachedResults;
        if (futureResult == null) return null;

        if (!futureResult.isDone()) return null;
        else return getResult();
    }

    private String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        if (domain == null) return "";
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }

    @Override
    public ScanType getScanType() {
        return ScanType.WEB;
    }

    public String getUrl() {
        return url;
    }

    public String getDomain() {
        return domain;
    }
}
