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
            if(count != 2 || (char)format[0] != 'P' || ((char)format[1] != '5' && (char)format[1] != '6')) {
                throw new RuntimeException("Unsupported image format");
            }
            int width = Integer.parseInt(readUntilWhitespace(br));
            int height = Integer.parseInt(readUntilWhitespace(br));
            int maxVal = Integer.parseInt(readUntilWhitespace(br));
            int bufSize = width * height;
            if(format[1] == '6'){
                bufSize *= 3;
            }

            byte[] buf = new byte[bufSize];
            if(format[1] == '5'){
//                for(int i = 0; i < width * height; ++i){
//                    byte b = (byte) br.read();
//                    buf[i * 3] = b;
//                    buf[i * 3 + 1] = b;
//                    buf[i * 3 + 2] = b;
//                }
                br.read(buf);
            }
            else{br.read(buf);}
//            for (int i = 0; i < bufSize; ++i) {
//                byte c = (byte) br.read();
//                buf[i] = c;
//            }

            return new GrafixImage(new String(format), width, height, maxVal, buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readUntilWhitespace(InputStream br) throws IOException {
        char c = (char) br.read();
        while(Character.isWhitespace(c)){
            c = (char) br.read();
        }
        List<Character> buffer = new ArrayList<>();
        while(!Character.isWhitespace(c)){
            buffer.add(c);
            c = (char) br.read();
        }
        return buffer.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
