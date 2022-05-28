package com.github.sebastian4j.spaceship.utils;

import com.github.sebastian4j.spaceship.dto.RequestResponse;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class RequestResponseUtils {
    private static final System.Logger LOGGER = System.getLogger(RequestResponseUtils.class.getName());

    private HttpURLConnection con;
    private Thread thread;
    private static String normalize(String url) {
        var ret = url;
        if (!url.startsWith("http")) {
           ret = "http://" + url;
        }
        return ret;
    }

    public void kill() {
        if (con != null) {
            LOGGER.log(System.Logger.Level.INFO, "stop, interrupt, disconnect");
            thread.interrupt();
            con.disconnect();
            LOGGER.log(System.Logger.Level.INFO, "kill");
            con = null;
        }
    }

    public RequestResponse sendRequest(final String url, Map<String, String> headers, boolean withBody, final String body) {
        String result = "";
        final var sb = new StringBuilder();
        final var bytes = new AtomicLong();
        String[] statusCode = new String[1];
        statusCode[0] = "";
        Map<String, List<String>> hr = new HashMap<>();
        try {
            con = (HttpURLConnection) new URL(normalize(url)).openConnection();
            headers.forEach((a, b) -> con.addRequestProperty(a, b));
            if (withBody) {
                LOGGER.log(System.Logger.Level.INFO, "enviar request");
                con.setDoOutput(withBody);
                con.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));
            }
            thread = new Thread(() -> {
                try {
                    LOGGER.log(System.Logger.Level.INFO, "leer respuesta en thread");
                    statusCode[0] = String.valueOf(con.getResponseCode());
                    try (var is = con.getInputStream()) {
                        String res = "";
                        int lee;
                        LOGGER.log(System.Logger.Level.INFO, "leyendo en thread");
                        while ((lee = is.read()) != -1) {
                            sb.append((char) lee);
                        }
                        res = sb.toString();
                        bytes.set(res.getBytes(StandardCharsets.UTF_8).length);
                    }
                    hr.putAll(con.getHeaderFields());
                } catch (Exception e) {
                    sb.delete(0, sb.length());
                    String error = readError();
                    sb.append(error);
                    bytes.set(error.getBytes(StandardCharsets.UTF_8).length);
                    LOGGER.log(System.Logger.Level.ERROR, "error al obtener input stream", e);
                }
            });
            thread.setName("sender");
            thread.start();
            thread.join();
            result = sb.toString();
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "error al enviar request", e);
            result = e.getMessage() + " ~ " + readError();
        }
        return new RequestResponse(result, hr, bytes.get(), statusCode[0]);
    }

    private String readError() {
        var sb = new StringBuilder();
        if (con != null) {
            try (var is = con.getErrorStream()) {
                int lee = 0;
                while ((lee = is.read()) != -1) {
                    sb.append((char) lee);
                }
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "error al leer error stream", e);
            }
        }
        return sb.toString();
    }
}
