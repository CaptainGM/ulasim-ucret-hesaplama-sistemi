package com.ulasim.hesaplama.ui;

import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * GitHub ve dokümantasyon için uygulamanın güncel durumunu yüksek çözünürlükte
 * doğrudan ekran görüntüsü (screenshot.png) olarak dışa aktaran yardımcı sınıf.
 */
public final class ScreenshotExporter {
    private ScreenshotExporter() {
    }

    public static File yakalaVeKaydet(Node node, String dosyaAdi) throws IOException {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage fxImage = node.snapshot(params, null);
        int width = (int) fxImage.getWidth();
        int height = (int) fxImage.getHeight();

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        PixelReader reader = fxImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bImage.setRGB(x, y, reader.getArgb(x, y));
            }
        }

        File ciktiDosyasi = new File(dosyaAdi);
        if (ciktiDosyasi.getParentFile() != null) {
            ciktiDosyasi.getParentFile().mkdirs();
        }
        ImageIO.write(bImage, "png", ciktiDosyasi);
        return ciktiDosyasi;
    }
}
