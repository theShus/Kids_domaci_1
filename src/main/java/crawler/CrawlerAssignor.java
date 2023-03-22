package crawler;

public class CrawlerAssignor {

    private final DirectoryCrawler directoryCrawler;
    private final WebCrawler webCrawler;


    public CrawlerAssignor() {//todo prosledi im JobQueue
        this.directoryCrawler = new DirectoryCrawler();
        this.webCrawler = new WebCrawler();
    }


    public void startCrawler(String crawlerType, String path){
        if (crawlerType.equals("FILE")) directoryCrawler.crawl(path);
        else if (crawlerType.equals("WEB")) webCrawler.crawl(path);
    }

    public void stopCrawlers(){
        directoryCrawler.stop();
        webCrawler.stop();
    }

}
