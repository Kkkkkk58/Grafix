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
import ru.itmo.grafix.exception.InvalidGammaException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class MainSceneController {
    @FXML
    public TabPane tabPane;
    @FXML
    public ComboBox<ColorSpace> colorSpaceList;

    @FXML
    public ComboBox<String> channelList;

    private boolean wasColorSpaceChanged = true;
    private boolean isEndlessLoop = false;
    private final List<ColorSpace> colorSpaces = List.of(
            new RGB(), new HSL(), new HSV(), new CMY(), new YCbCr601(), new YCbCr709(), new YCoCg());

    private final float gammaConst = 0.0031308f;

    public void initialize() {
        channelList.getSelectionModel().selectLast();
        colorSpaceList.getSelectionModel().selectFirst();
        tabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                channelList.getSelectionModel().selectLast();
                colorSpaceList.getSelectionModel().selectFirst();
                return;
            }
            String id = newValue.getId();
            GrafixImage image = tabMapping.get(id);
            if (image == null) {
                return;
            }
            channelList.setVisible(!Objects.equals(image.getFormat(), "P5"));
            colorSpaceList.setVisible(!Objects.equals(image.getFormat(), "P5"));
            ColorSpace colorSpace = image.getColorSpace();
            wasColorSpaceChanged = false;
            colorSpaceList.setValue(colorSpaceList.getItems().get(colorSpace.getIndex()));
            channelList.setValue(image.getChannel());
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
        ColorSpace colorSpace = getDefaultColorSpace();
//        channelList.setValue("all");
//        colorSpaceList.setValue(colorSpace);
        doOpen(file.getAbsolutePath(), file.getName(), colorSpace);
    }

    public void openDraggedFile(File file) {
        if (file == null) {
            return;
        }
        ColorSpace colorSpace = getDefaultColorSpace();
//        channelList.setValue("all");
//        colorSpaceList.setValue(colorSpace);
        doOpen(file.getAbsolutePath(), file.getName(), colorSpace);
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

    public void chooseChannel() {
        if (isEndlessLoop) {
            isEndlessLoop = false;
            return;
        }
        if (getActiveTab() == null) {
            isEndlessLoop = true;
            channelList.setValue("all");
            return;
        }
        String channel = channelList.getSelectionModel().getSelectedItem();
        if (!wasColorSpaceChanged) {
            return;
        }
        GrafixImage image = getActiveTabImage();
        if (Objects.equals(channel, "all")) {
            displayImageP6(FbConverter.convertFloatToByte(colorSpaceList.getSelectionModel().getSelectedItem().
                    toRGB(image.getData())), image.getWidth(), image.getHeight());
            image.setChannel(0);
        } else {
            int ch = Integer.parseInt(channel);
            displayImageP6(FbConverter.convertFloatToByte(colorSpaceList.getSelectionModel().
                    getSelectedItem().toRGB(ChannelDecomposer.decompose(image.getData(), ch, image.getColorSpace()))), image.getWidth(), image.getHeight());
            image.setChannel(ch);
        }
    }

    public void chooseColorSpace() {
        if (isEndlessLoop) {
            isEndlessLoop = false;
            return;
        }
        if (getActiveTab() == null) {
            isEndlessLoop = true;
            colorSpaceList.setValue(getDefaultColorSpace());
            return;
        }
        ColorSpace selectedMode = colorSpaceList.getSelectionModel().getSelectedItem();
        if (!wasColorSpaceChanged || selectedMode == getActiveTabImage().getColorSpace()) {
            return;
        }
        getActiveTabImage().convertTo(selectedMode);
        channelList.getSelectionModel().selectLast();
    }

    private GrafixImage getActiveTabImage() {
        Tab activeTab = getActiveTab();
        if (activeTab == null) {
            return null;
        }
        String activeTabId = activeTab.getId();
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
        byte[] data = FbConverter.convertFloatToByte(colorSpace.toRGB(image.getData()));
        if (image.getFormat().charAt(1) == '5') {
            // TODO вынести в функцию
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            img.getRaster().setDataElements(0, 0, image.getWidth(), image.getHeight(), data);
            WritableImage img2 = new WritableImage(image.getWidth(), image.getHeight());
            SwingFXUtils.toFXImage(img, img2);
            ImageView imageView = new ImageView(img2);
            scrP.setTarget(imageView);
            channelList.setVisible(false);
            colorSpaceList.setVisible(false);
        } else {
            channelList.setVisible(true);
            colorSpaceList.setVisible(true);
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

    private void displayImageP6(byte[] data, int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter writer = img.getPixelWriter();
        ZoomableScrollPane scrP = new ZoomableScrollPane();
        scrP.setPrefSize(tabPane.getPrefWidth(), tabPane.getPrefHeight());
        getActiveTab().setContent(scrP);
        PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
        writer.setPixels(0, 0, width, height, pf, data, 0, width * 3);
        ImageView imageView = new ImageView(img);
        scrP.setTarget(imageView);
    }

    private ColorSpace getDefaultColorSpace() {
        return colorSpaceList.getItems().get(0);
    }

    public void assignGamma() {
        Float gamma = getGammaInput("Assign gamma");
        if (gamma == null) {
            return;
        }
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        float previousGamma = image.getGamma();
        float[] data = convertGamma(gamma, previousGamma, image.getData());
        displayImageP6(FbConverter.convertFloatToByte(data), image.getWidth(), image.getHeight());
    }

    public void convertGamma() {
        Float gamma = getGammaInput("Convert gamma");
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        float previousGamma = image.getGamma();
        if (gamma == null || Float.compare(previousGamma, gamma) == 0) {
            return;
        }
        float[] data = convertGamma(gamma, previousGamma, image.getData());
        image.setData(data);
        image.setGamma(gamma);
    }

    private float[] convertGamma(Float gamma, float previousGamma, float[] data) {
        float[] dataBuffer = Arrays.copyOf(data, data.length);
        for (int i = 0; i < data.length; ++i) {
            double buffer = data[i];
            if (previousGamma != 0) {
                buffer = Math.pow(buffer, 1.0 / previousGamma);
            }
            if (gamma != 0) {
                dataBuffer[i] = (float) Math.pow(buffer, gamma);
            } else if (buffer < gammaConst) {
                dataBuffer[i] = (float) (buffer * 12.92f);
            } else {
                dataBuffer[i] = 1.055f * (float) Math.pow(buffer, 1.0 / 2.4) - 0.055f;
//                data[i] = (float) Math.pow((buffer + 0.055f) / 1.055f, 2.4f);
            }
        }
        return dataBuffer;
    }

    private Float getGammaInput(String title) {
        TextInputDialog td = new TextInputDialog();
        td.setTitle(title);
        td.setHeaderText("Enter gamma");
        td.setGraphic(null);
        float gamma = 0;
        GrafixImage image = getActiveTabImage();
        if (image != null) {
            gamma = image.getGamma();
        }
        td.getEditor().setText(String.valueOf(gamma));
        String inputValue = td.showAndWait().orElse(null);
        if (inputValue == null) {
            return null;
        }
        try {
            return Float.parseFloat(inputValue);
        } catch (NumberFormatException e) {
            throw new InvalidGammaException(inputValue);
        }
    }
}
