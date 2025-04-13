module engine {
    requires transitive javafx.graphics;
    requires transitive org.json;
    requires transitive java.logging;

    exports engine;
    exports engine.core;
    exports engine.gameobjects;
    exports engine.utils;
}
