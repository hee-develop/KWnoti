package kr.hee.kwnoti.info_activity;

import java.util.ArrayList;

public class UrlWithQuery {
    /**
     * data class for {@link UrlWithQuery}
     */
    public class UrlQuery {
        String tagName;
        String tagValue;

        public UrlQuery(String tagName, String tagValue) {
            this.tagName = tagName;
            this.tagValue = tagValue;
        }
    }

    ArrayList<UrlQuery> urlQueryArray;

    private final String baseUrl;
    String url;

    public UrlWithQuery(String baseUrl) {
        urlQueryArray = new ArrayList<>();

        this.baseUrl = baseUrl;
    }

    public void addUrlQuery(String queryTag, String queryValue) {
        urlQueryArray.add(new UrlQuery(queryTag, queryValue));

        if (queryValue != null) {
            url += "&" + queryTag + "=" + queryValue;
        }
    }
    public void addUrlQuery(String queryTag, int queryValue) {
        addUrlQuery(queryTag, String.valueOf(queryValue));
    }


    public void setUrlQuery(String queryTag, String queryValue) {
        boolean itemFound = false;

        for (int i=0; i<urlQueryArray.size(); i++) {
            if (urlQueryArray.get(i).tagName.equals(queryTag)) {
                urlQueryArray.get(i).tagValue = queryValue;
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            addUrlQuery(queryTag, queryValue);
        }
    }
    public void setUrlQuery(String queryTag, int queryValue) {
        setUrlQuery(queryTag, String.valueOf(queryValue));
    }

    public void clearUrlQuery(String queryTag) {
        for (int i=0; i<urlQueryArray.size(); i++) {
            if (urlQueryArray.get(i).tagName.equals(queryTag)) {
                urlQueryArray.remove(i);
                return;
            }
        }
    }


    public String getUrl() {
        StringBuilder url = new StringBuilder();
        url.append(new String(baseUrl));
        for (int i=0; i<urlQueryArray.size(); i++) {
            url.append("&");
            url.append(urlQueryArray.get(i).tagName);
            url.append("=");
            url.append(urlQueryArray.get(i).tagValue);
        }
        return url.toString();
    }
}
