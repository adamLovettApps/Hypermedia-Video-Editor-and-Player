// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

import java.awt.Rectangle;

/**
 * VideoLink Class
 * Stores all data necessary to provide video hyperlink
 */

public class VideoLink {
    private Video video;
    private int frameStart;
    private Rectangle linkLocation;
    
    VideoLink(Video video, int frameStart, Rectangle linkLocation) {
        this.video = video;
        this.frameStart = frameStart;
        this.linkLocation = linkLocation;
    }
    
    public Video getVideo() {
        return this.video;
    }
    
    public int getFrameStart() {
        return this.frameStart;
    }
    
    public Rectangle getLinkLocation() {
        return this.linkLocation;
    }
}
