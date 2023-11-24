package ru.itmo.grafix.ui.components.scrollpane;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ZoomableScrollPane extends ScrollPane {
    private double scaleValue = 1;
    private double zoomIntensity = 0.02;
    private Node target;
    private Node zoomNode;

    public ZoomableScrollPane() {
        super();
    }

    public void setTarget(Node target) {
        this.target = target;
        ImageView ht = (ImageView) target;
        double htt = ht.getImage().getHeight();
        this.zoomNode = new Group(target);
        setContent(outerNode(zoomNode));

        setPannable(true);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setFitToHeight(true);
        setFitToWidth(true);

        updateScale();
    }

    private Node outerNode(Node node) {
        Node outerNode = centeredNode(node);
        outerNode.setOnScroll(e -> {
            e.consume();
            onScroll(e.getTextDeltaY(), new Point2D(e.getX(), e.getY()));
        });
        return outerNode;
    }

    private Node centeredNode(Node node) {
        StackPane vBox = new StackPane(node);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }

    private void updateScale() {
        target.setScaleX(scaleValue);
        target.setScaleY(scaleValue);
    }

    private void onScroll(double wheelDelta, Point2D mousePoint) {
        double zoomFactor = Math.exp(wheelDelta * zoomIntensity);

        Bounds innerBounds = zoomNode.getLayoutBounds();
        Bounds viewportBounds = getViewportBounds();

        double valX = this.getHvalue() * (innerBounds.getWidth() - viewportBounds.getWidth());
        double valY = this.getVvalue() * (innerBounds.getHeight() - viewportBounds.getHeight());

        scaleValue = scaleValue * zoomFactor;
        updateScale();
        this.layout();

        Point2D posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint));

        Point2D adjustment = target.getLocalToParentTransform().deltaTransform(posInZoomTarget.multiply(zoomFactor - 1));

        Bounds updatedInnerBounds = zoomNode.getBoundsInLocal();
        this.setHvalue((valX + adjustment.getX()) / (updatedInnerBounds.getWidth() - viewportBounds.getWidth()));
        this.setVvalue((valY + adjustment.getY()) / (updatedInnerBounds.getHeight() - viewportBounds.getHeight()));
    }
}