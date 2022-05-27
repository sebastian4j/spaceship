package com.github.sebastian4j.spaceship.dto;

import java.io.File;

public interface FileLoader extends Controller {
    void load(File file);
    void save(File file);
}
