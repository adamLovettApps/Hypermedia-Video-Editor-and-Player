// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Frame Class
 * Stores all data necessary to display a raw frame
 */

public class Frame {
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int WIDTH = 352;
    public static final int HEIGHT = 288;
    
    private byte[] data;
    
    Frame(String fileName) throws IOException {
        File file = new File(fileName);
        this.data = new byte[(int)file.length()];
        DataInputStream dataStream = new DataInputStream(new FileInputStream(file));
        dataStream.readFully(data);
        dataStream.close();
    }
    
    /**
     * Provides BUffered Image data for color image
     * @return image data for display
     */
    public BufferedImage getFrameBytes() {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int ind = 0; 
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                byte r = data[ind + getColorOffset(RED)];
                byte g = data[ind + getColorOffset(GREEN)];
                byte b = data[ind + getColorOffset(BLUE)];
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x,y,pix);
                ind++;
            }
        } 
        return img;
    }
    
    private int getColorOffset(int color) {
        return HEIGHT * WIDTH * color;
    }   
}
