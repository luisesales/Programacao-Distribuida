package com.kore.marshaller;


import java.io.IOException;

import com.kore.httpmessage.HttpRequestModel;
import com.kore.httpmessage.HttpResponseModel;

public interface Marshaller {
    HttpRequestModel deserialize(String httpString) throws IOException;

    String serialize(HttpResponseModel response) throws IOException;
}