package app;

import job.crawler.DirectoryCrawler;
import job.JobDispatcher;
import job.ScanType;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;
import result.ResultRetriever;
import result.results.DirScanResult;
import result.results.Result;
import result.results.WebScanResult;
import scanner.file.FileScanner;
import scanner.web.UrlRefresher;
import scanner.web.WebScanner;

import java.util.*;
import java.util.concurrent.*;


public class App {

    private static final CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList<>();

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
    private static final WebScanner webScanner = new WebScanner();
    private static final JobDispatcher jobDispatcher = new JobDispatcher();



    //todo list
    /*
        popravi get summary za filove da vraca za direktorijume sumirano
        napravi get za web rezultate
        listu skeniranih linkova, da ne ulazi opet u njih
        brisanje linkova iz ^ liste
        stop metodu
        clear file rez
        clear web rez
     */

    public void start() {
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
//        String path = null;
        List<String> attributes;

        while (true) {
            line = cli.nextLine().trim();
            tokens = line.split(" ");
            command = tokens[0];

            if (line.isEmpty()) continue;

//                attributes = generatePathList(tokens);


            switch (command) {//todo dodaj cli checkove
                case "ad" -> {
                    System.out.println("ADDED NEW DIRECTORIES");
                    dirsToCrawl.add(tokens[1]);
                }
                case "aw" -> {
                    System.out.println("ADD WEB");
                    jobQueue.add(new WebJob(ScanType.WEB, tokens[1], PropertyStorage.getInstance().getHop_count()));
                }
                case "get" -> {
                    if (tokens[1].equals("-file")){
                        if (tokens[2].equals("-summary")) resultRetriever.getFileSummary();
                        else resultRetriever.getFileResult(tokens[2]);
                    }
                    else if (tokens[1].equals("-web")){
                        if (tokens[2].equals("-summary")) resultRetriever.getWebDomainSummary();
                        else resultRetriever.getDomainResult(tokens[2]);
                    }
                }
                case "query" -> {
                    if (tokens[1].equals("-file")){
                        if (tokens[2].equals("-summary")) resultRetriever.getFileSummary();
                        else resultRetriever.getFileQueryResult(tokens[2]);
                    }
                    else if (tokens[1].equals("-web")){
                        if (tokens[2].equals("-summary")) resultRetriever.getWebDomainQuerySummary();
                        else resultRetriever.getWebDomainQueryResult(tokens[2]);
                    }
                }
                case "cfs" -> System.out.println("FILE cfs");
                case "cws" -> System.out.println("WEB cws");
                case "help" -> {//todo popravi help
                    System.out.println
                            (
                                    """
                                    --> ad <directory path/ directory absolute path> : add file directory to scan
                                    --> aw <https> : add web page to scan
                                    --> get <corpus directory name> : gets result from scanned corpus directory
                                    --> get -summary : gets all results so far
                                    --> query <corpus directory name> : gets query result from scanned corpus directory
                                    --> query -summary : gets all results that are done so far
                                    --> cfs : clears file scan results
                                    --> cws : clears web scan results
                                    --> stop : stops the app and all the threads
                                    """
                            );
                }
                case "stop" -> {
                    System.out.println("STOPPING");
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
