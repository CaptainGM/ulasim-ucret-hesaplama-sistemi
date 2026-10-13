package com.ulasim.hesaplama;

import com.ulasim.hesaplama.service.UlasimSistemi;
import com.ulasim.hesaplama.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    private static final int[] SIMGE_BOYUTLARI = {16, 32, 48, 64, 128, 256};

    @Override
    public void start(Stage stage) {
        UlasimSistemi sistem = new UlasimSistemi("/veriseti.json");
        MainView mainView = new MainView(sistem);

        Scene scene = new Scene(mainView, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        for (int boyut : SIMGE_BOYUTLARI) {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app-icon-" + boyut + ".png")));
        }

        stage.setTitle("TransitRoute - Kentsel Rota & Ücret Optimizasyon Platformu");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setWidth(1280);
        stage.setHeight(840);
        stage.centerOnScreen();
        stage.show();

        String scenarioProp = System.getProperty("scenario");
        if (scenarioProp != null) {
            try {
                int sc = Integer.parseInt(scenarioProp);
                javafx.animation.PauseTransition initWait = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.0));
                initWait.setOnFinished(ev -> mainView.senaryoSec(sc));
                initWait.play();
            } catch (Exception ignored) {}
        }

        // Harita ve rotalar render edildikten sonra GitHub için güncel ekran görüntüsünü otomatik kaydet
        javafx.animation.PauseTransition autoShot = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(6.0));
        autoShot.setOnFinished(e -> {
            try {
                String shotOut = System.getProperty("shotOut", "screenshot.png");
                java.io.File shot = com.ulasim.hesaplama.ui.ScreenshotExporter.yakalaVeKaydet(scene.getRoot(), shotOut);
                System.out.println("SCREENSHOT_KAYDEDILDI: " + shot.getAbsolutePath());
                if (System.getProperty("autoExit") != null) {
                    javafx.application.Platform.exit();
                    System.exit(0);
                }
            } catch (Exception ex) {
                System.err.println("Screenshot hatası: " + ex.getMessage());
            }
        });
        autoShot.play();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
