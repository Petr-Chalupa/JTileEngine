module gameengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens gameengine to javafx.fxml;

    exports gameengine;
}
