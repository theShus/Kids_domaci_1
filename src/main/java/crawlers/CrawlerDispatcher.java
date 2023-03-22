package crawlers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrawlerDispatcher {

    private final DirectoryCrawler directoryCrawler;
    private final WebCrawler webCrawler;


    public CrawlerDispatcher() {
        this.directoryCrawler = new DirectoryCrawler();
        this.webCrawler = new WebCrawler();
    }

    public void startCrawler(String crawlerType, List<String> paths) throws InterruptedException {
        if (crawlerType.equals("FILE")) {
            addPathsToCrawl(paths);

            if (!directoryCrawler.isAlive())
                directoryCrawler.start();
        }
        else if (crawlerType.equals("WEB")) {
            //todo web crawler
            //webCrawler.crawl(path);

        }
    }

    public void stopCrawlers() {
        directoryCrawler.stop();
        webCrawler.stop();
    }


    private void addPathsToCrawl(List<String> paths) {
        for (String path : paths) directoryCrawler.getDirsToCrawl().add(path);
    }

}
