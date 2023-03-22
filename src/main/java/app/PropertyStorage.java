package app;

import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Data
public class PropertyStorage {

    private static PropertyStorage instance = null;
    private Properties properties;

    private List<String> keywords;
    private String file_corpus_prefix;
    private String dir_crawler_sleep_time;
    private long file_scanning_size_limit;
    private int hop_count;
    private long url_refresh_time;


    public PropertyStorage() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("app.properties"));
        } catch (IOException e) {
            System.err.println("Error while loading app.properties " + e.getMessage());
        }
    }

    public static PropertyStorage getInstance() {
        if (instance == null) instance = new PropertyStorage();
        return instance;
    }

    public void loadProperties() {
        file_corpus_prefix = readProperty("file_corpus_prefix");
        dir_crawler_sleep_time = readProperty("dir_crawler_sleep_time");
        keywords = new ArrayList<>(Arrays.asList(readProperty("keywords").split(",")));
        file_scanning_size_limit = Long.parseLong(readProperty("file_scanning_size_limit"));
        hop_count = Integer.parseInt(readProperty("hop_count"));
        url_refresh_time = Long.parseLong(readProperty("url_refresh_time"));
    }

    private String readProperty(String keyName) {
        System.out.println("Loading property " + keyName);
        return properties.getProperty(keyName, "Missing data");
    }


}
