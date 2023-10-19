package ru.itmo.grafix.core.drawing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.models.DrawingParams;
import ru.itmo.grafix.ui.models.Point;

public class WuAlgorithm implements DrawingAlgorithm {

    public float[] drawLine(GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams) {
        float[] buff = grafixImage.getColorSpace().toRGB(grafixImage.getData());
        if (Math.abs(drawingParams.getThickness() - 1.0) < 1e-6) {
            drawLineWith1Thickness(buff, grafixImage, beginPoint, endPoint, drawingParams);
            return buff;
        }

        // Calculating rectangle points
        double dx = endPoint.getX() - beginPoint.getX();
        double dy = endPoint.getY() - beginPoint.getY();
        double gradient = (dx == 0) ? 1.0 : dy / dx;
        double perpGradient = -1 / gradient;
        double yIntersect = beginPoint.getY() - perpGradient * beginPoint.getX();
        double r = drawingParams.getThickness() / 2.0;
        double factor = Math.sqrt(r * r / (1 + perpGradient * perpGradient));
        double aX = beginPoint.getX() + factor;
        double bX = beginPoint.getX() - factor;
        double aY = perpGradient * aX + yIntersect;
        double bY = perpGradient * bX + yIntersect;
        double deltaX = Math.abs(beginPoint.getX() - aX);
        yIntersect = endPoint.getY() - perpGradient * endPoint.getX();
        double cX = endPoint.getX() - deltaX;
        double dX = endPoint.getX() + deltaX;
        double cY = perpGradient * cX + yIntersect;
        double dY = perpGradient * dX + yIntersect;
        Point a = new Point(aX, aY);
        Point b = new Point(bX, bY);
        Point c = new Point(cX, cY);
        Point d = new Point(dX, dY);

        // Drawing rectangle contour
        drawLineWith1Thickness(buff, grafixImage, a, b, drawingParams);
        drawLineWith1Thickness(buff, grafixImage, b, c, drawingParams);
        drawLineWith1Thickness(buff, grafixImage, c, d, drawingParams);
        drawLineWith1Thickness(buff, grafixImage, d, a, drawingParams);

        // Drawing inside lines
        double abDistance = Math.abs((aX * aX + bY * bY) - (bX * bX + bY * bY));
        double step = abDistance / drawingParams.getThickness();
        for (float i = drawingParams.getThickness(); i > 1e-6; --i) {
            drawLineWith1Thickness(buff, grafixImage, a, d, drawingParams);
            d = getPointWithRDistanceFromBeginning(d, c, step);
            a = getPointWithRDistanceFromBeginning(a, b, step);
        }

        return buff;
    }
    public void drawLineWith1Thickness(float[] buff, GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams) {
        double x0 = beginPoint.getX();
        double x1 = endPoint.getX();
        double y0 = beginPoint.getY();
        double y1 = endPoint.getY();

        boolean steep = Math.abs(y0 - y1) > Math.abs(x0 - x1);
        if (steep) {
            double temp = x0;
            x0 = y0;
            y0 = temp;

            temp = x1;
            x1 = y1;
            y1 = temp;
        }

        if (x0 > x1) {
            drawLineWith1Thickness(buff, grafixImage, endPoint, beginPoint, drawingParams);
        }
        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = (dx == 0) ? 1.0 : dy / dx;


        int beginX = (int) Math.round(x0);
        double beginY = y0 + gradient * (beginX - x0);
        double gapX = 1 - fractionPart(x0 + 0.5);
        double gapY = 1 - fractionPart(beginY);
        double intersectionY = beginY + gradient;
        int k = buff.length / (grafixImage.getWidth() * grafixImage.getHeight());
        if (steep) {
            fillBuffer(buff, (int) beginY, beginX, k, gapY * gapX, grafixImage.getWidth(), drawingParams);
            fillBuffer(buff, (int) beginY + 1, beginX, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams);
        } else {
            fillBuffer(buff, beginX, (int) beginY, k, gapY * gapX, grafixImage.getWidth(), drawingParams);
            fillBuffer(buff, beginX, (int) beginY + 1, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams);
        }

        int endX = (int) Math.round(x1);
        double endY = y1 + gradient * (endX - x1);
        gapX = fractionPart(x1 + 0.5);
        gapY = 1 - fractionPart(endY);
        if (steep) {
            fillBuffer(buff, (int) endY, endX, k, gapY * gapX, grafixImage.getWidth(), drawingParams);
            fillBuffer(buff, (int) (endY) + 1, endX, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams);
        } else {
            fillBuffer(buff, endX, (int) endY, k, gapY * gapX, grafixImage.getWidth(), drawingParams);
            fillBuffer(buff, endX, (int) (endY) + 1, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams);
        }

        for (int i = beginX + 1; i < endX; ++i) {
            if (steep) {
                fillBuffer(buff, (int) (intersectionY), i, k, 1 - fractionPart(intersectionY), grafixImage.getWidth(), drawingParams);
                fillBuffer(buff, (int) (intersectionY) + 1, i, k, fractionPart(intersectionY), grafixImage.getWidth(), drawingParams);
            } else {
                fillBuffer(buff, i, (int) (intersectionY), k, 1 - fractionPart(intersectionY), grafixImage.getWidth(), drawingParams);
                fillBuffer(buff, i, (int) (intersectionY) + 1, k, fractionPart(intersectionY), grafixImage.getWidth(), drawingParams);
            }
            intersectionY += gradient;
        }
    }

    private double fractionPart(double param) {
        return param - Math.floor(param);
    }

    private int getPixelsCoordinates(int x, int y, int width, int k) {
        return k * (y * width + x);
    }

    private void fillBuffer(float[] buffer, int x, int y, int k, double intensity, int width, DrawingParams params) {
        int coordinate = getPixelsCoordinates(x, y, width, k);
        for (int i = 0; i < k; ++i) {
            float bgColor = buffer[coordinate + i];
            float lineColor = blendColors(bgColor, (params.getColor()[i] & 0xff) / 255f, intensity);
            buffer[coordinate + i] = blendColors(bgColor, lineColor, params.getOpacity());
        }
    }

    private float blendColors(float bgColor, float color, double intensity) {
        return (float) ((1 - intensity) * bgColor + intensity * color);
    }

    private Point getPointWithRDistanceFromBeginning(Point beginPoint, Point endPoint, double r) {
        double dx = endPoint.getX() - beginPoint.getX();
        double dy = endPoint.getY() - beginPoint.getY();
        double gradient = (dx == 0) ? 1.0 : dy / dx;
        double yIntersect = beginPoint.getY() - gradient * beginPoint.getX();
        double factor = Math.sqrt(r * r / (1 + gradient * gradient));
        double aX = beginPoint.getX() + factor;
        double aY = gradient * aX + yIntersect;
        double distanceToEnd = Math.pow(endPoint.getX(), 2) + Math.pow(endPoint.getY(), 2);
        double distanceToBeginning = Math.pow(beginPoint.getX(), 2) + Math.pow(beginPoint.getY(), 2);
        if (Math.abs(distanceToEnd - distanceToBeginning) < Math.abs(distanceToEnd - (aX * aX + aY * aY))) {
            aX = beginPoint.getX() - factor;
            aY = gradient * aX + yIntersect;
        }
        return new Point(aX, aY);
    }
}
