module engine {
    requires transitive javafx.graphics;
    requires org.json;

    exports engine.core;
    exports engine.gameobjects;
    // exports engine.utils;
}
