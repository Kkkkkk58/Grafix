package ru.itmo.grafix.core.drawing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.ui.models.DrawingParams;
import ru.itmo.grafix.ui.models.Point;

public interface DrawingAlgorithm {
    float[] drawLine(GrafixImage grafixImage, Point beginPoint, Point endPoint, DrawingParams drawingParams);
}
