package gameengine;

import java.io.IOException;

import javafx.fxml.FXML;

public class LevelEditor {
    @FXML
    private void clickMainMenu() throws IOException {
        App.setRoot("main_menu");
    }
}
