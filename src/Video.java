import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;

// Name: Adam Lovett & Brent Illingworth
// CSCI 576 Final Project
// Fall 2018

/**
 * Video Class
 * Stores all data necessary to play video with audio and hyperlinks
 * Assumes that the folder specified in folderPath only contains a single video with multiple
 * .rgb files with sequential naming convention, a single .wav file, and an optional .hyp file
 */

public class Video {
    public static final int LEADING_FRAME_BUFFER = 10;
    public static final int TRAILING_FRAME_BUFFER = 10;
    public static final int LINK_MAX = 50;
    public static final int HYP_MAX = 200;
    public static final int HYP_FIELDS = 13;
    private int currentFrame;
    private File[] rgbFiles;
    private Frame[] frameArray;
    private VideoLink[][] linkArray;
    private Boolean isHyper;
    private FileInputStream audioStream;
    private Clip clip;
    private String folderPath;
    public String[][] hypVals;
    
    Video(String folderPath) throws IOException {
        this.currentFrame = 0;
        this.folderPath = folderPath;
        
        //setup rgbFiles to be sorted list of file paths
        File dir = new File(folderPath);
        this.rgbFiles = dir.listFiles((d, name) -> name.endsWith(".rgb"));
        Arrays.sort(rgbFiles);
        
        //setup frameArray and instantiate LEADING_FRAME_BUFFER Frames
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
            hypVals = new String[HYP_MAX][HYP_FIELDS];
            FileInputStream fstream = new FileInputStream(hypFiles[0]);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            int counter = 0;
            while ((line = br.readLine()) != null) {
                //System.out.println (line);
                String[] vals = line.split(" ");
                hypVals[counter] = vals;
                counter++;
                Rectangle startRect = new Rectangle(Integer.parseInt(vals[2]), Integer.parseInt(vals[3]), Integer.parseInt(vals[4]), Integer.parseInt(vals[5]));
                Rectangle endRect = new Rectangle(Integer.parseInt(vals[6]), Integer.parseInt(vals[7]), Integer.parseInt(vals[8]), Integer.parseInt(vals[9]));
                
                //compute rectangle deltas
                int startFrame = Integer.parseInt(vals[0]);
                int endFrame = Integer.parseInt(vals[1]);
                int dur = endFrame - startFrame;
                double dx = (endRect.getX() -  startRect.getX()) / dur;
                double dy = (endRect.getY() -  startRect.getY()) / dur;
                double dw = (endRect.getWidth() -  startRect.getWidth()) / dur;
                double dh = (endRect.getHeight() -  startRect.getHeight()) / dur;

                //Video video = new Video(vals[10]); //recursive - be careful with test files
                for (int i = startFrame; i <= endFrame; i++) {
                    int j = 0;
                    while(linkArray[i][j] != null) {
                        j++;
                    }
                    int newX = (int) Math.round(startRect.getX() + dx * (i - startFrame));
                    int newY = (int) Math.round(startRect.getY() + dy * (i - startFrame));
                    int newW = (int) Math.round(startRect.getWidth() + dw * (i - startFrame));
                    int newH = (int) Math.round(startRect.getHeight() + dh * (i - startFrame));
                    Rectangle rect = new Rectangle(newX, newY, newW, newH);
                    //linkArray[i][j] = new VideoLink(video, Integer.parseInt(vals[11]), rect);
                    linkArray[i][j] = new VideoLink(Integer.parseInt(vals[11]), rect, vals[10]);
                }   
            }
            br.close();
        }
        
        //Create audioStream from wav file
        File[] wavFile = dir.listFiles((d, name) -> name.endsWith(".wav"));
        //this.audioStream = new FileInputStream(wavFile[0]);
        try {
    		this.clip = AudioSystem.getClip();
    		this.clip.open(AudioSystem.getAudioInputStream(wavFile[0]));    		
    		
    	}catch(Exception e) {}
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
    
    public FileInputStream getAudioStream() {
        return audioStream;
    }
    //returns the index offset into the audioStream to be used for frame sync
    //assumes 16 bits per sample at 44.1kHz
    public long getCurrentAudioOffset() {
        return currentFrame * 2 * 1470;
    }
    
    public Clip getClip() {
    	return this.clip;
    }
    
    public boolean isHyper() {
        return isHyper;
    }
    
    public int getDuration() {
        return rgbFiles.length;
    }
    
    public String getPath() {
        return folderPath;
    }
    
    private void bufferFrames() throws IOException {
        for (int i = currentFrame; i < (currentFrame + LEADING_FRAME_BUFFER); i++) {
            if (frameArray[i] == null) {
                frameArray[i] = new Frame(rgbFiles[i]);
            }
        }
        if (currentFrame >= TRAILING_FRAME_BUFFER) {
            frameArray[currentFrame - TRAILING_FRAME_BUFFER] = null;
        }
        System.gc();
        System.runFinalization();
    }   
}