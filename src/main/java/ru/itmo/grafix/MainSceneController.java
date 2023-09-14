package ru.itmo.grafix;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

public class MainSceneController {
    @FXML
    public TabPane tabPane;
    private ImageProcessorService imageProcessorService;

    public MainSceneController() {
        imageProcessorService = new ImageProcessorServiceImpl();
    }

    public MainSceneController(ImageProcessorService imageProcessorService) {
        this.imageProcessorService = imageProcessorService;
    }

    public void saveFile() {
        // сохранить открытый
    }

    public void saveFileAs() {

    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        Tab tab = new Tab(file.getName());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        ScrollPane scrP = new ScrollPane();
        scrP.setPrefSize(tabPane.getPrefWidth(), tabPane.getPrefHeight());
        tab.setContent(scrP);
        GrafixImage image = imageProcessorService.open(file.getAbsolutePath());
        if (image.getFormat().charAt(1) == '5') {
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setDataElements(0, 0, image.getWidth(), image.getHeight(), image.getData());
            WritableImage img2 = new WritableImage(image.getWidth(), image.getHeight());
            SwingFXUtils.toFXImage(img, img2);
            ImageView imageView = new ImageView(img2);
            scrP.setContent(imageView);
            //tab.setContent(imageView);
        } else {
//        var canvas = new Canvas(image.getWidth(), image.getHeight());
//        tab.setContent(canvas);
            //final PixelWriter writer = canvas.getGraphicsContext2D().getPixelWriter();

            WritableImage img = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter writer = img.getPixelWriter();
            ImageView imageView = new ImageView(img);
            //tab.setContent(imageView);
            int pos = 0;
            PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
            int mult = 3;
            writer.setPixels(0, 0, image.getWidth(), image.getHeight(), pf, image.getData(), 0, image.getWidth() * mult);
            scrP.setContent(imageView);
        }

//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                if (Objects.equals(image.getFormat(), "P6")) {
//                    writer.setColor(x, y, javafx.scene.paint.Color.rgb(image.getData()[pos] & 0xFF, image.getData()[pos + 1] & 0xFF, image.getData()[pos + 2] & 0xFF));
//                    pos += 3;
//                } else{
//                    writer.setColor(x, y, javafx.scene.paint.Color.grayRgb(image.getData()[pos] & 0xFF));
//                    pos += 1;
//                }
//            }
//        }
//        var gc = canvas.getGraphicsContext2D();
//        gc.drawImage(image, 0, 0);

//        GraphicsContext
//        GrafixImage image = imageProcessorService.open(file.getAbsolutePath());
//        displayImage(bytes);

    }
}