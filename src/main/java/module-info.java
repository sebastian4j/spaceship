module com.github.sebastian4j.spaceship {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires org.jfxtras.styles.jmetro;
    exports com.github.sebastian4j.spaceship.fxml;
    opens com.github.sebastian4j.spaceship.fxml;
    exports com.github.sebastian4j.spaceship;
    opens com.github.sebastian4j.spaceship;
    exports com.github.sebastian4j.spaceship.utils;
    opens com.github.sebastian4j.spaceship.utils;
    exports com.github.sebastian4j.spaceship.dto;
    opens com.github.sebastian4j.spaceship.dto;
}