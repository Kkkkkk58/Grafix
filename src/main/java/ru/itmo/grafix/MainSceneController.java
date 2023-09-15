package ru.itmo.grafix;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainSceneController {
    @FXML
    public TabPane tabPane;
    private final ImageProcessorService imageProcessorService;
    private final Map<String, GrafixImage> tabMapping = new HashMap<>();

    public MainSceneController() {
        imageProcessorService = new ImageProcessorServiceImpl();
    }

    public MainSceneController(ImageProcessorService imageProcessorService) {
        this.imageProcessorService = imageProcessorService;
    }

    public void saveFile() {
        if (getActiveTab() == null) {
            return;
        }
        GrafixImage image = getActiveTabImage();
        doSave(image.getPath(), image);
    }

    public boolean saveFileAs() {
        if (getActiveTab() == null) {
            return false;
        }
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return false;
        }

        GrafixImage image = getActiveTabImage();
        doSave(file.getAbsolutePath(), image);
        closeTab(getActiveTab());
        doOpen(file.getAbsolutePath(), file.getName());
        return true;
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        doOpen(file.getAbsolutePath(), file.getName());
    }

    public void openDraggedFile(File file) {
        if (file == null) {
            return;
        }

        doOpen(file.getAbsolutePath(), file.getName());
    }

    private GrafixImage getActiveTabImage() {
        String activeTabId = getActiveTab().getId();
        return tabMapping.get(activeTabId);
    }

    private void doSave(String absolutePath, GrafixImage image) {
        ByteArrayOutputStream stream = imageProcessorService.write(image);
        try(OutputStream fileStream = new FileOutputStream(absolutePath)){
            stream.writeTo(fileStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doOpen(String absolutePath, String fileName) {
        GrafixImage image = imageProcessorService.open(absolutePath);
        Tab tab = new Tab(fileName);
        tab.setOnCloseRequest(getTabOnCloseRequestEvent());
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
        String tabId = UUID.randomUUID().toString();
        ZoomableScrollPane scrP = new ZoomableScrollPane();
        scrP.setPrefSize(tabPane.getPrefWidth(), tabPane.getPrefHeight());
        tab.setContent(scrP);
        tab.setId(tabId);
        tabMapping.put(tabId, image);
        if (image.getFormat().charAt(1) == '5') {
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setDataElements(0, 0, image.getWidth(), image.getHeight(), image.getData());
            WritableImage img2 = new WritableImage(image.getWidth(), image.getHeight());
            SwingFXUtils.toFXImage(img, img2);
            ImageView imageView = new ImageView(img2);
            scrP.setTarget(imageView);
        } else {
            WritableImage img = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter writer = img.getPixelWriter();
            ImageView imageView = new ImageView(img);
            PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
            int mult = 3;
            writer.setPixels(0, 0, image.getWidth(), image.getHeight(), pf, image.getData(), 0, image.getWidth() * mult);
            scrP.setTarget(imageView);
        }

    }

    private Tab getActiveTab() {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void closeTab(Tab tab) {
        EventHandler<Event> handler = tab.getOnClosed();
        if (handler != null) {
            handler.handle(null);
        } else {
            tab.getTabPane().getTabs().remove(tab);
        }
    }

    private EventHandler<Event> getTabOnCloseRequestEvent() {
        return event -> {
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
            Alert alert = new ImageSavingBeforeClosingConfirmationAlert();
            ButtonType buttonType = alert.showAndWait().orElseThrow();
            if (ButtonType.YES.equals(buttonType)) {
                if (!saveFileAs()) {
                    event.consume();
                }
            } else if (ButtonType.CANCEL.equals(buttonType)) {
                event.consume();
            }
            Platform.runLater(() -> tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER));
        };
    }
}