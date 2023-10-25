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
//        System.out.println("A: " + a.getX() + " " + a.getY());
//        System.out.println("B: " + b.getX() + " " + b.getY());
//        System.out.println("C: " + c.getX() + " " + c.getY());
//        System.out.println("D: " + d.getX() + " " + d.getY());

        float[] buffer = Arrays.copyOf(buff, buff.length);


//        int k = buff.length / (grafixImage.getWidth() * grafixImage.getHeight());
//        double highest = Math.max(Math.max(a.getY(), b.getY()), Math.max(c.getY(), d.getY()));
//        double lowest = Math.min(Math.min(a.getY(), b.getY()), Math.min(c.getY(), d.getY()));
//        double rightest = Math.max(Math.max(a.getX(), b.getX()), Math.max(c.getX(), d.getX()));
//        double leftest = Math.min(Math.min(a.getX(), b.getX()), Math.min(c.getX(), d.getX()));
//        for(double x = leftest; x < rightest; ++x){
//            for(double y = lowest; y < highest; ++y){
//
//                double p1 = product(x, y, aX, aY, bX, bY);
//                double p2 = product(x, y, bX, bY, cX, cY);
//                double p3 = product(x, y, cX, cY, dX, dY);
//                double p4 = product(x, y, dX, dY, aX, aY);
//
//                if ((p1 < 0 && p2 < 0 && p3 < 0 && p4 < 0) ||
//                        (p1 > 0 && p2 > 0 && p3 > 0 && p4 > 0)){
//                    fillBuffer(buff, (int) x, (int) y, k, 1, grafixImage.getWidth(), drawingParams, 1);
//                }
//            }
//        }

        drawLineWith1Thickness(buff, grafixImage, a, b, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, b, c, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, c, d, drawingParams, 1);
        drawLineWith1Thickness(buff, grafixImage, d, a, drawingParams, 1);
//        double abDistance = getBetweenDistance(aX, aY, bX, bY);
//        double step = abDistance / drawingParams.getThickness();
//        c = getPointWithRDistanceFromBeginning(c, d, 1.0);
//        b = getPointWithRDistanceFromBeginning(b, a, 1.0);
        for (float i = drawingParams.getThickness() * 2 - 1; i > 0; --i) {
//            defaultLineNoAliasingDrawing(buff, buffer, grafixImage, c, b, drawingParams);
//            drawLineWith1Thickness(buff, grafixImage, a, d, drawingParams, 1);
            c = getPointWithRDistanceFromBeginning(c, d, 0.5);
            b = getPointWithRDistanceFromBeginning(b, a, 0.5);
            defaultLineNoAliasingDrawing(buff, buffer, grafixImage, c, b, drawingParams);

        }

        return buff;
    }

//        double abDistance = Math.abs((aX * aX + bY * bY) - (bX * bX + bY * bY));
//        double step = abDistance / drawingParams.getThickness();
//        for (float i = drawingParams.getThickness(); i > 1e-6; --i) {
//            drawLineWith1Thickness(buff, grafixImage, a, d, drawingParams, 1);
//            d = getPointWithRDistanceFromBeginning(d, c, step);
//            a = getPointWithRDistanceFromBeginning(a, b, step);
//
//        }
//        drawLineWith1Thickness(buff, grafixImage, a, b, drawingParams, 1);
//        drawLineWith1Thickness(buff, grafixImage, b, c, drawingParams, 1);
//        drawLineWith1Thickness(buff, grafixImage, c, d, drawingParams, 1);
//        drawLineWith1Thickness(buff, grafixImage, d, a, drawingParams, 1);

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

//        boolean steep = Math.abs(y0 - y1) > Math.abs(x0 - x1);
//        if (steep) {
//            double temp = x0;
//            x0 = y0;
//            y0 = temp;
//
//            temp = x1;
//            x1 = y1;
//            y1 = temp;
//        }
//
//        if (x0 > x1) {
//            getPointWithRDistanceFromBeginning(endPoint, beginPoint, r);
//        }

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

    private void plotBresenham(float[] buffer, DrawingParams params, int k, Point beginPoint, Point endPoint, int width) {
        double x0 = beginPoint.getX();
        double x1 = endPoint.getX();
        double y0 = beginPoint.getY();
        double y1 = endPoint.getY();

        double dx = Math.abs(x1 - x0);
        double sx = x0 < x1 ? 1 : -1;
        double dy = -Math.abs(y1 - y0);
        double sy = y0 < y1 ? 1 : -1;
        double error = dx + dy;
        while (x0 < x1 && y0 < y1) {
            fillBuffer(buffer, (int) x0, (int) y0, k, 1, width, params, 1);
            double error2 = 2 * error;
            if (error2 >= dy) {
                error += dy;
                x0 += sx;
            }
            if (error2 <= dx) {
                error += dx;
                y0 += sy;
            }
        }
    }

    private double product(double Px, double Py, double Ax, double Ay, double Bx, double By) {
        return (Bx - Ax) * (Py - Ay) - (By - Ay) * (Px - Ax);
    }
}
