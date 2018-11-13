import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class AVTuple{ 
    public Video video; 
    public Clip clip; 
    public AVTuple(Video video, String filePath) { 
        this.video = video; 
        try {
    		this.clip = AudioSystem.getClip();
    		this.clip.open(AudioSystem.getAudioInputStream(new File(filePath + "/" + filePath + ".wav")));
    	}catch(Exception e) {}
    }
    
    public Video getVideo() {
    	return this.video;
    }
    
    public Clip getClip() {
    	return this.clip;
    }
  
}