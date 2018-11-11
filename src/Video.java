import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

/**
 * Video Class
 * Stores all data necessary to play video with audio and hyperlinks
 */

public class Video {
    public static final int FRAME_BUFFER = 30;
    public static final int LINK_MAX = 50;
    private int currentFrame;
    private File[] rgbFiles;
    private Frame[] frameArray;
    private VideoLink[][] linkArray;
    private Boolean isHyper;
    //private AudioSegment[] audioArray; //TBD
    
    Video(String folderPath) throws IOException {
        this.currentFrame = 0;
        
        //setup rgbFiles to be sorted list of file paths
        File dir = new File(folderPath);
        this.rgbFiles = dir.listFiles((d, name) -> name.endsWith(".rgb"));
        Arrays.sort(rgbFiles);
        
        //setup frameArray and instantiate FRAME_BUFFER Frames
        frameArray = new Frame[rgbFiles.length];
        bufferFrames();
        
        //parse .hyp file to build linkArray
        File[] hypFiles = dir.listFiles((d, name) -> name.endsWith(".hyp"));
        if (hypFiles.length < 1) {
            isHyper = false;
        }
        else {
            isHyper = true;
            linkArray = new VideoLink[rgbFiles.length][LINK_MAX];
            FileInputStream fstream = new FileInputStream(hypFiles[0]);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println (line);
                String[] vals = line.split(" ");
                Rectangle startRect = new Rectangle(Integer.parseInt(vals[2]), Integer.parseInt(vals[3]), Integer.parseInt(vals[4]), Integer.parseInt(vals[5]));
                Rectangle endRect = new Rectangle(Integer.parseInt(vals[6]), Integer.parseInt(vals[7]), Integer.parseInt(vals[8]), Integer.parseInt(vals[9]));
                Video video = new Video(vals[10]); //recursive - be careful with test files
                VideoLink link = new VideoLink(video, Integer.parseInt(vals[11]), startRect); //this is dumb for now. Need to implement rectangle transformation
                for (int i = Integer.parseInt(vals[0]); i < Integer.parseInt(vals[1]); i++) {
                    int j = 0;
                    while(linkArray[i][j] != null) {
                        j++;
                    }
                    linkArray[i][j] = link;
                }   
            }
            br.close();
        }
        
        //Deal with audio - TBD
    }
    
    public int getCurrentFrameNum() {
        return currentFrame;
    }
    
    public Frame getCurrentFrame() {
        return frameArray[currentFrame];
    }
    
    public void setCurrentFrame(int index) throws IOException {
        this.currentFrame = index;
        bufferFrames();
    }
    
    public VideoLink[] getCurrentVideoLinks() {
        return linkArray[currentFrame];
    }
    
    public void getCurrentAudio() {
        //TBD
    }
    
    public boolean isHyper() {
        return isHyper;
    }
    
    private void bufferFrames() throws IOException {
        for (int i = currentFrame; i < FRAME_BUFFER; i++) {
            if (frameArray[i] == null) {
                frameArray[i] = new Frame(rgbFiles[i]);
            }
        }
    }   
}