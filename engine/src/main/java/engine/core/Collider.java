package engine.core;

import engine.gameobjects.GameObject;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Collider {
	private final GameObject parent;
	private final Bounds box;

	public Collider(GameObject parent, double posX, double posY, double width, double height) {
		this.parent = parent;
		this.box = new BoundingBox(posX, posY, width, height);
	}

	public double getMinX() {
		return box.getMinX() + parent.getPosX();
	}

	public double getMinY() {
		return box.getMinY() + parent.getPosY();
	}

	public double getMaxX() {
		return box.getMaxX() + parent.getPosX();
	}

	public double getMaxY() {
		return box.getMaxY() + parent.getPosY();
	}

	public double getCenterX() {
		return getMinX() + (getMaxX() - getMinX()) / 2;
	}

	public double getCenterY() {
		return getMinY() + (getMaxY() - getMinY()) / 2;
	}

	public double getWidth() {
		return box.getWidth();
	}

	public double getHeight() {
		return box.getHeight();
	}

	/**
	 * Calculates physical intersection of two colliders after distance change
	 *
	 * @param c      The collider to compare against
	 * @param deltaX The X distance change of the supplied collider
	 * @param deltaY The Y distance change of the supplied collider
	 * @return Bounds - the calculated intersection
	 */
	public Bounds getIntersection(Collider c, double deltaX, double deltaY) {
		double minX = Math.max(getMinX() + deltaX, c.getMinX());
		double minY = Math.max(getMinY() + deltaY, c.getMinY());
		double maxX = Math.min(getMaxX() + deltaX, c.getMaxX());
		double maxY = Math.min(getMaxY() + deltaY, c.getMaxY());
		return (minX < maxX && minY < maxY) ? new BoundingBox(minX, minY, maxX - minX, maxY - minY) : null;
	}

	/**
	 * Calculated distance of the centers of two colliders
	 *
	 * @param c The collider to calculate distance to
	 * @return double - the calculated distance
	 */
	public double getDistanceTo(Collider c) {
		double dx = this.getCenterX() - c.getCenterX();
		double dy = this.getCenterY() - c.getCenterY();
		return Math.sqrt(dx * dx + dy * dy);
	}

}
