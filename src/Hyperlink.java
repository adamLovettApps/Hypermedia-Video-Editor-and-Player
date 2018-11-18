// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

import java.awt.Rectangle;

/**
 * Hyperlink Class
 * Stores all data necessary to create .hyp files
 */

public class Hyperlink {
    private String name;
    private int startFrame;
    private int endFrame;
    private int targetFrame;
    private Rectangle startRect;
    private Rectangle endRect;
    private String videoPath;
    
    Hyperlink(String name, int startFrame, int endFrame, Rectangle startRect, Rectangle endRect, String videoPath, int targetFrame) {
        this.name = name;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
        this.startRect = startRect;
        this.endRect = endRect;
        this.videoPath = videoPath;
        this.targetFrame = targetFrame;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getStartFrame() {
        return this.startFrame;
    }
    
    public int getEndFrame() {
        return this.endFrame;
    }
    
    public Rectangle getStartRect() {
        return this.startRect;
    }
    
    public Rectangle getEndRect() {
        return this.endRect;
    }
      
    public void setStartFrame(int frameNumber) {
    	this.startFrame = frameNumber;
    }
    
    public void setEndFrame(int frameNumber) {
    	this.endFrame = frameNumber;
    }
    
    public void setStartRect(Rectangle rect) {
    	this.startRect = rect;
    }
    
    public void setEndRect(Rectangle rect) {
    	this.endRect = rect;
    }
    
    public String getHypLine() {
        String [] fields = new String[13];
        fields[0] = Integer.toString(this.startFrame);
        fields[1] = Integer.toString(this.endFrame);
        fields[2] = Integer.toString(startRect.x);
        fields[3] = Integer.toString(startRect.y);
        fields[4] = Integer.toString(startRect.width);
        fields[5] = Integer.toString(startRect.height);
        fields[6] = Integer.toString(endRect.x);
        fields[7] = Integer.toString(endRect.y);
        fields[8] = Integer.toString(endRect.width);
        fields[9] = Integer.toString(endRect.height);
        fields[10] = this.videoPath;
        fields[11] = Integer.toString(this.targetFrame);
        fields[12] = this.name;
        
        String line = fields[0];
        
        for (int i = 1; i < fields.length; i++) {
            line += " "  + fields[i];
        }
        
        return line;
    }
}
