package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.implementation.RGB;
import ru.itmo.grafix.core.exception.ByteReaderException;
import ru.itmo.grafix.core.exception.ByteWriterException;
import ru.itmo.grafix.core.exception.UnsupportedImageFormatException;
import ru.itmo.grafix.core.image.GrafixImage;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class ImageProcessorServiceImpl implements ImageProcessorService {

    private static final int PNG_GAMMA_DIVISOR = 100000;
    @Override
    public GrafixImage open(String absolutePath, ColorSpace colorSpace) {
        try (FileInputStream br = new FileInputStream(absolutePath)) {
            byte[] format = new byte[2];
            int count = br.read(format);
            if (count == 2 && (char) format[0] == 'P' && ((char) format[1] == '5' || (char) format[1] == '6')) {
                return openPnm(format, absolutePath, br);
            } else if (format[0] == (byte) 0x89 && format[1] == (byte) 0x50) {
                return openPng(absolutePath, br);
            }
            throw new UnsupportedImageFormatException();
        } catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    @Override
    public ByteArrayOutputStream write(GrafixImage image) {
        image = ChannelDecomposer.getDecomposedImage(image);
        String header =
                image.getFormat() + " " + image.getWidth() + " " + image.getHeight() + " " + image.getMaxVal() + " ";
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

    private GrafixImage openPnm(byte[] format, String absolutePath, FileInputStream br) {
        try {
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
            return new GrafixImage(new String(format), width, height, 255, FbConverter.convertBytesToFloat(buf,
                    maxVal), absolutePath, headerSize, new RGB());
        } catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    private GrafixImage openPng(String absolutePath, FileInputStream br) {
        int headerInfoSize = 4;
        try (Decompressor decompressor = new Decompressor()) {
            IHDR params = null;
            byte[] format = new byte[6];
            int count = br.read(format);
            if (count != 6 && (format[0] != 0x4e || format[1] != 0x47 || format[2] != 0x0D || format[3] != 0x0A
                    || format[4] != 0x1A || format[5] != 0x0A)) {
                throw new UnsupportedImageFormatException();
            }
            String type = "IHDR";
            Map<Integer, byte[]> plt = null;
            float gamma = 1f;
            while (!Objects.equals(type, "IEND")) {
                byte[] headerInfoBuffer = new byte[headerInfoSize];
                count = br.read(headerInfoBuffer);
                int length = ByteBuffer.wrap(headerInfoBuffer).getInt();
                count = br.read(headerInfoBuffer);
                type = new String(headerInfoBuffer);
                byte[] buffer = new byte[length];
                count = br.read(buffer);
                count = br.read(headerInfoBuffer);
                if (Objects.equals(type, "IHDR")) {
                    params = readIHDR(buffer);
                } else if (Objects.equals(type, "PLTE")) {
                    if (length % 3 != 0 || length > 256 * 3 || params == null || params.getColorType() == 0) {
                        throw new UnsupportedImageFormatException();
                    }
                    plt = processPalette(buffer);
                } else if (Objects.equals(type, "IDAT")) {
                    if (params == null) {
                        throw new UnsupportedImageFormatException();
                    }
                    decompressor.addData(buffer);
                } else if (Objects.equals(type, "gAMA")) {
                    gamma = readGAMMA(buffer);
                }
            }

            if (params == null) {
                throw new UnsupportedImageFormatException();
            }
            byte[] decoded = decompressor.decode();
            byte[] imageData = getImageData(decoded, params.getWidth(), params.getHeight(), params.getColorType(), plt);

            GrafixImage image = new GrafixImage("PNG", params.getWidth(), params.getHeight(), 255,
                    FbConverter.convertBytesToFloat(imageData, 255), absolutePath, 0, new RGB());
            image.setGamma(gamma);
            return image;
        } catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    private IHDR readIHDR(byte[] chunk) {
        int width = ByteBuffer.wrap(chunk, 0, 4).getInt();
        int height = ByteBuffer.wrap(chunk, 4, 4).getInt();
        int depth = (chunk[8] & 0xff);
        int colorType = (chunk[9] & 0xff);
        byte compression = chunk[10];
        return new IHDR(width, height, depth, colorType, compression != 0);
    }

    private float readGAMMA(byte[] buffer) {
        return (float) ByteBuffer.wrap(buffer).getInt() / PNG_GAMMA_DIVISOR;
    }

    private Map<Integer, byte[]> processPalette(byte[] palette) {
        Map<Integer, byte[]> plt = new HashMap<>(palette.length);
        int j = 0;
        for (int i = 0; i < palette.length; ++i) {
            plt.put(i, new byte[]{palette[j], palette[j + 1], palette[j + 2]});
            j += 3;
        }
        return plt;
    }

    private byte[] getImageData(byte[] decoded, int width, int height, int colorType, Map<Integer, byte[]> plt) {
        // TODO refactor
        int bytesPerPixel = (colorType == 0) ? 1 : 3;
        byte[] data = new byte[width * height * bytesPerPixel];
        int i = 0;
        for (int scanline = 0; scanline < height; ++scanline) {
            int filterType = decoded[bytesPerPixel * scanline * width + scanline] & 0xff;
            System.out.println(filterType);
            for (int pixel = 0; pixel < width; ++pixel) {
                if (plt == null) {
                    for (int channel = 0; channel < bytesPerPixel; ++channel) {
                        data[i + channel] = getReverseFilteredValue(decoded, data, filterType, scanline, pixel, channel, width, bytesPerPixel);
                    }
                } else {
                    int paletteColor = decoded[getPngValueIndex(scanline, pixel, 0, width, bytesPerPixel)] & 0xff;
                    byte[] color = plt.get(paletteColor);
                    System.arraycopy(color, 0, data, i, bytesPerPixel);
                }
                i += bytesPerPixel;
            }
        }

        return data;
    }

    private byte getReverseFilteredValue(byte[] decoded, byte[] reversed, int filterType, int scanline, int pixel, int channel, int width, int bytesPerPixel) {
        int value = decoded[getPngValueIndex(scanline, pixel, channel, width, bytesPerPixel)] & 0xff;

        // TODO refactor, separate filters into classes
        return switch (filterType) {
            case 1 -> getReverseSubFilterValue(value, reversed, scanline, pixel, channel, width, bytesPerPixel);
            case 2 -> getReverseUpFilterValue(value, reversed, scanline, pixel, channel, width, bytesPerPixel);
            case 3 -> getReverseAverageFilterValue(value, reversed, scanline, pixel, channel, width, bytesPerPixel);
            case 4 -> getReversePaethFilterValue(value, reversed, scanline, pixel, channel, width, bytesPerPixel);
            default -> (byte) value;
        };
    }

    private byte getReversePaethFilterValue(int value, byte[] reversed, int scanline, int pixel, int channel, int width, int bytesPerPixel) {
        // TODO check types (bytes or ints)
        int left = (pixel == 0) ? 0 : reversed[getRgbValueIndex(scanline, pixel - 1, channel, width, bytesPerPixel)] & 0xff;
        int upper = (scanline == 0) ? 0 : reversed[getRgbValueIndex(scanline - 1, pixel, channel, width, bytesPerPixel)] & 0xff;
        int leftUpper = (scanline == 0 || pixel == 0) ? 0 : reversed[getRgbValueIndex(scanline - 1, pixel - 1, channel, width, bytesPerPixel)] & 0xff;
        int paeth = getPaethValue(left, upper, leftUpper);

        return (byte) (value + paeth);
    }

    private int getPaethValue(int a, int b, int c) {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc) {
            return a;
        } else if (pb <= pc) {
            return b;
        }
        return c;
    }

    private byte getReverseAverageFilterValue(int value, byte[] reversed, int scanline, int pixel, int channel, int width, int bytesPerPixel) {
        int left = (pixel == 0) ? 0 : reversed[getRgbValueIndex(scanline, pixel - 1, channel, width, bytesPerPixel)] & 0xff;
        int upper = (scanline == 0) ? 0 : reversed[getRgbValueIndex(scanline - 1, pixel, channel, width, bytesPerPixel)] & 0xff;
        int average = (left + upper) / 2;
        return (byte) (value + average);
    }

    private byte getReverseUpFilterValue(int value, byte[] reversed, int scanline, int pixel, int channel, int width, int bytesPerPixel) {
        int up = ((scanline == 0) ? 0 : reversed[getRgbValueIndex(scanline - 1, pixel, channel, width, bytesPerPixel)] & 0xff);
        return (byte) (value + up);
    }

    private byte getReverseSubFilterValue(int value, byte[] reversed, int scanline, int pixel, int channel, int width, int bytesPerPixel) {
        int sub = ((pixel == 0) ? 0 : reversed[getRgbValueIndex(scanline, pixel - 1, channel, width, bytesPerPixel)] & 0xff);
        return (byte) (value + sub);
    }

    private int getPngValueIndex(int row, int col, int channel, int width, int bytesPerPixel) {
        return getRgbValueIndex(row, col, channel, width, bytesPerPixel) + row + 1;
    }

    private int getRgbValueIndex(int row, int col, int channel, int width, int bytesPerPixel) {
        return bytesPerPixel * (row * width + col) + channel;
    }


    // TODO remove from this class
    private static class Decompressor implements Closeable {
        private final ByteArrayOutputStream baos;
        private final Inflater inflater;

        public Decompressor() {
            this.baos = new ByteArrayOutputStream();
            this.inflater = new Inflater();
        }

        public void addData(byte[] data) throws IOException {
            baos.write(data);
        }

        public byte[] decode() {
            try (ByteArrayOutputStream out = new ByteArrayOutputStream(baos.size())) {
                inflater.setInput(baos.toByteArray());
                byte[] buf = new byte[1024];
                while (!inflater.finished()) {
                    int i = inflater.inflate(buf);
                    out.write(buf, 0, i);
                }

                return out.toByteArray();
            } catch (IOException | DataFormatException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() throws IOException {
            baos.close();
            inflater.end();
        }
    }
}
