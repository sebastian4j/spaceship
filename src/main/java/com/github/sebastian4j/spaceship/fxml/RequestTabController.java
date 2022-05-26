package com.github.sebastian4j.spaceship.fxml;

import com.github.sebastian4j.spaceship.BytesUtils;
import com.github.sebastian4j.spaceship.dto.FileLoader;
import com.github.sebastian4j.spaceship.dto.GUIRequest;
import com.github.sebastian4j.spaceship.dto.HttpMethods;
import com.github.sebastian4j.spaceship.utils.FXMLUtils;
import com.github.sebastian4j.spaceship.utils.RequestResponseUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class RequestTabController implements Initializable, FileLoader {
    private static final System.Logger LOGGER = System.getLogger(RequestTabController.class.getName());

    @FXML
    private Button addHeader;

    @FXML
    private TextArea bodyresult;

    @FXML
    private VBox containerHeader;

    @FXML
    private ComboBox<String> methods;

    @FXML
    private TextArea requestbody;

    @FXML
    private Button send;

    @FXML
    private Tab tab1;

    @FXML
    private Tab tab2;

    @FXML
    private TextField url;
    private ResourceBundle rb;
    private File last;

    @FXML
    void addHeader(ActionEvent event) {
        addHeader(null, null);
    }

    private void addHeader(final String k, final String v) {
        HBox hbox = new HBox();
        TextField tfk = new TextField();
        tfk.setMinWidth(280);
        tfk.setMaxWidth(280);
        if (k != null) {
            tfk.setText(k);
        }
        TextField tfv = new TextField();
        tfv.setMinWidth(410);
        tfv.setMaxWidth(410);
        if (v != null) {
            tfv.setText(v);
        }
        Button del = new Button("-");
        hbox.getChildren().add(tfk);
        hbox.getChildren().add(tfv);
        hbox.getChildren().add(del);
        del.setOnAction(ev -> containerHeader.getChildren().remove(hbox));
        containerHeader.getChildren().add(hbox);
    }

    @FXML
    void sendRequest(ActionEvent event) {
        if (url.getText() != null && !url.getText().isEmpty()) {
            Platform.runLater(() -> {
                send.setDisable(true);
                Platform.runLater(() -> {
                    bodyresult.setText(RequestResponseUtils.sendRequest(
                                url.getText(), FXMLUtils.headers(containerHeader),
                                HttpMethods.hasBody(methods.getSelectionModel().getSelectedItem()), requestbody.getText()));
                    send.setDisable(false);
                });
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.rb = rb;
        methods.setItems(FXCollections.observableArrayList(HttpMethods.list()));
        methods.getSelectionModel().selectFirst();
    }

    @Override
    public void load(File file) {
        if (file != null) {
            Platform.runLater(()-> {
                try {
                    var gui = (GUIRequest) BytesUtils.object(Files.readAllBytes(file.toPath()));
                    url.setText(gui.url());
                    methods.getSelectionModel().select(gui.type());
                    requestbody.setText(gui.body());
                    containerHeader.getChildren().clear();
                    var hh = gui.headers();
                    if (hh != null) {
                        for (var h : gui.headers()) {
                            addHeader(h.key(), h.value());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(System.Logger.Level.ERROR, "error al recuperar archivo", e);
                }});
        }
    }

    @Override
    public void save(File file) {
        last = file;
        url.requestFocus();
        if (last != null) {
            Platform.runLater(() -> {
                var gui = new GUIRequest(url.getText(),
                        FXMLUtils.headersArray(containerHeader),
                        requestbody.getText(),
                        methods.getSelectionModel().getSelectedIndex());
                FXMLUtils.saveFile(last, gui);
            });
        }
    }
}
