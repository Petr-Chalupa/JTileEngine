package engine.gameobjects;

import engine.gameobjects.entities.Entity;

@FunctionalInterface
public interface Interactable {
	void interact(Entity user);
}
