package com.github.sebastian4j.spaceship.utils;

import com.github.sebastian4j.spaceship.dto.RequestResponse;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestResponseUtils {
    private static String normalize(String url) {
        var ret = url;
        if (!url.startsWith("http")) {
           ret = "http://" + url;
        }
        return ret;
    }
    public static RequestResponse sendRequest(final String url, Map<String, String> headers, boolean withBody, final String body) {
        String result = "";
        Map<String, List<String>> hr = new HashMap<>();
        try {
            var con = (HttpURLConnection) new URL(normalize(url)).openConnection();
            headers.forEach((a, b) -> con.addRequestProperty(a, b));
            if (withBody) {
                con.setDoOutput(withBody);
                con.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            }
            try (var is = con.getInputStream()) {
                final var sb = new StringBuilder();
                int lee;
                while ((lee = is.read()) != -1) {
                    sb.append((char) lee);
                }
                result = sb.toString();
            }
            hr.putAll(con.getHeaderFields());
        } catch (Exception e) {
            result = e.getMessage();
        }
        return new RequestResponse(result, hr);
    }
}
