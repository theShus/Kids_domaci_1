package logger;

public class Logger {

    //Standard
    private static final boolean cli = true;
    private static final boolean propertyStorage = true;

    //Crawler
    private static final boolean logCrawler = false;
    private static final boolean crawlerJobDetection = true;

    //Threads / Jobs
    private static final boolean jobDispatcher = false;
    private static final boolean fileScanner = true;
    private static final boolean webScanner = false;
    private static final boolean urlAlreadyScanned = false;

    //Results
    private static final boolean resultRetrieverSorter = false;
    private static final boolean resultRetriever = true;



    public void cli(String message){
        if (!cli) return;
        System.out.println(Color.WHITE_BOLD + message + Color.RESET);
    }

    public void urlAlreadyScanned(String message){
        if (!urlAlreadyScanned) return;
        System.out.println(Color.RED_BOLD + message + Color.RESET);
    }

    public void logCrawler(String message){
        if (!logCrawler) return;
        System.out.println(Color.YELLOW + message + Color.RESET);
    }

    public void crawlerJobDetection(String message){
        if (!crawlerJobDetection) return;
        System.out.println(Color.YELLOW + message + Color.RESET);
    }

    public void jobDispatcher(String message){
        if (!jobDispatcher) return;
        System.out.println(Color.CYAN + message + Color.RESET);
    }

    public void resultRetrieverSorter(String message){
        if (!resultRetrieverSorter) return;
        System.out.println(Color.GREEN + message + Color.RESET);
    }

    public void resultRetriever(String message){
        if (!resultRetriever) return;
        System.out.println(Color.MAGENTA + message + Color.RESET);
    }

    public void fileScanner(String message){
        if (!fileScanner) return;
        System.out.println(Color.BLUE + message + Color.RESET);
    }

    public void webScanner(String message){
        if (!webScanner) return;
        System.out.println(Color.BLUE + message + Color.RESET);
    }

    public void propertyStorage(String message){
        if (!propertyStorage) return;
        System.out.println(Color.WHITE_BOLD + message + Color.RESET);
    }

}
