package ru.itmo.grafix.ui.controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.implementation.*;
import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.implementation.AtkinsonDithering;
import ru.itmo.grafix.core.dithering.implementation.FloydSteinbergDithering;
import ru.itmo.grafix.core.dithering.implementation.OrderedDithering;
import ru.itmo.grafix.core.dithering.implementation.RandomDithering;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.*;
import ru.itmo.grafix.ui.components.dialogs.*;
import ru.itmo.grafix.ui.components.scrollpane.ZoomableScrollPane;

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
    private final List<Dithering> ditheringMethods = List.of(new AtkinsonDithering(), new OrderedDithering(), new RandomDithering(),
            new FloydSteinbergDithering());

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
        colorSpaceList.setValue(colorSpaceList.getItems().get(currentColorSpace.getIndex()));
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }
        ColorSpace colorSpace = getDefaultColorSpace();
        doOpen(file.getAbsolutePath(), file.getName(), colorSpace);
    }

    public void openDraggedFile(File file) {
        if (file == null) {
            return;
        }
        ColorSpace colorSpace = getDefaultColorSpace();
        doOpen(file.getAbsolutePath(), file.getName(), colorSpace);
    }

    public void openFileAs() {
        ChoiceDialog<ColorSpace> dialog = new ColorSpaceChoiceDialog(getDefaultColorSpace(), colorSpaces);
        ColorSpace result = dialog.showAndWait().orElse(null);
        if (result != null) {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
            if (file == null) {
                return;
            }

            doOpen(file.getAbsolutePath(), file.getName(), result);
            colorSpaceList.setValue(colorSpaceList.getItems().get(result.getIndex()));
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

    public void assignGamma() {
        GrafixImage image = getActiveTabImage();
        Float gamma = GammaInputDialog.getGammaInput("Assign gamma", image);
        if (gamma == null) {
            return;
        }
        float previousGamma = image.getGamma();
        ColorSpace space = image.getColorSpace();
        float[] data = GammaCorrecter.convertGamma(gamma, previousGamma, space.toRGB(image.getData()));
        displayImage(image.getFormat(), FbConverter.convertFloatToByte(space.fromRGB(data)), image.getWidth(), image.getHeight());
    }

    public void convertGamma() {
        GrafixImage image = getActiveTabImage();
        Float gamma = GammaInputDialog.getGammaInput("Convert gamma", image);
        if (gamma == null) {
            return;
        }
        float previousGamma = image.getGamma();
        if (previousGamma == gamma) {
            return;
        }
        ColorSpace space = image.getColorSpace();
        float[] data = GammaCorrecter.convertGamma(gamma, previousGamma, space.toRGB(image.getData()));
        image.setData(space.fromRGB(data));
        image.setGamma(gamma);
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
        openTab(fileName, image);
        float[] data = colorSpace.toRGB(image.getData());
        displayImage(image.getFormat(), FbConverter.convertFloatToByte(data), image.getWidth(), image.getHeight());
    }

    private void openTab(String tabName, GrafixImage image) {
        Tab tab = new Tab(tabName);
        tab.setOnCloseRequest(getTabOnCloseRequestEvent());
        tabPane.getTabs().add(tab);
        String tabId = UUID.randomUUID().toString();
        tab.setId(tabId);
        tabMapping.put(tabId, image);
        tabPane.getSelectionModel().select(tab);
        if (image.getFormat().charAt(1) == '5') {
            channelList.setVisible(false);
            colorSpaceList.setVisible(false);
        } else {
            channelList.setVisible(true);
            colorSpaceList.setVisible(true);
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
        PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
        writer.setPixels(0, 0, width, height, pf, data, 0, width * 3);
        setImage(img);
    }

    private void displayImageP5(byte[] data, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, width, height, data);
        WritableImage img2 = new WritableImage(width, height);
        SwingFXUtils.toFXImage(img, img2);
        setImage(img2);
    }

    private void setImage(WritableImage img) {
        ImageView imageView = new ImageView(img);
        ZoomableScrollPane scrP = new ZoomableScrollPane();
        scrP.setPrefSize(tabPane.getPrefWidth(), tabPane.getPrefHeight());
        getActiveTab().setContent(scrP);
        scrP.setTarget(imageView);
    }

    private ColorSpace getDefaultColorSpace() {
        return colorSpaceList.getItems().get(0);
    }

    private void displayImage(String format, byte[] data, int width, int height) {
        if (Objects.equals(format, "P6")) {
            displayImageP6(data, width, height);
        } else {
            displayImageP5(data, width, height);
        }
    }

    public void generateGradient() {
        SizeInputDialog gradientSizeInputDialog = new SizeInputDialog();
        Pair<Integer, Integer> size = gradientSizeInputDialog.showAndWait().orElse(null);
        if (size == null || size.getKey() == null || size.getValue() == null) {
            return;
        }
        Integer width = size.getKey();
        Integer height = size.getValue();
        byte[] buffer = GradientGenerator.generateGradient(width, height);
        GrafixImage grafixImage = new GrafixImage("P5", width, height, 255, FbConverter.convertBytesToFloat(buffer, 255), null, 0, new RGB());
        openTab("Gradient", grafixImage);
        displayImageP5(buffer, width, height);
    }

    public void openEmptyTab(ActionEvent actionEvent) {
    }

    public void chooseDithering() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        CheckBox preview = new CheckBox("Preview");
        DitheringChoiceDialog dialog = new DitheringChoiceDialog(ditheringMethods, preview);
        byte[] imageBytes = FbConverter.convertFloatToByte(image.getData());
        byte[] previewImageBytes = imageBytes;
        preview.setOnAction(event -> {
            Dithering dithering = dialog.getSelectedItem();
            if (dithering == null) {
                return;
            }
            byte[] data = (preview.isSelected())
                    ? dithering.convert(previewImageBytes, image.getWidth(), image.getHeight())
                    : previewImageBytes;
            displayImage(image.getFormat(), data, image.getWidth(), image.getHeight());
        });

        Dithering dithering = dialog.showAndWait().orElse(null);
        if (dithering != null) {
            imageBytes = dithering.convert(imageBytes, image.getWidth(), image.getHeight());
            image.setData(FbConverter.convertBytesToFloat(imageBytes, 255));
        }
        displayImage(image.getFormat(), imageBytes, image.getWidth(), image.getHeight());
    }
}
