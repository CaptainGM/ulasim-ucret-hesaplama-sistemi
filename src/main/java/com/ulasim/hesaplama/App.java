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

        Scene scene = new Scene(new MainView(sistem), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        for (int boyut : SIMGE_BOYUTLARI) {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/app-icon-" + boyut + ".png")));
        }

        stage.setTitle("Ulaşım Sistemi");
        stage.setScene(scene);
        stage.setMinWidth(960);
        stage.setMinHeight(640);
        stage.setWidth(1200);
        stage.setHeight(800);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
