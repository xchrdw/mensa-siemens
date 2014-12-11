package de.chrdw.mensa_siemens.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.List;

import lombok.SneakyThrows;
import lombok.val;

import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import de.chrdw.mensa_siemens.parser.model.Mensa;



/**
 */
public class Parser {

    @SneakyThrows({URISyntaxException.class, IOException.class})
    List<Mensa> getMensas() {
        val uri = new URIBuilder();
        InputStream result = Request.Get(uri.build()).execute().returnContent().asStream();

        return null;
    }


}
