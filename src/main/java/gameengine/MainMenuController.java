package gameengine;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainMenuController {

    @FXML
    private void clickPlay() throws IOException {
        GameViewController gc = (GameViewController) App.setRoot("game_view");
        gc.loadLvl("Intro level");
    }

    @FXML
    private void clickLevelEditor() throws IOException {
        App.setRoot("level_editor");
    }

    @FXML
    private void clickExit() throws IOException {
        Platform.exit();
    }
}
