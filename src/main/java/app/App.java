package app;

import crawlers.CrawlerDispatcher;
import job.Job;
import job.JobDispatcher;
import result.ResultRetriever;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;

public class App {

    public static BlockingQueue<Job> jobQueue;
    public static ForkJoinPool fileScannerPool;

    private JobDispatcher jobDispatcher;
    private CrawlerDispatcher crawlerDispatcher;
    public static ResultRetriever resultRetriever;

    public App() {
        jobQueue = new ArrayBlockingQueue<>(10);
        fileScannerPool = new ForkJoinPool();

        this.crawlerDispatcher = new CrawlerDispatcher();
        this.jobDispatcher = new JobDispatcher();
        resultRetriever = new ResultRetriever();
    }

    public void start() throws InterruptedException {
        PropertyStorage.getInstance().loadProperties();
        this.startCommandParser();
    }


    public void startCommandParser() throws InterruptedException {
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;
        String command;
        List<String> paths;
        String param;

        while(true) {
            line = cli.nextLine().trim();
            tokens = line.split(" ");
            command = tokens[0];
            param = null;

            if (line.isEmpty()) continue;
            paths = generatePathList(tokens);

            switch (command) {
                case "ad" -> {
                    System.out.println("ADDED NEW DIRECTORY");
                    crawlerDispatcher.startCrawler("FILE", paths);
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
