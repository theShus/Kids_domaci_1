package app;

import crawlers.DirectoryCrawler;
import crawlers.WebCrawler;
import job.JobDispatcher;
import job.jobs.DirectoryJob;
import job.jobs.Job;
import job.jobs.WebJob;
import result.results.Result;
import scanner.FileScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final CopyOnWriteArrayList<String> dirsToCrawl = new CopyOnWriteArrayList<>();

    public static BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<>(10);
    public static final BlockingQueue<DirectoryJob> directoryJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<WebJob> webJobQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<Result> resultQueue = new LinkedBlockingQueue<>();


    private static final DirectoryCrawler directoryCrawler = new DirectoryCrawler(dirsToCrawl);
    private static final WebCrawler webCrawler = new WebCrawler();
    private static final FileScanner fileScanner = new FileScanner();
    private static final JobDispatcher jobDispatcher = new JobDispatcher();
    //todo private final ResultRetriever resultRetriever = new ResultRetriever();

    public static ForkJoinPool fileScannerPool = new ForkJoinPool();



    public void start() {
        PropertyStorage.getInstance().loadProperties();

        Thread crawlerThread = new Thread(directoryCrawler, "DirectoryCrawler");
        crawlerThread.start();

        //todo
//        Thread thread = new Thread(webCrawler, "WebCrawler");
//        thread.start();

        Thread scannerThread = new Thread(fileScanner, "fileScanner");
        scannerThread.start();

        Thread dispatcherThread = new Thread(jobDispatcher, "JobDispatcher");
        dispatcherThread.start();

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
            paths = generatePathList(tokens);

            switch (command) {
                case "ad" -> {
                    System.out.println("ADDED NEW DIRECTORIES");
                    dirsToCrawl.addAll(paths);
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
                default -> System.err.println("Unknown command");
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
