package scanner;

import app.PropertyStorage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileScannerWorker implements Callable<Map<String, Integer>> {

    private final List<File> files;
    private final List<String> keywords = PropertyStorage.getInstance().getKeywords();

    public FileScannerWorker(List<File> files) {
        this.files = files;
    }

    @Override
    public Map<String, Integer> call(){
        Map<String, Integer> results = new HashMap<>();
        String text;

        // Set all keys to 0
        for (String key: keywords)
            results.put(key, 0);

        //https://www.baeldung.com/java-regex-s-splus
        for (File file: this.files) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));

                while ((text = reader.readLine()) != null) {
                    String[] words = text.split("\\s+");

                    for (String word: words) {
                        if (word != null && keywords.contains(word)) {
                            int count = results.get(word);
                            results.put(word, count + 1);
                        }
                    }
                }
                reader.close();
            } catch (Exception e) {
                System.err.println("Can not open file at - " + file.getAbsolutePath());
            }
        }

        return results;
    }
}
