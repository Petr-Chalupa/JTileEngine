package gameengine;

import java.io.IOException;

import javafx.fxml.FXML;

public class GameViewController implements Runnable {
    private static Thread MAIN_THREAD;
    private static int FPS = 60;
    private static double deltaTime = 0;

    @FXML
    private void clickMainMenu() throws IOException {
        stop();
        App.setRoot("main_menu");
    }

    public void loadLvl(String lvlName) {
        MAIN_THREAD = new Thread(this);
        MAIN_THREAD.start();
    }

    public static void stop() {
        if (MAIN_THREAD != null) MAIN_THREAD.interrupt();
    }

    @Override
    public void run() {
        double interval = 1_000_000_000 / FPS;
        long lastTime = System.nanoTime();

        while (MAIN_THREAD != null && !MAIN_THREAD.isInterrupted()) {
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime) / interval;
            lastTime = currentTime;

            while (deltaTime >= 1) {
                update();
                deltaTime--;
            }

            render();
        }
    }

    private void update() {
        System.out.println("Update: " + deltaTime);
    }

    private void render() {
    }
}
