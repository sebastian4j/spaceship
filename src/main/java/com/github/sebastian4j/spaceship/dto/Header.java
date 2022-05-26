package com.github.sebastian4j.spaceship.dto;

import java.io.Serializable;

public record Header (String key, String value) implements Serializable {
}
