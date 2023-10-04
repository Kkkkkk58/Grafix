package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.colorspace.ColorSpace;

import java.io.ByteArrayOutputStream;

public interface ImageProcessorService {
    GrafixImage open(String absolutePath, ColorSpace colorSpace);
    ByteArrayOutputStream write(GrafixImage image);
}
