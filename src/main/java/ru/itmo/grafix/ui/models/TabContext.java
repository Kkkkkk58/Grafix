package ru.itmo.grafix.ui.models;

import ru.itmo.grafix.core.image.GrafixImage;

public class TabContext {
    private GrafixImage image;
    private DrawingParams drawingContext;
    private Point beginPoint;

    public TabContext(GrafixImage image, DrawingParams drawingContext, Point beginPoint) {
        this.image = image;
        this.drawingContext = drawingContext;
        this.beginPoint = beginPoint;
    }

    public GrafixImage getImage() {
        return image;
    }

    public void setImage(GrafixImage image) {
        this.image = image;
    }

    public DrawingParams getDrawingContext() {
        return drawingContext;
    }

    public void setDrawingContext(DrawingParams drawingContext) {
        this.drawingContext = drawingContext;
    }

    public Point getBeginPoint() {
        return beginPoint;
    }

    public void setBeginPoint(Point point) {
        this.beginPoint = point;
    }
}
