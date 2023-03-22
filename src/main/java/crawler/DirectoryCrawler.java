package crawler;

import app.PropertyStorage;

import java.io.File;

public class DirectoryCrawler {


    public void crawl(String startPath) {
        File[] listFiles = new File(startPath).listFiles();
        assert listFiles != null;

        for (File file : listFiles) {
            if (file.isDirectory()) {
//                System.out.println("Directory: " + file.getName());//todo skloni
                if (file.getName().startsWith(PropertyStorage.getInstance().getFile_corpus_prefix()))
                    addJobToQueue(file);
            }
        }
    }

    public void stop() {
        //todo
    }

    private void addJobToQueue(File corpusDir) {
        System.err.println("<CORPUS_DIR> -" + corpusDir.getName());


    }

}
