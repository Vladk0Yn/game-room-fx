module com.yanovych {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.slf4j;
    requires java.sql;
    requires com.google.gson;
    requires org.kordamp.bootstrapfx.core;
    exports com.yanovych;
    opens com.yanovych to javafx.graphics;
    opens com.yanovych.controllers;
    opens com.yanovych.entities;
    opens com.yanovych.entities.enums;
}