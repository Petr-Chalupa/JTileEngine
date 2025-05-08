package engine.ui;

import engine.Engine;
import engine.gameobjects.entities.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class PlayerStats extends UIComponent {
	private final Player parent;
	private final Healthbar healthbar;
	private final double statRowHeight = 30;

	public PlayerStats(Player parent, UIRegion region, int layer, double width, double height, double padding) {
		super(parent, region, layer, width, height, padding);
		this.parent = parent;
		this.healthbar = new Healthbar(parent, UIRegion.BOTTOM_LEFT, layer, width, 0.1, 5, true);

		Engine.getInstance().getUIManager().addComponent(this.healthbar);
	}

	private void renderStatRow(GraphicsContext context, double x, double y, double width, Color iconColor, String value) {
		// Render icon
		double iconSize = statRowHeight - 2 * padding;
		context.setFill(iconColor);
		context.fillRect(x, y, iconSize, iconSize);
		// Render stat value
		context.setTextAlign(TextAlignment.RIGHT);
		context.setFont(new Font(iconSize - 2));
		context.fillText(value, x + width - 2 * padding, y + statRowHeight / 2);
	}

	@Override
	public void render(GraphicsContext context, double width, double height) {
		double statsHeight = 2 * statRowHeight;
		double y = height - healthbar.getHeight() - statsHeight;
		// Render background
		context.setFill(Color.rgb(0, 0, 0, 0.75));
		context.fillRoundRect(0, y, width, statsHeight - padding, 10, 10);
		// Render stats
		renderStatRow(context, padding, y + padding, width, Color.LIGHTBLUE, "" + parent.getArmor());
		renderStatRow(context, padding, y + statRowHeight, width, Color.GOLD, "" + parent.getMoney());
	}

}