package com.github.sebastian4j.spaceship.utils;

import com.github.sebastian4j.spaceship.dto.GUIRequest;
import com.github.sebastian4j.spaceship.dto.Header;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class FXMLUtils {
    private static final System.Logger LOGGER = System.getLogger(FXMLUtils.class.getName());

    public static void saveFile(final File output, final String content) {
        try (var fw = new FileOutputStream(output)) {
            fw.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "error al guardar archivo", e);
        }
    }

    public static void saveFile(File output, final GUIRequest gui) {
        if (output != null && gui != null) {
            try (var fw = new FileOutputStream(output)) {
                fw.write(BytesUtils.bytes(gui));
            } catch (Exception e) {
                LOGGER.log(System.Logger.Level.ERROR, "error al guardar el archivo", e);
            }
        }
    }

    public static Header[] headersArray(VBox container) {
        var headers = headers(container);
        var hh = new Header[headers.size()];
        var ai = new AtomicInteger();
        headers.forEach((a, b) -> {
            hh[ai.getAndIncrement()] = new Header(a, b);
        });
        return hh;
    }

    public static Map<String, String> headers(VBox container) {
        Map<String, String> headers = new HashMap<>();
        container.getChildren().forEach(n -> {
            var nn = (HBox) n;
            var first = new AtomicBoolean();
            String key = "";
            String value = "";
            for (Node o : nn.getChildren()) {
                if (o.getClass() == TextField.class) {
                    TextField tf = (TextField) o;
                    if (!first.get()) {
                        first.set(true);
                        key = tf.getText();
                    } else {
                        value = tf.getText();
                    }
                }
            }
            headers.put(key, value);
        });
        return headers;
    }
}
