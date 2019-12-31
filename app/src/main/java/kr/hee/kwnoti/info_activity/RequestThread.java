package kr.hee.kwnoti.info_activity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public abstract class RequestThread extends Thread {
    private final static int TIMEOUT = 10000;
    private Document doc;

    String url;

    public RequestThread(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        try {
            doc = Jsoup.connect(url).timeout(TIMEOUT).get();
        }
        catch (IOException e) {
            doc = null;
        }
        finally {
            afterReceived(doc);
        }
    }

    public abstract void afterReceived(Document doc);
}