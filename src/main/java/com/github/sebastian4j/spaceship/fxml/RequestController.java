package com.github.sebastian4j.spaceship.fxml;

import com.github.sebastian4j.spaceship.dto.Controller;
import com.github.sebastian4j.spaceship.dto.FileLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestController implements Initializable, Controller {
    private static final System.Logger LOGGER = System.getLogger(RequestController.class.getName());
    private Stage st;
    private ResourceBundle bundle;
    private Map<Tab, FileLoader> tabsActivas;
    private File last;
    @FXML
    private TabPane tabPane;

    @FXML
    void fileClose(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void fileOpen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("file.chooser.open"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(bundle.getString("file.chooser.all.files"), "*.*"));
        if (last != null) {
            fileChooser.setInitialDirectory(last.getParentFile());
        }
        last = fileChooser.showOpenDialog(null);
        getActiveFileLoader().ifPresent(c -> c.load(last));

    }

    private Optional<FileLoader> getActiveFileLoader() {
        FileLoader cargador = null;
        if (!tabsActivas.isEmpty()) {
            for (var a : tabsActivas.entrySet()) {
                if (a.getKey().isSelected()) {
                    cargador = a.getValue();
                    break;
                }
            }
        }
        return Optional.ofNullable(cargador);
    }

    private Optional<Tab> getActiveTab() {
        Tab tab = null;
        if (!tabsActivas.isEmpty()) {
            for (var a : tabsActivas.entrySet()) {
                if (a.getKey().isSelected()) {
                    tab = a.getKey();
                    break;
                }
            }
        }
        return Optional.ofNullable(tab);
    }

    @FXML
    void fileSaveAs(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(bundle.getString("file.chooser.save"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(bundle.getString("file.chooser.all.files"), "*.*"));
        if (last != null) {
            fileChooser.setInitialDirectory(last.getParentFile());
        }
        last = fileChooser.showSaveDialog(null);
        getActiveFileLoader().ifPresent(c -> c.save(last));
    }

    @FXML
    void newTab(ActionEvent event) {
        var tab = new Tab();
        tabPane.getTabs().add(tab);
        var contenido =  new FXMLLoader(getClass().getClassLoader().getResource("requestTab.fxml"), bundle);
        try {
            tab.setContent(contenido.load());
            FileLoader controller = contenido.getController();
            tabsActivas.put(tab, controller);
        } catch (Exception e) {
            LOGGER.log(System.Logger.Level.ERROR, "error al cargar tab", e);
        }
        tabsTitles();
    }

    private void tabsTitles() {
        var ai = new AtomicInteger();
        tabsActivas.forEach((a, b) -> {
            a.setText("Request: " + ai.incrementAndGet());
        });
    }

    @Override
    public void setStage(Stage stage) {
        this.st = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bundle = resourceBundle;
        tabsActivas = new LinkedHashMap<>();
        newTab(null);
    }


    @FXML
    void closeTab(ActionEvent event) {
        getActiveTab().ifPresent(tab -> {
            tabsActivas.remove(tab);
            tabPane.getTabs().remove(tab);
        });
        tabsTitles();
    }
}