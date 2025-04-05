module ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive engine;

    opens ui to javafx.fxml;

    exports ui;
}
