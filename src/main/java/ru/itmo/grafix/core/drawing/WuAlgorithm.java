package ru.itmo.grafix.core.drawing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.imageprocessing.GammaCorrecter;
import ru.itmo.grafix.ui.models.DrawingParams;
import ru.itmo.grafix.ui.models.Point;

import java.util.Arrays;

public class WuAlgorithm implements DrawingAlgorithm {

    public float[] drawLine(GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams) {
        float[] buff = grafixImage.getColorSpace().toRGB(grafixImage.getData());
        buff = GammaCorrecter.convertGamma(1, grafixImage.getGamma(), buff);
        if (Math.abs(drawingParams.getThickness() - 1.0) < 1e-6) {
            drawLineWith1Thickness(buff, grafixImage, beginPoint, endPoint, drawingParams, 1);
            return buff;
        }

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

        float[] buffer = Arrays.copyOf(buff, buff.length);


        drawLineWith1Thickness(buff, grafixImage, a, b, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, b, c, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, c, d, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, d, a, drawingParams, 1);

        for (float i = drawingParams.getThickness() * 2 - 1; i > 0; --i) {
            c = getPointWithRDistanceFromBeginning(c, d, 0.5);
            b = getPointWithRDistanceFromBeginning(b, a, 0.5);
            defaultLineNoAliasingDrawing(buff, buffer, grafixImage, c, b, drawingParams);
        }

        return buff;
    }

    public void defaultLineNoAliasingDrawing(float[] buff, float[] buffer, GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams) {
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
            defaultLineNoAliasingDrawing(buff, buffer, grafixImage, endPoint, beginPoint, drawingParams);
            return;
        }

        double dx = x1 - x0;
        double dy = y1 - y0;
        double step = Math.max(Math.abs(dx), Math.abs(dy));
        dx = dx / step;
        dy = dy / step;
        double x = x0 + dx;
        double y = y0 + dy;
        int k = buff.length / (grafixImage.getWidth() * grafixImage.getHeight());
        for (double i = 1; i <= step; ++i) {
            int iX = (int) Math.round(x);
            int iY = (int) Math.round(y);
            int coordinate;
            int secondCoordinate;
            if (steep) {
                coordinate = getPixelsCoordinates(iY, iX, grafixImage.getWidth(), k);
                secondCoordinate = getPixelsCoordinates(iY, iX + 1, grafixImage.getWidth(), k);

            } else {
                coordinate = getPixelsCoordinates(iX, iY, grafixImage.getWidth(), k);
                secondCoordinate = getPixelsCoordinates(iX + 1, iY, grafixImage.getWidth(), k);
            }
            for (int j = 0; j < k; ++j) {
                float c = (drawingParams.getColor()[j] & 0xff) / 255f;
                buff[coordinate + j] = blendColors(buffer[coordinate + j], c, drawingParams.getOpacity());
                buff[secondCoordinate + j] = blendColors(buffer[secondCoordinate + j], c, drawingParams.getOpacity());
            }
            x += dx;
            y += dy;
//            if (steep) {
//                fillBuffer(buff, iY, iX, k, 1, grafixImage.getWidth(), drawingParams, 1);
//            } else {
//                fillBuffer(buff, iX, iY, k, 1, grafixImage.getWidth(), drawingParams, 1);
//            }
        }
    }

    public void wuNoAliasingDrawing(float[] buff, GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams, float coeff) {
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
            wuNoAliasingDrawing(buff, grafixImage, endPoint, beginPoint, drawingParams, coeff);
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
            fillBuffer(buff, (int) beginY, beginX, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, (int) beginY + 1, beginX, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
        } else {
            fillBuffer(buff, beginX, (int) beginY, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, beginX, (int) beginY + 1, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
        }

        int endX = (int) Math.round(x1);
        double endY = y1 + gradient * (endX - x1);
        gapX = fractionPart(x1 + 0.5);
        gapY = 1 - fractionPart(endY);
        if (steep) {
            fillBuffer(buff, (int) endY, endX, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, (int) (endY) + 1, endX, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
        } else {
            fillBuffer(buff, endX, (int) endY, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, endX, (int) (endY) + 1, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
        }

        for (int i = beginX + 1; i < endX; ++i) {
            if (steep) {
                fillBuffer(buff, (int) (intersectionY), i, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
                fillBuffer(buff, (int) (intersectionY) + 1, i, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            } else {
                fillBuffer(buff, i, (int) (intersectionY), k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
                fillBuffer(buff, i, (int) (intersectionY) + 1, k, 1.0, grafixImage.getWidth(), drawingParams, coeff);
            }
            intersectionY += gradient;
        }
    }

    public void drawLineWith1Thickness(float[] buff, GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams, float coeff) {
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
            drawLineWith1Thickness(buff, grafixImage, endPoint, beginPoint, drawingParams, coeff);
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
            fillBuffer(buff, (int) beginY, beginX, k, gapY * gapX, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, (int) beginY + 1, beginX, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams, coeff);
        } else {
            fillBuffer(buff, beginX, (int) beginY, k, gapY * gapX, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, beginX, (int) beginY + 1, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams, coeff);
        }

        int endX = (int) Math.round(x1);
        double endY = y1 + gradient * (endX - x1);
        gapX = fractionPart(x1 + 0.5);
        gapY = 1 - fractionPart(endY);
        if (steep) {
            fillBuffer(buff, (int) endY, endX, k, gapY * gapX, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, (int) (endY) + 1, endX, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams, coeff);
        } else {
            fillBuffer(buff, endX, (int) endY, k, gapY * gapX, grafixImage.getWidth(), drawingParams, coeff);
            fillBuffer(buff, endX, (int) (endY) + 1, k, (1 - gapY) * gapX, grafixImage.getWidth(), drawingParams, coeff);
        }

        for (int i = beginX + 1; i < endX; ++i) {
            if (steep) {
                fillBuffer(buff, (int) (intersectionY), i, k, 1 - fractionPart(intersectionY), grafixImage.getWidth(), drawingParams, coeff);
                fillBuffer(buff, (int) (intersectionY) + 1, i, k, fractionPart(intersectionY), grafixImage.getWidth(), drawingParams, coeff);
            } else {
                fillBuffer(buff, i, (int) (intersectionY), k, 1 - fractionPart(intersectionY), grafixImage.getWidth(), drawingParams, coeff);
                fillBuffer(buff, i, (int) (intersectionY) + 1, k, fractionPart(intersectionY), grafixImage.getWidth(), drawingParams, coeff);
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

    private void fillBuffer(float[] buffer, int x, int y, int k, double intensity, int width, DrawingParams params, float coeff) {
        intensity *= coeff;
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
        double x0 = beginPoint.getX();
        double x1 = endPoint.getX();
        double y0 = beginPoint.getY();
        double y1 = endPoint.getY();

        double dx = x1 - x0;
        double dy = y1 - y0;
        double gradient = (dx == 0) ? 1.0 : dy / dx;
        double yIntersect = y0 - gradient * x0;
        double factor = Math.sqrt(r * r / (1 + gradient * gradient));
        double aX = x0 + factor;
        double aY = gradient * aX + yIntersect;
        if (getBetweenDistance(x0, y0, x1, y1) < getBetweenDistance(aX, aY, x0, y0)) {
            aX = x0 - factor;
            aY = gradient * aX + yIntersect;
        }
        return new Point(aX, aY);
    }

    private double getBetweenDistance(double aX, double aY, double bX, double bY) {
        return Math.sqrt(Math.pow(aX - bX, 2) + Math.pow(aY - bY, 2));
    }
}
