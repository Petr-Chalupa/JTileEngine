module ui {
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive engine;
	requires java.desktop;

	opens ui to javafx.fxml;

	exports ui;
}
