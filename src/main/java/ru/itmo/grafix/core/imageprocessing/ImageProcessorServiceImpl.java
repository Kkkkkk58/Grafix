package ru.itmo.grafix.core.imageprocessing;

import ru.itmo.grafix.core.colorspace.ColorSpace;
import ru.itmo.grafix.core.colorspace.implementation.RGB;
import ru.itmo.grafix.core.exception.ByteReaderException;
import ru.itmo.grafix.core.exception.ByteWriterException;
import ru.itmo.grafix.core.exception.UnsupportedImageFormatException;
import ru.itmo.grafix.core.image.GrafixImage;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.*;
import java.util.zip.Inflater;

public class ImageProcessorServiceImpl implements ImageProcessorService {
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
            return new GrafixImage(new String(format), width, height, 255, FbConverter.convertBytesToFloat(buf, maxVal), absolutePath, headerSize, new RGB());
        } catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    private GrafixImage openPng(String absolutePath, FileInputStream br) {
        int headerInfoSize = 4;
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()){
            IHDR params = null;
            byte[] format = new byte[6];
            int count = br.read(format);
            if (count != 6 && (format[0] != 0x4e || format[1] != 0x47 || format[2] != 0x0D || format[3] != 0x0A
                    || format[4] != 0x1A || format[5] != 0x0A)) {
                throw new UnsupportedImageFormatException();
            }
            String type = "IHDR";
            Map<Integer, byte[]> plt = null;
            while(!Objects.equals(type, "IEND")) {
                byte[] headerInfoBuffer = new byte[headerInfoSize];
                count = br.read(headerInfoBuffer);
                int length = ByteBuffer.wrap(headerInfoBuffer).getInt();
                count = br.read(headerInfoBuffer);
                type = new String(headerInfoBuffer);
                byte[] buffer = new byte[length];
                count = br.read(buffer);
                count = br.read(headerInfoBuffer);
                if(Objects.equals(type, "IHDR")){
                    params = readIHDR(buffer);
                } else if (Objects.equals(type, "PLTE")) {
                    if(length % 3 != 0 || length > 256 * 3 || params == null || params.getColorType() == 0){
                        throw new UnsupportedImageFormatException();
                    }
                    plt = processPalette(buffer);
                } else if (Objects.equals(type, "IDAT")) {
                    if(params == null){
                        throw new UnsupportedImageFormatException();
                    }
                    byte[] d = readIDAT(buffer, params.isCompression(), plt);
                    if(d != null) {
                        stream.write(d);
                    }
                    else{
                        stream.write(new byte[buffer.length]);
                    }
                } else if (Objects.equals(type, "gAMA")) {

                }
            }
            return new GrafixImage("PNG", params.getWidth(), params.getHeight(), 255, FbConverter.convertBytesToFloat(stream.toByteArray(), 255), absolutePath, 0, new RGB());
        } catch (IOException inputException) {
            throw new ByteReaderException();
        }
    }

    private IHDR readIHDR(byte[] chunk){
        int width = ByteBuffer.wrap(chunk, 0, 4).getInt();
        int height = ByteBuffer.wrap(chunk, 4, 4).getInt();
        int depth = (chunk[8] & 0xff);
        int colorType = (chunk[9] & 0xff);
        byte compression = chunk[10];
        return new IHDR(width, height, depth, colorType, compression != 0);
    }

    private byte[] readIDAT(byte[] chunk, boolean compression, Map<Integer, byte[]> plt){
        chunk = decompressData(chunk);
        if(plt == null){
            return chunk;
        }
        //TODO: grayscale
        byte[] newChunk = new byte[chunk.length * 3];
        for(int i = 0; i < newChunk.length; i += 3){
            newChunk[i] = plt.get((int) chunk[i / 3])[0];
            newChunk[i + 1] = plt.get((int) chunk[i / 3])[1];
            newChunk[i + 2] = plt.get((int) chunk[i / 3])[2];
        }
        return newChunk;
    }

    private Map<Integer, byte[]> processPalette(byte[] palette){
        Map<Integer, byte[]> plt = new HashMap<>(palette.length);
        int j = 0;
        for(int i = 0; i < palette.length; ++i){
            plt.put(i, new byte[]{palette[j], palette[j+1], palette[j+2]});
            j += 3;
        }
        return plt;
    }

    private byte[] decompressData(byte[] data){
        byte[] output = new byte[0];

        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);

        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[1024];
            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }
            output = o.toByteArray();
        } catch (Exception e) {
            output = data;
            e.printStackTrace();
        } finally {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decompresser.end();
        return output;
    }
}
