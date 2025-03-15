package gameengine;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenu {

    @FXML
    private void clickPlay() throws IOException {
        GameView controller = (GameView) App.setRoot("game_view");
        controller.loadLvl("Intro level");
    }

    @FXML
    private void clickLevelEditor() throws IOException {
        LevelEditor controller = (LevelEditor) App.setRoot("level_editor");
        controller.loadLevel("levels/1.json");
    }

    @FXML
    private void clickExit() throws IOException {
        Platform.exit();
    }
}
