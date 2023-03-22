package app;

import crawler.CrawlerAssignor;

import java.util.Scanner;

public class CrawlerApp {

    //jobQueue
    //jobDispatcher
    private CrawlerAssignor crawlerAssignor;
    //resultRetriever


    public CrawlerApp() {
        this.crawlerAssignor = new CrawlerAssignor();
    }

    public void start(){
        PropertyStorage.getInstance().loadProperties();
        this.startCommandParser();
    }


    public void startCommandParser() {
        Scanner cli = new Scanner(System.in);
        String line;
        String[] tokens;
        String command;
        String param;

        while(true) {
//            System.out.print("$> ");
            line = cli.nextLine().trim();
            tokens = line.split(" ");
            command = tokens[0];
            param = null;

            if (line.isEmpty()) continue;

            if(tokens.length == 2)
                param = tokens[1];
            else {
                System.err.println("Too many params");
                continue;
            }

//            int totalParams = tokens.length - 1; //todo useless

            switch(command) {
                case "ad":
                    System.out.println("ADD FILE");
//                    commander.addDirectory(param, totalParams);
                    crawlerAssignor.startCrawler("FILE", param);
                    break;
                case "aw":
                    System.out.println("ADD WEB");
//                    commander.addWeb(param, totalParams);
                    break;
                case "get":
                    System.out.println("RESULT SYNC");
//                    commander.getResultSync(param, totalParams);
                    break;
                case "query":
                    System.out.println();
//                    commander.getResultAsync(param, totalParams);
                    break;
                case "cfs":
                    System.out.println("FILE SUMMARY");
//                    commander.clearSummaryFile(totalParams);
                    break;
                case "cws":
                    System.out.println("WEB SUMMARY");
//                    commander.clearSummaryWeb(totalParams);
                    break;
                case "stop":
                    System.out.println("STOPPING");
//                    commander.stopThreads();
                    cli.close();
                    return;
                default:
                    System.err.println("Unknown command");
                    break;
            }
        }
    }

}
