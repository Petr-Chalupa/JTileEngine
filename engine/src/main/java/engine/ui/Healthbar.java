package engine.ui;

import engine.core.Camera;
import engine.gameobjects.entities.Entity;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Healthbar extends UIComponent {
	private final Entity parent;
	private final boolean showPercentage;
	private double height;

	public Healthbar(Entity parent, UIRegion region, int layer, double width, double height, double padding, boolean showPercentage) {
		super(parent, region, layer, width, height, padding);
		this.parent = parent;
		this.showPercentage = showPercentage;
	}

	public double getHeight() {
		return height;
	}

	@Override
	public Bounds calculateBounds(Bounds regionBounds) {
		if (region == UIRegion.FLOAT) {
			Camera camera = Camera.getInstance();
			double barWidth = parent.getSize() * 1.5;
			double barHeight = 8;
			double x = camera.worldToScreenX(parent.getPosX()) - ((barWidth - parent.getSize()) / 2);
			double y = camera.worldToScreenY(parent.getPosY()) - barHeight;
			return new BoundingBox(x, y, barWidth, barHeight);
		} else {
			return super.calculateBounds(regionBounds);
		}
	}

	@Override
	public void render(GraphicsContext context, double width, double height) {
		this.height = height;

		// Render background
		context.setFill(Color.DARKGRAY);
		context.fillRect(0, 0, width, height);

		// Render health
		double healthPercentage = parent.getHealth() / parent.getMaxHealth();
		if (healthPercentage > 0.6) context.setFill(Color.GREEN);
		else if (healthPercentage > 0.3) context.setFill(Color.ORANGE);
		else context.setFill(Color.RED);
		context.fillRect(0, 0, width * healthPercentage, height);

		// Render health as text
		if (showPercentage && height >= 16) {
			context.save();
			context.setFill(Color.WHITE);
			context.setTextAlign(TextAlignment.CENTER);
			context.setTextBaseline(VPos.CENTER);
			context.setFont(new Font(height - 2));
			context.fillText((int) (healthPercentage * 100) + "%", width / 2, height / 2);
			context.restore();
		}
	}

}
