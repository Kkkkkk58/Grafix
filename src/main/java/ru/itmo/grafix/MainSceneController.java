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
        } else {
            WritableImage img = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter writer = img.getPixelWriter();
            ImageView imageView = new ImageView(img);
            PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
            int mult = 3;
            writer.setPixels(0, 0, image.getWidth(), image.getHeight(), pf, image.getData(), 0, image.getWidth() * mult);
            scrP.setContent(imageView);
        }
    }
}