package marshaller;


import httpmessage.HttpRequestModel;
import httpmessage.HttpResponseModel;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public interface Marshaller {
    HttpRequestModel deserialize(String httpString) throws IOException;

    String serialize(HttpResponseModel response) throws IOException;
}