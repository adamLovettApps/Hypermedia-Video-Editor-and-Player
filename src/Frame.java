// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Rectangle;

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
    public static final float BRIGHTNESS_FACTOR = 0.25f;
    //public static final float BRIGHTNESS_FACTOR2 = 0.6f;
    private static Rectangle currentRect;
    
    private byte[] data;
    
    Frame(File file) throws IOException {
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
    
    
//    public BufferedImage getFrameBytes(VideoLink[] links) {
//        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
//        int ind = 0;
//        float[] hsbvals = { 0, 0, 0 };
//        for(int y = 0; y < HEIGHT; y++){
//            for(int x = 0; x < WIDTH; x++){
//                byte r = data[ind + getColorOffset(RED)];
//                byte g = data[ind + getColorOffset(GREEN)];
//                byte b = data[ind + getColorOffset(BLUE)];
//                
//                
//                for (int i = 0; i < links.length; i++) {
//                	if (links[i] == null) {
//		    			break;
//		    		}
//		    		else {
//		    			if (links[i].getLinkLocation().contains(x,y)) {
//		    				
//		    				//Convert colors to HSB Color Space
//		    				Color.RGBtoHSB(ByteToInt(r), ByteToInt(g), ByteToInt(b), hsbvals );
//		    				//Brighten
//		    				float newBrightness;
//		    				if (hsbvals[2] > 0.5) {
//		    					newBrightness = BRIGHTNESS_FACTOR2 * hsbvals[2];
//		    				}
//		    				else {
//		    					newBrightness = BRIGHTNESS_FACTOR * hsbvals[2];
//		    				}
//		    				if (newBrightness > 1.0) {newBrightness = 1.0f;}
//		    				Color c = new Color( Color.HSBtoRGB( hsbvals[0], hsbvals[1], newBrightness));
//		    				r = (byte) c.getRed();
//		    				g = (byte) c.getGreen();
//		    				b = (byte) c.getBlue();
//		    			}
//		    		}
//                }
//                
//                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
//                img.setRGB(x,y,pix);
//                ind++;
//            }
//        } 
//        return img;
//    }
    
    public BufferedImage getFrameBytes(VideoLink[] links) {
        int BORDER_THICKNESS = 1;
        Color BORDER_COLOR = new Color(57,255,20);
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        int ind = 0;
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                byte r = data[ind + getColorOffset(RED)];
                byte g = data[ind + getColorOffset(GREEN)];
                byte b = data[ind + getColorOffset(BLUE)];
                
                for (int i = 0; i < links.length; i++) {
                    if (links[i] == null) {
                        break;
                    }
                    else {
                        if (links[i].getLinkLocation().contains(x,y)) {
                            if (x - links[i].getLinkLocation().x < BORDER_THICKNESS || x - links[i].getLinkLocation().x >= links[i].getLinkLocation().width -  BORDER_THICKNESS
                                    || y - links[i].getLinkLocation().y < BORDER_THICKNESS || y - links[i].getLinkLocation().y >= links[i].getLinkLocation().height -  BORDER_THICKNESS) {
                                r = (byte) BORDER_COLOR.getRed();
                                g = (byte) BORDER_COLOR.getGreen();
                                b = (byte) BORDER_COLOR.getBlue();
                            }
                            else {
                                float[] hsbvals = { 0, 0, 0 };
                                Color.RGBtoHSB(ByteToInt(r), ByteToInt(g), ByteToInt(b), hsbvals );
                                float newBrightness = BRIGHTNESS_FACTOR + hsbvals[2];
                                if (newBrightness > 1.0f) {
                                    newBrightness = 1.0f;
                                }
                                Color c = new Color( Color.HSBtoRGB( hsbvals[0], hsbvals[1], newBrightness));
                                r = (byte) c.getRed();
                                g = (byte) c.getGreen();
                                b = (byte) c.getBlue();
                            }
                        }
                    }
                }
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x,y,pix);
                ind++;
            }
        } 
        return img;
    }
    
    public BufferedImage getFrameBytes(ArrayList<Hyperlink> links, int frameNum, String targetLink) {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Color c;
        int ind = 0;
        float[] hsbvals = { 0, 0, 0 };
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 0; x < WIDTH; x++){
                byte r = data[ind + getColorOffset(RED)];
                byte g = data[ind + getColorOffset(GREEN)];
                byte b = data[ind + getColorOffset(BLUE)];
                
                
                for (int i = 0; i < links.size(); i++) {
                	if (links.get(i) == null) {
		    			break;
		    		}
		    		else {
		    			if ((frameNum >= links.get(i).getStartFrame()) && (frameNum <= links.get(i).getEndFrame())) {
		    				int startFrame = links.get(i).getStartFrame();
		                    int endFrame = links.get(i).getEndFrame();
		                    int dur = endFrame - startFrame;
		                    double dx = (links.get(i).getEndRect().getX() -  links.get(i).getStartRect().getX()) / dur;
		                    double dy = (links.get(i).getEndRect().getY() -  links.get(i).getStartRect().getY()) / dur;
		                    double dw = (links.get(i).getEndRect().getWidth() -  links.get(i).getStartRect().getWidth()) / dur;
		                    double dh = (links.get(i).getEndRect().getHeight() -  links.get(i).getStartRect().getHeight()) / dur;
		                    
		                    
		                    int newX = (int) Math.round(links.get(i).getStartRect().getX() + dx * (frameNum - links.get(i).getStartFrame()));
		                    int newY = (int) Math.round(links.get(i).getStartRect().getY() + dy * (frameNum - links.get(i).getStartFrame()));
		                    int newW = (int) Math.round(links.get(i).getStartRect().getWidth() + dw * (frameNum - links.get(i).getStartFrame()));
		                    int newH = (int) Math.round(links.get(i).getStartRect().getHeight() + dh * (frameNum - links.get(i).getStartFrame()));
		                    
		                    if (links.get(i).getName().equals(targetLink)) {
		                    	c = Color.red;
		                    }
		                    else {
		                    	c = Color.yellow;
		                    }
		                    
		                    if ((x == newX) && (y >= newY) && (y < (newY + newH))) {
		                    	
		                    	r = (byte) c.getRed();
			    				g = (byte) c.getGreen();
			    				b = (byte) c.getBlue();
		                    }
		                    else if ((x == (newX + newW -1)) && (y >= newY) && (y < (newY + newH))) {
		                    	
		                    	r = (byte) c.getRed();
			    				g = (byte) c.getGreen();
			    				b = (byte) c.getBlue();
		                    }
		                    else if ((y == newY) && (x >= newX) && (x < (newX + newW))) {
		                    	
		                    	r = (byte) c.getRed();
			    				g = (byte) c.getGreen();
			    				b = (byte) c.getBlue();
		                    }
		                    else if ((y == (newY + newH -1)) && (x >= newX) && (x < (newX + newW))) {
		                    	
		                    	r = (byte) c.getRed();
			    				g = (byte) c.getGreen();
			    				b = (byte) c.getBlue();
		                    }
		                    
		    			}
		    		}
                }
                
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
    
    static int ByteToInt(byte b){
        return (int)(b & 0x000000FF);
    }
}
