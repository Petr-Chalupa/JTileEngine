package engine.core;

import engine.gameobjects.Entity;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Healthbar {
	public enum HealthbarType {
		LEFT_BOTTOM, FLOAT
	}

	private final Entity parent;
	private final HealthbarType type;

	public Healthbar(Entity parent, HealthbarType type) {
		this.parent = parent;
		this.type = type;
	}

	public void render(GraphicsContext context) {
		double width = 0;
		double height = 0;
		double dx = 0;
		double dy = 0;
		if (type == HealthbarType.LEFT_BOTTOM) {
			width = 200;
			height = 20;
			dx = 30;
			dy = (int) context.getCanvas().getHeight() - 50;
		} else if (type == HealthbarType.FLOAT) {
			width = parent.getSize() * 1.5;
			height = 8;
			Camera camera = Camera.getInstance();
			dx = camera.worldToScreenX(parent.getPosX()) - ((width - parent.getSize()) / 2);
			dy = camera.worldToScreenY(parent.getPosY()) - height - 5;
		}

		double healthPercentage = (double) parent.getHealth() / parent.getMaxHealth();

		// Render background
		context.setFill(Color.DARKGRAY);
		context.fillRect(dx, dy, width, height);

		// Render health
		if (healthPercentage > 0.6) context.setFill(Color.GREEN);
		else if (healthPercentage > 0.3) context.setFill(Color.ORANGE);
		else context.setFill(Color.RED);
		context.fillRect(dx, dy, width * healthPercentage, height);

		if (type == HealthbarType.LEFT_BOTTOM) {
			// Render health as text
			context.save();
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			context.setFont(new Font(height - 2));
			context.fillText((int) (healthPercentage * 100) + "%", dx + width / 2, dy + height / 2);
			context.restore(); // Reset
		}
	}

}
