module gameengine {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires org.json;

    opens gameengine to javafx.fxml;

    exports gameengine;
}
