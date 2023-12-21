package ru.itmo.grafix.ui.controllers;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import ru.itmo.grafix.core.autocorrection.AutoCorrecter;
import ru.itmo.grafix.core.autocorrection.ImageHistogramData;
import ru.itmo.grafix.core.autocorrection.ImageHistogramExtractor;
import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.implementation.*;
import ru.itmo.grafix.core.dithering.Dithering;
import ru.itmo.grafix.core.dithering.implementation.AtkinsonDithering;
import ru.itmo.grafix.core.dithering.implementation.FloydSteinbergDithering;
import ru.itmo.grafix.core.dithering.implementation.OrderedDithering;
import ru.itmo.grafix.core.dithering.implementation.RandomDithering;
import ru.itmo.grafix.core.drawing.DrawingAlgorithm;
import ru.itmo.grafix.core.drawing.WuAlgorithm;
import ru.itmo.grafix.core.exception.InvalidAutocorrectionException;
import ru.itmo.grafix.core.filtering.Filter;
import ru.itmo.grafix.core.filtering.implementation.*;
import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.*;
import ru.itmo.grafix.core.scaling.Scaling;
import ru.itmo.grafix.core.scaling.ScalingType;
import ru.itmo.grafix.core.scaling.implementation.BCsplineScaling;
import ru.itmo.grafix.core.scaling.implementation.BilinearScaling;
import ru.itmo.grafix.core.scaling.implementation.Lanczos3Scaling;
import ru.itmo.grafix.core.scaling.implementation.NearestNeighbourScaling;
import ru.itmo.grafix.ui.components.dialogs.*;
import ru.itmo.grafix.ui.components.dialogs.filters.FilterParamsChoiceDialog;
import ru.itmo.grafix.ui.components.scrollpane.ZoomableScrollPane;
import ru.itmo.grafix.ui.components.windows.HistogramWindow;
import ru.itmo.grafix.ui.models.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

public class MainSceneController {
    private final List<ColorSpace> colorSpaces = List.of(
            new RGB(), new HSL(), new HSV(), new CMY(), new YCbCr601(), new YCbCr709(), new YCoCg());
    private final List<Dithering> ditheringMethods = List.of(new AtkinsonDithering(), new OrderedDithering(), new RandomDithering(),
            new FloydSteinbergDithering());

    private final List<Scaling> scalingMethods = List.of(new NearestNeighbourScaling(), new BilinearScaling(), new Lanczos3Scaling(),
            new BCsplineScaling());

    private final List<Filter> filterAlgorithms = List.of(new ThresholdFilter(), new ThresholdOtsuFilter(), new MedianFilter(), new GaussianFilter(),
            new LinearAveragingFilter(), new UnsharpMaskingFilter(), new ContrastAdaptiveSharpeningFilter(),
            new SobelFilter(), new CannyEdgeDetectorFilter());
    private final ImageProcessorService imageProcessorService;
    private final Map<String, TabContext> tabMapping = new HashMap<>();
    private final Map<GrafixImage, HistogramWindow> imageToHistogramMap = new HashMap<>();
    @FXML
    public TabPane tabPane;
    @FXML
    public ComboBox<ColorSpace> colorSpaceList;
    @FXML
    public ComboBox<String> channelList;
    private boolean wasColorSpaceChanged = true;
    private boolean isEndlessLoop = false;

    public MainSceneController() {
        imageProcessorService = new ImageProcessorServiceImpl();
    }

    public MainSceneController(ImageProcessorService imageProcessorService) {
        this.imageProcessorService = imageProcessorService;
    }

    private static void changeDitheringPreview(CheckBox preview) {
        if (!preview.isSelected()) {
            return;
        }
        preview.setSelected(false);
        preview.fire();
    }

    private static boolean isAutocorrectionParameterOutOfBounds(Double autocorrectionParameter) {
        return autocorrectionParameter < 0 || autocorrectionParameter >= 0.5;
    }

    public void initialize() {
        channelList.getSelectionModel().selectLast();
        colorSpaceList.getSelectionModel().selectFirst();
        tabPane.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                channelList.getSelectionModel().selectLast();
                colorSpaceList.getSelectionModel().selectFirst();
                return;
            }
            changeTabHistogramVisibilityIfExists(oldValue, false);
            String id = newValue.getId();
            if (!tabMapping.containsKey(id)) {
                return;
            }
            GrafixImage image = tabMapping.get(id).getImage();
//            if (image == null) {
//                return;
//            }
            channelList.setVisible(!Objects.equals(image.getFormat(), "P5"));
            colorSpaceList.setVisible(!Objects.equals(image.getFormat(), "P5"));
            ColorSpace colorSpace = image.getColorSpace();
            wasColorSpaceChanged = false;
            colorSpaceList.setValue(colorSpaceList.getItems().get(colorSpace.getIndex()));
            channelList.setValue(image.getChannel() == 0 ? "all" : String.valueOf(image.getChannel()));
            wasColorSpaceChanged = true;
//            changeTabHistogramVisibilityIfExists(newValue, true);
        }));
    }

    public void saveFile() {
        if (getActiveTab() == null) {
            return;
        }
        GrafixImage image = getActiveTabImage();
        doSave(image.getPath(), image, image.getFormat());
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
        updateHistogramIfExists();
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
        updateHistogramIfExists();
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
        displayImage(image.getFormat(), space.fromRGB(data), image.getWidth(), image.getHeight());
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
        updateHistogramIfExists();
    }

    public void applyFilter() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        Dialog<Filter> dialog = new FilterParamsChoiceDialog(filterAlgorithms);
        Filter filter = dialog.showAndWait().orElse(null);
        if (filter == null) {
            return;
        }
        if (!filter.setParams()) {
            return;
        }

        GrafixImage filteredImage = filter.apply(image);

        tabMapping.get(getActiveTab().getId()).setImage(filteredImage);
        displayImage(filteredImage.getFormat(), filteredImage.getData(), filteredImage.getWidth(), filteredImage.getHeight());
    }

    private GrafixImage getActiveTabImage() {
        Tab activeTab = getActiveTab();
        if (activeTab == null) {
            return null;
        }
        String activeTabId = activeTab.getId();
        return tabMapping.get(activeTabId).getImage();
    }

    private void doSave(String absolutePath, GrafixImage image, String format) {
        ByteArrayOutputStream stream = imageProcessorService.write(image, format);
        try (OutputStream fileStream = new FileOutputStream(absolutePath)) {
            stream.writeTo(fileStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doOpen(String absolutePath, String fileName, ColorSpace colorSpace) {
        GrafixImage image = imageProcessorService.open(absolutePath, colorSpace);
        openTab(fileName, image);
        float[] data = GammaCorrecter.restoreGamma(image.getGamma(), colorSpace.toRGB(image.getData()));
        displayImage(image.getFormat(), data, image.getWidth(), image.getHeight());
    }

    private void openTab(String tabName, GrafixImage image) {
        Tab tab = new Tab(tabName);
        tab.setOnCloseRequest(getTabOnCloseRequestEvent());
//        tab.setOnClosed(event -> closeHistogramIfExists(getActiveTabImage()));
        tabPane.getTabs().add(tab);
        String tabId = UUID.randomUUID().toString();
        tab.setId(tabId);
        tabMapping.put(tabId, new TabContext(image, null, null));
        tabPane.getSelectionModel().select(tab);
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
        closeHistogramIfExists(tabMapping.get(tab.getId()).getImage());
        tabMapping.remove(tab.getId());
    }

    private void changeTabHistogramVisibilityIfExists(Tab tab, boolean makeVisible) {
        if (tab == null) {
            return;
        }
        String id = tab.getId();
        if (!tabMapping.containsKey(id)) {
            return;
        }
        GrafixImage image = tabMapping.get(id).getImage();
        if (image != null) {
            makeHistogramVisibleIfExists(image, makeVisible);
        }
    }

    private void closeHistogramIfExists(GrafixImage image) {
        if (image != null) {
            HistogramWindow histogram = imageToHistogramMap.remove(image);
            if (histogram != null) {
                histogram.close();
            }
        }
    }

    private void makeHistogramVisibleIfExists(GrafixImage image, boolean makeVisible) {
        HistogramWindow histogram = imageToHistogramMap.get(image);
        if (histogram != null) {
            if (makeVisible) {
                histogram.show();
            } else {
                histogram.hide();
            }
        }
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
        FileChooser.ExtensionFilter pnm = new FileChooser.ExtensionFilter("PNM", "*.pnm");
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("PNG", "*.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(pnm, png);

        GrafixImage image = getActiveTabImage();
        if (image.getFormat().startsWith("PNG")) {
            fileChooser.setSelectedExtensionFilter(png);
        } else {
            fileChooser.setSelectedExtensionFilter(pnm);
        }

        File file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return null;
        }

        String extenstion = fileChooser.getSelectedExtensionFilter().getDescription() + (image.isGrayscale() ? "5" : "6");
        doSave(file.getAbsolutePath(), image, extenstion);

        return file;
    }

    private ImageView displayImageP6(byte[] data, int width, int height) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter writer = img.getPixelWriter();
        PixelFormat<ByteBuffer> pf = PixelFormat.getByteRgbInstance();
        writer.setPixels(0, 0, width, height, pf, data, 0, width * 3);
        return setImage(img);
    }

    private ImageView displayImageP5(byte[] data, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, width, height, data);
        WritableImage img2 = new WritableImage(width, height);
        SwingFXUtils.toFXImage(img, img2);
        return setImage(img2);
    }

    private ImageView setImage(WritableImage img) {
        ImageView imageView = new ImageView(img);
        ZoomableScrollPane scrP = new ZoomableScrollPane();
        getActiveTab().setContent(scrP);
        scrP.setTarget(imageView);
        return imageView;
    }

    private ColorSpace getDefaultColorSpace() {
        return colorSpaceList.getItems().get(0);
    }

    private ImageView displayImage(String format, float[] data, int width, int height) {
        if (Objects.equals(format, "P6") || Objects.equals(format, "PNG6")) {
            channelList.setVisible(true);
            colorSpaceList.setVisible(true);
            return displayImageP6(FbConverter.convertFloatToByte(data), width, height);
        } else {
            channelList.setVisible(false);
            colorSpaceList.setVisible(false);
            return displayImageP5(FbConverter.convertFloatToByte(data), width, height);
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
        float[] imageBytes = image.getData();
        preview.setOnAction(event -> {
            Dithering dithering = dialog.getDitheringSelection().getValue();
            if (dithering == null) {
                return;
            }
            Integer bitDepth = dialog.getBitDepthSelection().getValue();
            float[] data = (preview.isSelected())
                    ? applyDithering(dithering, image, bitDepth)
                    : image.getData();
            displayImage(image.getFormat(), data, image.getWidth(), image.getHeight());
        });
        dialog.getDitheringSelection().setOnAction(event -> changeDitheringPreview(preview));
        dialog.getBitDepthSelection().setOnAction(event -> changeDitheringPreview(preview));
        Pair<Dithering, Integer> ditheringModel = dialog.showAndWait().orElse(null);
        if (ditheringModel != null && ditheringModel.getKey() != null) {
            imageBytes = applyDithering(ditheringModel.getKey(), image, ditheringModel.getValue());
            image.setData(imageBytes);
        }
        displayImage(image.getFormat(), imageBytes, image.getWidth(), image.getHeight());
    }

    private float[] applyDithering(Dithering dithering, GrafixImage image, int bitDepth) {
        float[] data = image.getColorSpace().toRGB(image.getData());
        data = GammaCorrecter.restoreGamma(image.getGamma(), data);
        data = dithering.convert(data, image.getWidth(), image.getHeight(), bitDepth, image.getGamma());
        return image.getColorSpace().fromRGB(data);
    }

    private ImageView getImageViewFromScrollPane(ScrollPane scrollPane) {
        StackPane stackPane = (StackPane) scrollPane.getContent();
        Group group = (Group) stackPane.getChildren().get(0);

        return (ImageView) group.getChildren().get(0);
    }

    public void selectDrawingParams() {
        Tab activeTab = getActiveTab();
        if (activeTab == null) {
            return;
        }
        TabContext tabContext = tabMapping.get(activeTab.getId());
        tabContext.setBeginPoint(null);
        GrafixImage image = tabContext.getImage();
        DrawingParamsChoiceDialog drawingParamsChoiceDialog = getDrawingParamsChoiceDialog(image.getFormat(), image.getColorSpace(), image.getChannel());
        DrawingParams params = drawingParamsChoiceDialog.showAndWait().orElse(null);
        if (params == null) {
            return;
        }
        ScrollPane scrollPane = (ScrollPane) getActiveTab().getContent();
        tabContext.setDrawingContext(params);
        ImageView imageView = getImageViewFromScrollPane(scrollPane);
        imageView.setOnMouseClicked(e -> getCoordinatesOnDrawMode(e, tabContext));
    }

    private DrawingParamsChoiceDialog getDrawingParamsChoiceDialog(String format, ColorSpace colorSpace, int channel) {
        if (Objects.equals(format, "P5") || channel != 0) {
            return new SingleChannelDrawingParamsChoiceDialog(format, colorSpace, channel);
        }
        return new MultiChannelDrawingParamsChoiceDialog(format, colorSpace, channel);
    }

    private void getCoordinatesOnDrawMode(MouseEvent e, TabContext tabContext) {
        if (tabContext.getBeginPoint() == null) {
            tabContext.setBeginPoint(new Point(e.getX(), e.getY()));
        } else {
            DrawingAlgorithm algo = new WuAlgorithm();
            GrafixImage image = tabContext.getImage();
            float[] buff = algo.drawLine(image, tabContext.getBeginPoint(), new Point(e.getX(), e.getY()), tabContext.getDrawingContext());
            image.setData(image.getColorSpace().fromRGB(GammaCorrecter.convertGamma(image.getGamma(), 1, buff)));
            buff = image.getColorSpace().fromRGB(buff);
            ImageView iv = displayImage(image.getFormat(), image.getColorSpace().toRGB(ChannelDecomposer.decompose(buff, image.getChannel(), image.getColorSpace())), image.getWidth(), image.getHeight());
            iv.setOnMouseClicked(event -> getCoordinatesOnDrawMode(event, tabContext));
            tabContext.setBeginPoint(null);
        }
        System.out.println(e.getX());
    }

    public void unsetDrawMode() {
        Tab activeTab = getActiveTab();
        if (activeTab == null) {
            return;
        }

        ScrollPane scrollPane = (ScrollPane) getActiveTab().getContent();
        getImageViewFromScrollPane(scrollPane).setOnMouseClicked(null);
    }


    private void updateHistogramIfExists() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        HistogramWindow histogram = imageToHistogramMap.get(image);
        if (histogram != null) {
            List<ImageHistogramData> newHistogramData = ImageHistogramExtractor.getImageHistogramData(image);
            histogram.updateHistogram(newHistogramData);
        }

    }

    public void showHistogram() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }

        List<ImageHistogramData> histogramData = ImageHistogramExtractor.getImageHistogramData(image);

        HistogramWindow histogramWindow = new HistogramWindow(histogramData, image);
        imageToHistogramMap.put(image, histogramWindow);
        histogramWindow.showAndWait();
    }

    public void autocorrect() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }

        Double autocorrectionParameter = AutocorrectionInputDialog.getAutocorrectionInput();
        if (autocorrectionParameter == null) {
            return;
        }
        if (isAutocorrectionParameterOutOfBounds(autocorrectionParameter)) {
            throw new InvalidAutocorrectionException(String.valueOf(autocorrectionParameter));
        }
        boolean flag = false;
        if (Objects.equals(image.getColorSpace().toString(), "HSV")) {
            image.convertTo(new RGB());
            flag = true;
        }
        image = AutoCorrecter.applyAutocorrection(image, autocorrectionParameter);

        tabMapping.get(getActiveTab().getId()).setImage(image);
        displayImage(image.getFormat(), image.getData(), image.getWidth(), image.getHeight());
        if (flag) {
            image.convertTo(new HSV());
        }
    }

    public void chooseScalingParams() {
        GrafixImage image = getActiveTabImage();
        if (image == null) {
            return;
        }
        ScalingParamsChoiceDialog dialog = new ScalingParamsChoiceDialog(scalingMethods, image.getWidth(), image.getHeight());
        ScalingParams scalingParams = dialog.showAndWait().orElse(null);
        if (scalingParams == null) {
            return;
        }
        GrafixImage newImage;
        if (scalingParams.getScalingMethod().getType() == ScalingType.BC_SPLINE) {
            BCsplineScaling bCsplineScaling = new BCsplineScaling();
            bCsplineScaling.setB(((BCsplineScalingParams) scalingParams).getB());
            bCsplineScaling.setC(((BCsplineScalingParams) scalingParams).getC());
            newImage = bCsplineScaling.applyScaling(image, scalingParams.getWidth(), scalingParams.getHeight(), scalingParams.getBiasX(), scalingParams.getBiasY());
        } else {
            newImage = scalingParams.getScalingMethod().applyScaling(image, scalingParams.getWidth(), scalingParams.getHeight(), scalingParams.getBiasX(), scalingParams.getBiasY());
        }
        tabMapping.get(getActiveTab().getId()).setImage(newImage);
        displayImage(newImage.getFormat(), newImage.getData(), newImage.getWidth(), newImage.getHeight());
    }
}