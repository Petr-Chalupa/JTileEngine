module engine {
	requires transitive javafx.graphics;
	requires org.json;
	requires transitive java.logging;

	exports engine;
	exports engine.core;
	exports engine.gameobjects;
	exports engine.utils;
	exports engine.gameobjects.entities;
	exports engine.gameobjects.blocks;
	exports engine.gameobjects.items;
	exports engine.gameobjects.tiles;
}
