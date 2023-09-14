package ru.itmo.grafix;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
            int width = Integer.parseInt(readUntilWhitespace(br));
            int height = Integer.parseInt(readUntilWhitespace(br));
            int maxVal = Integer.parseInt(readUntilWhitespace(br));
            int bufSize = width * height;
            if (format[1] == '6') {
                bufSize *= 3;
            }

            byte[] buf = new byte[bufSize];
            br.read(buf);
            return new GrafixImage(new String(format), width, height, maxVal, buf);
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
