package gameengine;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;

public class LevelEditor {
    @FXML
    private void clickMainMenu() throws IOException {
        App.setRoot("main_menu");
    }

    // move level loading to specialized class accepting root tag and level path
    public void loadLevel(String path) {
        try {
            File lvlFile = new File(App.class.getResource(path).toURI());
            System.out.println(lvlFile.toString());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
