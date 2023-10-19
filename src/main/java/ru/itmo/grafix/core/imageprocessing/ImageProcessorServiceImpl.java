package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.image.GrafixImage;
import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.exception.ByteReaderException;
import ru.itmo.grafix.core.exception.ByteWriterException;
import ru.itmo.grafix.core.exception.UnsupportedImageFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessorServiceImpl implements ImageProcessorService {
    @Override
    public GrafixImage open(String absolutePath, ColorSpace colorSpace) {
        try (FileInputStream br = new FileInputStream(absolutePath)) {
            byte[] format = new byte[2];
            int count = br.read(format);
            if (count != 2 || (char) format[0] != 'P' || ((char) format[1] != '5' && (char) format[1] != '6')) {
                throw new UnsupportedImageFormatException();
            }
            int headerSize = 2 + 1;
            String res;
            int width = Integer.parseInt(res = readUntilWhitespace(br));
            headerSize += res.length() + 1;
            int height = Integer.parseInt(res = readUntilWhitespace(br));
            headerSize += res.length() + 1;
            int maxVal = Integer.parseInt(res = readUntilWhitespace(br));
            headerSize += 3 + 1;
            int bufSize = width * height;
            if (format[1] == '6') {
                bufSize *= 3;
            }

            byte[] buf = new byte[bufSize];
            br.read(buf);
            return new GrafixImage(new String(format), width, height, 255, FbConverter.convertBytesToFloat(buf, maxVal), absolutePath, headerSize, colorSpace);
        }
        catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    @Override
    public ByteArrayOutputStream write(GrafixImage image) {
        image = ChannelDecomposer.getDecomposedImage(image);
        String header = image.getFormat() + " " + image.getWidth() + " " + image.getHeight() + " " + image.getMaxVal() + " ";
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(header.length() + image.getData().length + 1)) {
            stream.write(header.getBytes());
            stream.write(FbConverter.convertFloatToByte(image.getData()));
            return stream;
        } catch (IOException outputException) {
            throw new ByteWriterException();
        }
    }

    private String readUntilWhitespace(InputStream br) throws IOException {
        char c = (char) br.read();
        while (Character.isWhitespace(c)) {
            c = (char) br.read();
        }
        List<Character> buffer = new ArrayList<>();
        while (!Character.isWhitespace(c)) {
            buffer.add(c);
            c = (char) br.read();
        }
        return buffer.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
