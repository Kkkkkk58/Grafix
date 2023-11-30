package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.image.GrafixImage;

import java.io.ByteArrayOutputStream;

public interface ImageProcessorService {
    GrafixImage open(String absolutePath, ColorSpace colorSpace);

    ByteArrayOutputStream write(GrafixImage image);

    ByteArrayOutputStream write(GrafixImage image, String format);
}
