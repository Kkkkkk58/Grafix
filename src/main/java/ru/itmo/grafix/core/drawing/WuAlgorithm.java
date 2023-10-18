package ru.itmo.grafix.core.drawing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.models.DrawingParams;
import ru.itmo.grafix.ui.models.Point;

public class WuAlgorithm{

    public static float[] drawLine(GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams) {
        double x0 = beginPoint.getX();
        double x1 = endPoint.getX();
        double y0 = beginPoint.getY();
        double y1 = endPoint.getY();

        if(Math.abs(y0 - y1) > Math.abs(x0 - x1)){
            drawLine(grafixImage, new Point(y0, x0), new Point(y1, x1), drawingParams);
        }

        if(x0 > x1){
            drawLine(grafixImage, endPoint, beginPoint, drawingParams);
        }
       float[] buff =  grafixImage.getData();
        
//       for(int i = getPixelsCoordinates(beginPoint, grafixImage.getWidth()); i < getPixelsCoordinates(endPoint, grafixImage.getWidth()); ++i){
//           buff[i] = 0;
//       }
       return buff;
    }

//    private static int getPixelsCoordinates(Point point, int width){
//        return (int) (point.getY()) * 3 * (width - 1) + Math.round(3 * point.getX()));
//    }
}
