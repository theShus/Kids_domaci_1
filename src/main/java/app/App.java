package app;

import job.crawler.DirectoryCrawler;
import job.JobDispatcher;
import job.ScanType;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;
import logger.Logger;
import result.ResultRetriever;
import result.ClearType;
import result.results.DirScanResult;
import result.results.Result;
import result.results.WebScanResult;
import scanner.file.FileScanner;
import scanner.web.UrlRefresher;
import scanner.web.WebScanner;

import java.util.*;
import java.util.concurrent.*;


public class App {

    //Else
    private static final CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList<>();
    private static final Map<String, Long> scannedUrls = new ConcurrentHashMap<>();
    public static final Logger logger = new Logger();

    //Queues
    public static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>(100);
    public static final BlockingQueue<DirectoryJob> directoryJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<WebJob> webJobQueue = new LinkedBlockingQueue<>();

    //Results
    public static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    public static final Map<String, DirScanResult> fileScannerResults = new ConcurrentHashMap<>();
    public static final Map<String, WebScanResult> webScannerResults = new ConcurrentHashMap<>();
    public static final Map<String, Future<Map<String, Integer>>> webDomainResults = new ConcurrentHashMap<>();

    //Threads
    private static final ResultRetriever resultRetriever = new ResultRetriever();
    private static final DirectoryCrawler directoryCrawler = new DirectoryCrawler(dirsToCrawl);
    private static final FileScanner fileScanner = new FileScanner();
    public static final UrlRefresher urlRefresher = new UrlRefresher(scannedUrls);
    private static final WebScanner webScanner = new WebScanner(urlRefresher, scannedUrls);
    private static final JobDispatcher jobDispatcher = new JobDispatcher();


    public void start() {//pokrenemo sve threadove
        PropertyStorage.getInstance().loadProperties();

        resultRetriever.start();
        directoryCrawler.start();
        fileScanner.start();
        webScanner.start();
        jobDispatcher.start();

        startCommandParser();
    }


    private void startCommandParser(){
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;
        String command;

        while (true) {
            line = cli.nextLine().trim();
            tokens = line.split(" ");
            command = tokens[0];

            if (line.isEmpty()) continue;

            switch (command) {//todo dodaj cli checkove
                case "ad" -> {
                    if (tokens.length > 2){
                        System.err.println("Too many argument");
                        continue;
                    }
                    logger.cli("Added directory to scan list 🦊");
                    dirsToCrawl.add(tokens[1]);
                }
                case "aw" -> {
                    if (tokens.length > 2){
                        System.err.println("Too many argument");
                        continue;
                    }
                    logger.cli("Added url to scan list 🐺");
                    jobQueue.add(new WebJob(ScanType.WEB, tokens[1], PropertyStorage.getInstance().getHop_count()));
                }
                case "get" -> {
                    if (tokens.length < 2) {
                        System.err.println("Badly entered command ☠");
                        continue;
                    }

                    if (tokens[1].equals("-file")){
                        if (tokens[2].equals("-summary")) resultRetriever.getFileSummary();
                        else resultRetriever.getFileResult(tokens[2]);
                    }
                    else if (tokens[1].equals("-web")){
                        if (tokens[2].equals("-summary")) resultRetriever.getWebDomainSummary();
                        else resultRetriever.getDomainResult(tokens[2]);
                    }
                    else System.err.println("Badly entered command ☠");
                }
                case "query" -> {
                    if (tokens.length < 2) {
                        System.err.println("Badly entered command ☠");
                        continue;
                    }

                    if (tokens[1].equals("-file")){
                        if (tokens[2].equals("-summary")) resultRetriever.getFileQuerySummary();
                        else resultRetriever.getFileQueryResult(tokens[2]);
                    }
                    else if (tokens[1].equals("-web")){
                        if (tokens[2].equals("-summary")) resultRetriever.getWebDomainQuerySummary();
                        else resultRetriever.getWebDomainQueryResult(tokens[2]);
                    }
                    else System.err.println("Badly entered command ☠");
                }
                case "cfs" -> {
                    logger.cli("Wiping stored results from file scanning 💀");
                    fileScannerResults.clear();
                }
                case "cws" ->{
                    logger.cli("Wiping stored results from web scanning 💀");

                    if (tokens.length >= 3 && tokens[1].equals("-domain")) {
                        resultRetriever.clearCashStorage(ClearType.DOMAIN, tokens[2]);
                    }
                    else if (tokens.length == 1){
                        webScannerResults.clear();
                        webDomainResults.clear();
                        resultRetriever.clearCashStorage(ClearType.ALL, null);
                    }
                    else System.err.println("Badly entered command ☠");
                }
                case "help" -> logger.cli
                        (
                                """
                                --> ad < directory path/ directory absolute path > : add file directory to scan
                                --> aw < https://... > : add web page to scan
                                --> get\040
                                  ->  -file < corpus directory name > : get results for corpus directory
                                  ->  -file -summary : get results for all scanned corpus directories
                                  ->  -web < url domain name - example: "gatesnotes.com" > : get result for single domain
                                  ->  -web -summary : get results for all scanned domains
                                --> query\040
                                  ->    -file < corpus directory name > : get results for corpus directory
                                  ->    -file -summary : get results for all scanned corpus directories
                                  ->    -web < url domain name example: "gatesnotes.com" > : get result for single domain
                                  ->    -web -summary : get results for all scanned domains
                                --> cfs : clears file scan results
                                --> cws : clears web scan results
                                  ->   -domain < url domain name - example: "gatesnotes.com" > : brise rez za zeljeni domain
                                --> stop : stops all the threads and the app
                                """
                        );
                case "stop" -> {
                    logger.cli("Stopping the app 👋");
                    stopThreads();
                    cli.close();
                    return;
                }
                default -> System.err.println("Unknown command 😞");
            }
        }
    }


    //https://stackoverflow.com/questions/10961714/how-to-properly-stop-the-thread-in-java
    private void stopThreads(){
        resultRetriever.terminate();
        directoryCrawler.terminate();
        fileScanner.terminate();
        webScanner.terminateRefresher();
        webScanner.terminate();
        jobDispatcher.terminate();
    }


}
