package scanner.web;

import java.util.Map;

public class UrlRefresher extends Thread {

    private Map<String, Long> scannedUrls;
    private boolean running = true;


    public UrlRefresher(Map<String, Long> scannedUrls) {
        this.scannedUrls = scannedUrls;
    }


    @Override
    public void run() {
        while (running) {

            for (Map.Entry<String, Long> scannedUrl : scannedUrls.entrySet())
                if (scannedUrl.getValue() < System.currentTimeMillis()) scannedUrls.remove(scannedUrl.getKey());

            try {
                Thread.sleep(1800000);//todo proveri da li je ovo dobro uradnjeno (svakih 30 min proverava dal url treba da se refreshuje)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void terminate(){
        running = false;
    }

}
