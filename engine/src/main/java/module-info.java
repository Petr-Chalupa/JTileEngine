module engine {
    requires transitive javafx.graphics;
    requires transitive org.json;

    exports engine.core;
    exports engine.gameobjects;
    exports engine.utils;
}
