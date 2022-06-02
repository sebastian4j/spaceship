package com.github.sebastian4j.spaceship.fxml;

import com.github.sebastian4j.spaceship.dto.FileLoader;
import com.github.sebastian4j.spaceship.dto.GUIRequest;
import com.github.sebastian4j.spaceship.dto.HttpMethods;
import com.github.sebastian4j.spaceship.utils.BytesUtils;
import com.github.sebastian4j.spaceship.utils.FXMLUtils;
import com.github.sebastian4j.spaceship.utils.RequestResponseUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
    @FXML
    private TabPane tabsContainer;
    @FXML
    private CheckBox onlySave;

    private ResourceBundle rb;
    private File last;
    private Stage stage;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private Task<Void> runningTask;
    private Future<?> future;
    private RequestResponseUtils requestResponseUtils = new RequestResponseUtils();
    private File saveResponse = null;
    @FXML
    void addHeader(ActionEvent event) {
        addHeader(null, null);
    }

    private void addHeader(final String k, final String v) {
        HBox hbox = new HBox();
        TextField tfk = new TextField();
        if (k != null) {
            tfk.setText(k);
        }
        TextField tfv = new TextField();
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(2);
        if (v != null) {
            tfv.setText(v);
        }
        Button del = new Button("-");
        hbox.getChildren().add(tfk);
        hbox.getChildren().add(tfv);
        hbox.getChildren().add(del);
        stage.widthProperty().addListener((observableValue, number, t1) -> {
            calculateHeaderRequestWidth(tfk, tfv, del);
        });
        del.setOnAction(ev -> containerHeader.getChildren().remove(hbox));
        containerHeader.getChildren().add(hbox);
        calculateHeaderRequestWidth(tfk, tfv, del);
    }

    private void calculateHeaderRequestWidth(TextField tfk, TextField tfv, Button del) {
        var width = stage.getWidth() - 100;
        var widthBtn = del.getWidth();
        if (widthBtn == 0.0){
            widthBtn = 23; // TODO
        }
        var widthKey = width  * 0.3 - widthBtn;
        var widthVal = width * 0.6;
        tfk.setMaxWidth(widthKey);
        tfk.setPrefWidth(widthKey);
        tfv.setMaxWidth(widthVal);
        tfv.setPrefWidth(widthVal);
    }

    private void calculateHeaderResponseWidth(TextField tfk, TextField tfv) {
        var width = stage.getWidth() - 100;
        var widthKey = width  * 0.3;
        var widthVal = width * 0.6;
        tfk.setMaxWidth(widthKey);
        tfk.setPrefWidth(widthKey);
        tfv.setMaxWidth(widthVal);
        tfv.setPrefWidth(widthVal);
    }

    private File fileToSaveRequest() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("file.chooser.save"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(rb.getString("file.chooser.all.files"), "*.*"));
        return fileChooser.showSaveDialog(null);
    }

    private void sendRequest() {
        saveResponse = null;
        var toFile = onlySave.isSelected();
        var send = new AtomicBoolean(true);
        if (toFile) {
            saveResponse = fileToSaveRequest();
            if (saveResponse == null) {
                send.set(false);
                FXMLUtils.showWarningAlert(rb.getString("only.save.error.title"), rb.getString("only.save.error.text"));
            }
        }

        runningTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    if (send.get()) {
                        LOGGER.log(System.Logger.Level.INFO, "enviando request");
                        var ini = System.currentTimeMillis();
                        var result = requestResponseUtils.sendRequest(
                                url.getText(), FXMLUtils.headers(containerHeader),
                                HttpMethods.hasBody(methods.getSelectionModel().getSelectedItem()), requestbody.getText(),
                                saveResponse);

                        var res = System.currentTimeMillis() - ini;
                        Platform.runLater(() -> {
                            textFlowPaneResponse.setText(
                                    rb.getString("milis") + ": " + res +
                                            "   bytes: " + result.bytes() +
                                            "   status: " + result.statusCode());
                            VBox vb = new VBox();
                            containerHeaderResponse.getChildren().clear();
                            result.headers().forEach((a, b) -> {
                                for (var val : b) {
                                    var key = new TextField(a);
                                    key.setMinWidth(300);
                                    var value = new TextField(val);
                                    value.setMinWidth(400);
                                    var hbox = new HBox(key, value);
                                    hbox.setSpacing(2);
                                    hbox.setAlignment(Pos.CENTER);
                                    vb.getChildren().add(hbox);
                                    calculateHeaderResponseWidth(key, value);
                                    stage.widthProperty().addListener((observableValue, number, t1) ->
                                            calculateHeaderResponseWidth(key, value)
                                    );
                                }
                            });
                            var sp = new HBox();
                            sp.setMinHeight(50); // espacio al final
                            vb.getChildren().add(sp);
                            containerHeaderResponse.getChildren().add(vb);
                            bodyresult.setText(result.body());
                            tabsContainer.getSelectionModel().select(tab2);
                        });
                        LOGGER.log(System.Logger.Level.INFO, "request finalizado");
                    }
                } catch (Exception e) {
                    LOGGER.log(System.Logger.Level.ERROR, "error al enviar request", e);
                }
                cancelRequest();
                return null;
            }
        };
       this.future = executor.submit(runningTask);
        LOGGER.log(System.Logger.Level.INFO, "tarea enviada");
    }

    private void cancelRequest() {
        Platform.runLater(() -> {
            if (runningTask != null) {
                requestResponseUtils.kill();
                try {
                    executor.awaitTermination(0, TimeUnit.SECONDS);
                } catch (Exception e) {
                    LOGGER.log(System.Logger.Level.INFO, "error en await", e);
                }
                var ls = executor.shutdownNow();
                future.cancel(true);
                LOGGER.log(System.Logger.Level.INFO, "cancelar request...");
                runningTask.cancel();
                runningTask = null;
                LOGGER.log(System.Logger.Level.INFO, "cancelado y anulado");
                executor = Executors.newSingleThreadExecutor();
            }
            send.setText(rb.getString("url.search"));
        });
    }

    private void clear() {
        Platform.runLater(() -> {
            containerHeaderResponse.getChildren().clear();
            textFlowPaneResponse.setText("");
            bodyresult.setText("");
        });
    }

    @FXML
    void sendRequest(ActionEvent event) {
        clear();
        if (runningTask != null) {
            cancelRequest();
        } else {
            if (url.getText() != null && !url.getText().isEmpty()) {
                Platform.runLater(() -> {
                    clear();
                    sendRequest();
                    send.setDisable(false);
                    send.setText(rb.getString("cancel.send"));
                });
            }
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
            this.url.setMinWidth(scrollHeaders.getWidth() - send.getWidth() - methods.getWidth() - 100);
        });
        vboxResultResponse.heightProperty().addListener((observableValue, number, t1) -> resize());
        resize();
        onlySave.setTooltip(new Tooltip(rb.getString("only.save")));
    }

    private void resize() {
        var inicial = 35;
        var height = vboxResultResponse.heightProperty().get() - inicial;
        if (bodyresult.getHeight() == 0) {
            height = vboxResultResponse.getHeight() - inicial; // TODO mejorar el parche
        }
        bodyresult.setPrefHeight(height);
    }

    @Override
    public void loadFile(File file) {
        if (file != null) {
            last = file;
            Platform.runLater(() -> {
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
                }
            });
        }
    }

    @Override
    public void saveFile(File file) {
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
    public void quickSave() {
        saveFile(last);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        resize();
    }

    @FXML
    void saveResponse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("file.chooser.save"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(rb.getString("file.chooser.all.files"), "*.*"));
        if (last != null) {
            fileChooser.setInitialDirectory(last.getParentFile());
        }
        final var destino = fileChooser.showSaveDialog(null);
        FXMLUtils.saveFile(destino, bodyresult.getText());
    }
}