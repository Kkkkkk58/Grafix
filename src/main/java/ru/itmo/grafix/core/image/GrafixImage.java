package ru.itmo.grafix.core.image;

import ru.itmo.grafix.core.colorspace.ColorSpace;

public class GrafixImage {
    private String format;
    private int width;
    private int height;
    private int maxVal;
    private float[] data;
    private String path;
    private int headerSize;
    private ColorSpace colorSpace;
    private int channel;
    private float gamma;

    public GrafixImage(String format, int width, int height, int maxVal, float[] data, String path, int headerSize, ColorSpace colorSpace) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.maxVal = maxVal;
        this.data = data;
        this.path = path;
        this.headerSize = headerSize;
        this.colorSpace = colorSpace;
        this.channel = 0;
        this.gamma = 0f;
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float[] getData() {
        return data;
    }

    public String getPath() {
        return path;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public int getMaxVal() {
        return maxVal;
    }

    public ColorSpace getColorSpace() {
        return colorSpace;
    }

    public String getChannel(){
        if(channel == 0){
            return "all";
        }
        return String.valueOf(channel);
    }

    public float getGamma(){
        return gamma;
    }

    public void setChannel(int channel){
        this.channel = channel;
    }

    public void setGamma(float gamma){
        this.gamma = gamma;
    }

    public void convertTo(ColorSpace colorSpace) {
        if(colorSpace == this.getColorSpace()){
            return;
        }
        this.data =  colorSpace.fromRGB(this.colorSpace.toRGB(this.getData()));
        this.colorSpace = colorSpace;
        this.channel = 0;
    }

    public void setData(float[] newData){
        this.data = newData;
    }
}
