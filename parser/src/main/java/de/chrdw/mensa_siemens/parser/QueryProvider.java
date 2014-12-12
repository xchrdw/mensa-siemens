package de.chrdw.mensa_siemens.parser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.val;
import lombok.extern.java.Log;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.joox.JOOX;
import org.joox.Match;

@Log
public class QueryProvider {
    private static final String AUTHCODE = "W7PZTI:suinssafdk!";
    private final String baseUri = "https://gpartner.realestate.siemens.com/webservices/locationscout.asmx/";

    @SneakyThrows({URISyntaxException.class, IOException.class})
    public Match query(String string, Map<String, ? extends Object> additionalParams) {
        val uri = new URIBuilder(baseUri + string);
        val params = new HashMap<String, Object>();
        params.put("RestaurantID", "");
        params.put("nextRecords", "");
        params.put("lastChange", "");
        params.put("AuthCode", AUTHCODE);
        params.put("timestamp", System.currentTimeMillis());
        params.putAll(additionalParams);
        for (val entry : params.entrySet()) {
            uri.addParameter(entry.getKey(), Objects.toString(entry.getValue()));
        }
        URI uriString = uri.build();
        log.info(() -> "Query: " + uriString);
        String result = Request.Get(uriString).execute().returnContent().asString();
        String content = JOOX.$(result).text();
        return JOOX.$(content);
    }
}
