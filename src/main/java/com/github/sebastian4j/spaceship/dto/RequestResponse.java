package com.github.sebastian4j.spaceship.dto;

import java.util.List;
import java.util.Map;

public record RequestResponse(String body, Map<String, List<String>> headers, long bytes, String statusCode) {
}
