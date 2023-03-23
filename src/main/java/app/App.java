package app;

import crawlers.DirectoryCrawler;
import crawlers.WebCrawler;
import job.JobDispatcher;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;
import result.ResultRetriever;
import result.results.DirScanResult;
import result.results.Result;
import result.results.WebScanResult;
import scanner.FileScanner;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList<>();

    public static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>(10);
    public static final BlockingQueue<DirectoryJob> directoryJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<WebJob> webJobQueue = new LinkedBlockingQueue<>();

    public static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();
    public static final Map<String, DirScanResult> corpusScannerResults = new HashMap<>();
    public static final Map<String, WebScanResult> webScannerResults = new HashMap<>();

    private static final ResultRetriever resultRetriever = new ResultRetriever();
    private static final DirectoryCrawler directoryCrawler = new DirectoryCrawler(dirsToCrawl);
    private static final WebCrawler webCrawler = new WebCrawler();
    private static final FileScanner fileScanner = new FileScanner();
    private static final JobDispatcher jobDispatcher = new JobDispatcher();


    public void start() {
        PropertyStorage.getInstance().loadProperties();

        resultRetriever.start();
        directoryCrawler.start();
//todo     webCrawler.start();
        fileScanner.start();
        jobDispatcher.start();

        startCommandParser();
    }



    private void startCommandParser() {
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;
        String command;
        List<String> paths;

        while(true) {
            line = cli.nextLine().trim();
            tokens = line.split(" ");
            command = tokens[0];

            if (line.isEmpty()) continue;
//            paths = generatePathList(tokens);
            String path = tokens[1];

            switch (command) {
                case "ad" -> {
                    System.out.println("ADDED NEW DIRECTORIES");
//                    dirsToCrawl.addAll(paths);
                    dirsToCrawl.add(path);

                }
                case "aw" -> System.out.println("ADD WEB");

//                    commander.addWeb(param, totalParams);
                case "get" -> System.out.println("RESULT SYNC");

//                    commander.getResultSync(param, totalParams);
                case "query" -> System.out.println();

//                    commander.getResultAsync(param, totalParams);
                case "cfs" -> System.out.println("FILE SUMMARY");

//                    commander.clearSummaryFile(totalParams);
                case "cws" -> System.out.println("WEB SUMMARY");

//                    commander.clearSummaryWeb(totalParams);
                case "stop" -> {
                    System.out.println("STOPPING");
//                    commander.stopThreads();
                    cli.close();
                    return;
                }
                default -> System.err.println("Unknown command 😞");
            }
        }
    }

    private ArrayList<String> generatePathList(String[] tokens){
        ArrayList<String> paths = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            paths.add(tokens[i]);
        }
        return paths;
    }

}
