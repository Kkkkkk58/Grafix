package ru.itmo.grafix;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ru.itmo.grafix.api.ColorSpace;
import ru.itmo.grafix.colorSpace.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainSceneController {
    @FXML
    public TabPane tabPane;
    @FXML
    public ComboBox<ColorSpace> colorSpaceList;

    private boolean wasColorSpaceChanged = true;
    private final List<ColorSpace> colorSpaces = List.of(
            new RGB(), new HSL(), new HSV(), new CMY(), new YCbCr601(), new YCbCr709(), new YCoCg());

    public void initialize() {
        colorSpaceList.getSelectionModel().selectFirst();
        tabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            String id = newValue.getId();
            GrafixImage image = tabMapping.get(id);
            if (image == null) {
                return;
            }
            ColorSpace colorSpace = image.getColorSpace();
            wasColorSpaceChanged = false;
            colorSpaceList.setValue(colorSpaceList.getItems().get(colorSpace.getIndex()));
            wasColorSpaceChanged = true;
        }));
    }

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

    public void saveFileAs() {
        File file = saveActiveTabImageToChosenFile();
        if (file == null) {
            return;
        }
        ColorSpace currentColorSpace = getActiveTabImage().getColorSpace();
        closeTab(getActiveTab());
        doOpen(file.getAbsolutePath(), file.getName(), currentColorSpace);
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        doOpen(file.getAbsolutePath(), file.getName(), colorSpaceList.getItems().get(0));
    }

    public void openDraggedFile(File file) {
        if (file == null) {
            return;
        }

        doOpen(file.getAbsolutePath(), file.getName(), colorSpaces.get(0));
    }

    public void chooseColorSpace() {
        ColorSpace selectedMode = colorSpaceList.getSelectionModel().getSelectedItem();
        if (!wasColorSpaceChanged || getActiveTab() == null || selectedMode == getActiveTabImage().getColorSpace()) {
            return;
        }
        getActiveTabImage().convertTo(selectedMode);
//        System.out.println(getActiveTab().getId());
//        getActiveTabImage().setColorSpace(selectedMode);
//        System.out.println(selectedMode.toString());
    }

    private GrafixImage getActiveTabImage() {
        String activeTabId = getActiveTab().getId();
        return tabMapping.get(activeTabId);
    }

    private void doSave(String absolutePath, GrafixImage image) {
        ByteArrayOutputStream stream = imageProcessorService.write(image);
        try (OutputStream fileStream = new FileOutputStream(absolutePath)) {
            stream.writeTo(fileStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doOpen(String absolutePath, String fileName, ColorSpace colorSpace) {
        GrafixImage image = imageProcessorService.open(absolutePath, colorSpace);
        Tab tab = new Tab(fileName);
        tab.setOnCloseRequest(getTabOnCloseRequestEvent());
        tabPane.getTabs().add(tab);
        String tabId = UUID.randomUUID().toString();
        ZoomableScrollPane scrP = new ZoomableScrollPane();
        scrP.setPrefSize(tabPane.getPrefWidth(), tabPane.getPrefHeight());
        tab.setContent(scrP);
        tab.setId(tabId);
        tabMapping.put(tabId, image);
        tabPane.getSelectionModel().select(tab);
//        tab.setOnSelectionChanged((event) -> {
//                colorSpaceList.setValue(colorSpaceList.getItems().get(colorSpace.getIndex()));
//        });
        byte[] data = FbConverter.convertFloatToByte(colorSpace.toRGB(image.getData()));
        if (image.getFormat().charAt(1) == '5') {
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setDataElements(0, 0, image.getWidth(), image.getHeight(), data);
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
            writer.setPixels(0, 0, image.getWidth(), image.getHeight(), pf, data, 0, image.getWidth() * mult);
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
            tabPane.getTabs().remove(tab);
        }
        tabMapping.remove(tab.getId());
    }

    private EventHandler<Event> getTabOnCloseRequestEvent() {
        return event -> {
            tabPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
            Alert alert = new ImageSavingBeforeClosingConfirmationAlert();
            ButtonType buttonType = alert.showAndWait().orElseThrow();
            if (ButtonType.CANCEL.equals(buttonType)
                    || ButtonType.YES.equals(buttonType) && saveActiveTabImageToChosenFile() == null) {
                event.consume();
            } else {
                closeTab(getActiveTab());
            }
            Platform.runLater(() -> tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER));
        };
    }

    private File saveActiveTabImageToChosenFile() {
        if (getActiveTab() == null) {
            return null;
        }

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return null;
        }

        GrafixImage image = getActiveTabImage();
        doSave(file.getAbsolutePath(), image);

        return file;
    }

    public void openFileAs() {
        ChoiceDialog<ColorSpace> dialog = new ChoiceDialog<>(colorSpaces.get(0), colorSpaces);
        dialog.setTitle("Color spaces choice");
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.setContentText("Choose the color space");
        ColorSpace result = dialog.showAndWait().orElse(null);
        if (result != null) {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file == null) {
                return;
            }

            colorSpaceList.setValue(colorSpaceList.getItems().get(result.getIndex()));
            doOpen(file.getAbsolutePath(), file.getName(), result);
        }
    }
}
