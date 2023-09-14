package ru.itmo.grafix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ImageProcessorServiceImpl implements ImageProcessorService {
    @Override
    public GrafixImage open(String absolutePath) {
        try (FileInputStream br = new FileInputStream(absolutePath)) {
            byte[] format = new byte[2];
            int count = br.read(format);
            if (count != 2 || (char) format[0] != 'P' || ((char) format[1] != '5' && (char) format[1] != '6')) {
                throw new RuntimeException("Unsupported image format");
            }
            int headerSize = 2 + 1;
            String res = "";
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
            if(maxVal < 255){
                double multiplier = 255.0 / maxVal;
                for(int i = 0; i < bufSize; ++i){
                    buf[i] *= multiplier;
                }
            }
            return new GrafixImage(new String(format), width, height, 255, buf, absolutePath, headerSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteArrayOutputStream write(GrafixImage image) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(image.getHeaderSize() + image.getData().length + 1)) {
            String header = image.getFormat() + " " + image.getWidth() + " " + image.getHeight() + " " + image.getMaxVal() + " ";
            stream.write(header.getBytes());
            stream.write(image.getData());
            return stream;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
