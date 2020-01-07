package kr.hee.kwnoti;

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
            afterReceived(doc);
        }
        catch (IOException e) {
            catchError();
        }
    }

    public abstract void afterReceived(Document doc);

    public abstract void catchError();
}