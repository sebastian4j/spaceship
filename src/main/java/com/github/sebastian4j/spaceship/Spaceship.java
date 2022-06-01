package com.github.sebastian4j.spaceship;

import com.github.sebastian4j.spaceship.dto.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.Locale;
import java.util.ResourceBundle;

public class Spaceship extends Application {

    @Override
    public void stop() {
        Platform.exit();
        System.exit(Thread.MAX_PRIORITY);
    }

    @Override
    public void start(Stage ps) {
        try {
            var bundle = ResourceBundle.getBundle("label", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("request.fxml"),bundle);
            final Scene scene = new Scene(loader.load());
            Controller controller = loader.getController();
            controller.setStage(ps);
            ps.setTitle("Spaceship");
            ps.setScene(scene);
            ps.setFullScreen(false);
            ps.setAlwaysOnTop(false);
            ps.setResizable(true);
            ps.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("rocket-svgrepo-com.png")));
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            ps.setMaximized(true);
            ps.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
