package com.github.sebastian4j.spaceship.dto;

import java.io.File;

public interface FileLoader extends Controller {
    void loadFile(File file);
    void saveFile(File file);
    void quickSave();
}
