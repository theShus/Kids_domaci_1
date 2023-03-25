package scanner.web;

import app.App;
import app.PropertyStorage;
import job.ScanType;
import job.jobs.WebJob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Callable;

public class WebScannerWorker implements Callable<Map<String, Integer>> {

    private final String urlToScan;
    private final int hopCount;
    private final List<String> keywords = PropertyStorage.getInstance().getKeywords();

    public WebScannerWorker(String urlToScan, int hopCount) {
        this.urlToScan = urlToScan;
        this.hopCount = hopCount;
    }

    @Override
    public Map<String, Integer> call() {
        Map<String, Integer> results = new HashMap<>();

        try {
            Document website = Jsoup.connect(urlToScan).get();

            //vraca rezultat brojanja stranice gde se trenutno nalazimo
            results = countWords(website);

            //pravimo nove web job-ove od url-ova na ternutnom web site-u
            if (hopCount > 0) {
                for (String embeddedUrl : getEmbeddedUrls(website)) {
                    App.jobQueue.add(new WebJob(ScanType.WEB, embeddedUrl, hopCount - 1));
                }
            }

        } catch (Exception ex) {
            System.err.println("Unreachable url: " + urlToScan);
        }

        return results;
    }


    //prodje kroz sve reci na website-u i prebroji ih ako su keywords
    private Map<String, Integer> countWords(Document website) {
        Map<String, Integer> results = new HashMap<>();
        String word;

        for (String key : keywords) results.put(key, 0); //Stavimo sve kljuceve na  0

        Scanner websiteFile = new Scanner(website.text());

        while (websiteFile.hasNext()) {
            word = websiteFile.next();
            if (keywords.contains(word)) {
                results.put(word, results.get(word) + 1);
            }
        }
        websiteFile.close();
        return results;
    }

    //pokupimo url-ove iz sajta koji gledamo, ocistimo ih i vratimo ih u listi
    private List<String> getEmbeddedUrls(Document website) throws URISyntaxException {
        List<String> urls = new ArrayList<>();
        Elements links = website.select("a[href]");

        for (Element link : links) {
            String url = link.attr("abs:href");
            if (url == null || url.isEmpty() || url.isBlank()) continue;

            url = url.replaceAll(" ", "%20");
            url = url.replaceAll("’", "%20");
            urls.add(url);
        }
        App.logger.webScanner("ucitano je " + urls.size() + " url-ova");
        return urls;
    }

}
