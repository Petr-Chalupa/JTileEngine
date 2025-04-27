package engine.gameobjects.items;

import engine.gameobjects.entities.Entity;

@FunctionalInterface
public interface ItemCommand {
	boolean execute(Entity user);
}
