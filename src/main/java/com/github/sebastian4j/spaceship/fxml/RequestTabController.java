package com.github.sebastian4j.spaceship.fxml;

import com.github.sebastian4j.spaceship.BytesUtils;
import com.github.sebastian4j.spaceship.dto.Controller;
import com.github.sebastian4j.spaceship.dto.FileLoader;
import com.github.sebastian4j.spaceship.dto.GUIRequest;
import com.github.sebastian4j.spaceship.dto.HttpMethods;
import com.github.sebastian4j.spaceship.utils.FXMLUtils;
import com.github.sebastian4j.spaceship.utils.RequestResponseUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class RequestTabController implements Initializable, FileLoader {
    private static final System.Logger LOGGER = System.getLogger(RequestTabController.class.getName());

    @FXML
    private FlowPane a;

    @FXML
    private Button addHeader;

    @FXML
    private TextArea bodyresult;

    @FXML
    private VBox containerHeader;

    @FXML
    private VBox containerHeaderResponse;

    @FXML
    private ComboBox<String> methods;

    @FXML
    private TextArea requestbody;

    @FXML
    private ScrollPane scrollHeaders;

    @FXML
    private Button send;

    @FXML
    private Tab tab1;

    @FXML
    private Tab tab2;

    @FXML
    private Text textFlowPaneResponse;

    @FXML
    private TextField url;

    @FXML
    private VBox vboxurl;

    @FXML
    private HBox hboxResponse;

    @FXML
    private VBox vboxResultResponse;

    @FXML
    private BorderPane contenedor;

    private ResourceBundle rb;
    private File last;
    private Stage stage;

    @FXML
    void addHeader(ActionEvent event) {
        addHeader(null, null);
    }

    private void addHeader(final String k, final String v) {
        HBox hbox = new HBox();
        TextField tfk = new TextField();
        tfk.setMinWidth(280);
        if (k != null) {
            tfk.setText(k);
        }
        TextField tfv = new TextField();
        tfv.setMinWidth(410);
        hbox.setAlignment(Pos.CENTER);
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
                textFlowPaneResponse.setText("");
                Platform.runLater(() -> {
                    var ini = System.currentTimeMillis();
                    var result = RequestResponseUtils.sendRequest(
                            url.getText(), FXMLUtils.headers(containerHeader),
                            HttpMethods.hasBody(methods.getSelectionModel().getSelectedItem()), requestbody.getText());
                    var res = System.currentTimeMillis() - ini;
                    textFlowPaneResponse.setText(res + " " + rb.getString("milis"));
                    VBox vb = new VBox();
                    containerHeaderResponse.getChildren().clear();
                    result.headers().forEach((a, b) -> {
                        for (var val : b) {
                            var key = new TextField(a);
                            key.setMinWidth(300);
                            var value = new TextField(val);
                            value.setMinWidth(400);
                            var hbox = new HBox(key, value);
                            hbox.setAlignment(Pos.CENTER);
                            vb.getChildren().add(hbox);
                        }
                    });
                    var sp = new HBox();
                    sp.setMinHeight(50);
                    vb.getChildren().add(sp);
                    containerHeaderResponse.getChildren().add(vb);
                    bodyresult.setText(result.body());
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
        scrollHeaders.widthProperty().addListener(cl -> {
            containerHeader.setMinWidth(scrollHeaders.getWidth());
            containerHeaderResponse.setMinWidth(scrollHeaders.getWidth());
            this.url.setMinWidth(scrollHeaders.getWidth() - send.getWidth() - methods.getWidth() - 50);
        });
        vboxResultResponse.heightProperty().addListener((observableValue, number, t1) -> resize());
    }

    private void resize() {
        var height = vboxResultResponse.heightProperty().get() - 40;
        if (bodyresult.getHeight() == 0) {
            height = 419; // TODO mejorar el parche
        }
        bodyresult.setPrefHeight(height);
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

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
