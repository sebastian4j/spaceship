package com.github.sebastian4j.spaceship.dto;

import java.io.Serializable;

public record GUIRequest(String url, Header[] headers, String body, int type) implements Serializable {
}
