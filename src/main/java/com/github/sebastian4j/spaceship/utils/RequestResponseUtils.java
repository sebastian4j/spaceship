package com.github.sebastian4j.spaceship.utils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestResponseUtils {
    public static String sendRequest(final String url, Map<String, String> headers, boolean withBody, final String body) {
        String result = "";
        try {
            var con = (HttpURLConnection) new URL(url).openConnection();
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
        } catch (Exception e) {
            result = e.getMessage();
        }
        return result;
    }
}
