package marshaller;


import httpmessage.HttpRequest;
import httpmessage.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface Marshaller {
    HttpRequest deserialize(String httpString) throws IOException;

    String serialize(HttpResponse response) throws IOException;
}