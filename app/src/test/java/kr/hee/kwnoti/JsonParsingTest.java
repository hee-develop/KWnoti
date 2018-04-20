package kr.hee.kwnoti;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonParsingTest {
    private String select() throws JSONException {
        String json = "{  \"message\":{    \"token\":\"bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...\", " +
                "   \"data\":{ " +
                "      \"Nick\" : \"Mario\", " +
                "      \"body\" : \"great match!\",  " +
                "      \"Room\" : \"PortugalVSDenmark\"    }  }}";

        RemoteMessage.Builder builder = new RemoteMessage.Builder("SENDER_ID@gcm.googleapis.com");
        HashMap<String, String> jMap = new HashMap<>();
        jMap.put("Apple", "Delicious");
        jMap.put("Banana", "good");
        builder.setData(jMap);
        RemoteMessage msg = builder.build();

        JSONArray jsonArray = new JSONArray(json);

        return null;
    }


    @Test
    public void test() throws Exception {
        assertEquals(select(), "2017");
    }

}
