package com.github.sebastian4j.spaceship.dto;

import java.util.ArrayList;
import java.util.List;

public enum HttpMethods {
    GET(false), POST(true), PUT(true), DELETE(true), OPTIONS(false);

    private final boolean hasBody;

    HttpMethods(final boolean hasBody) {
        this.hasBody = hasBody;
    }

    public static List<String> list() {
        List<String> methods = new ArrayList<>();
        for (HttpMethods value : HttpMethods.values()) {
            methods.add(value.name());
        }
        return methods;
    }

    public static boolean hasBody(final String method) {
        return HttpMethods.valueOf(method).hasBody;
    }
}
