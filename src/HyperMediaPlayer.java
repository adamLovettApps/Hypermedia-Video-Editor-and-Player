import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.image.*;
import java.io.InputStream;
import javax.swing.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Canvas;
import java.awt.Button;
import java.util.ArrayDeque;
import java.util.Deque;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.lang.Math;

public class HyperMediaPlayer {
	
	BufferedImage img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private JFrame frame;
	JLabel videoFrame;
	File imgFile = null;
	InputStream imgStream;
	public Video startVideo;
	public Deque<Video> videoStack = new ArrayDeque<Video>();
	Video currentVideo;
	static int frameCounter = 1;
	static int timerDelay = 0;
	static long prevTime = 0;
	Clip clip;
	
	public static void main(String[] args) {
		String videoFolder = args[0];
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HyperMediaPlayer window = new HyperMediaPlayer();
					window.initialize(videoFolder);
					window.frame.setVisible(true);
				
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		});
		
	}

	
	public HyperMediaPlayer() {
		
		

	}


	public void initialize(String videoFolder) {
		try {
			startVideo = new Video(videoFolder);
			videoStack.push(startVideo);
		}catch(Exception e) {}
		
		
		
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(new File(videoFolder + "/" + videoFolder + ".wav")));
			//System.out.println(clip.getMicrosecondLength());
			}
			catch(Exception ex) {}
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(200, 100, 560, 350);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		videoFrame = new JLabel(new ImageIcon(img));
		ActionListener videoListener = new ActionListener(){
		    public void actionPerformed(ActionEvent e){
		    	if (videoStack.getFirst().getCurrentFrameNum() < videoStack.getFirst().getDuration()) {
		    		if ((videoStack.getFirst().getCurrentFrameNum() <= Math.floor(clip.getMicrosecondPosition()/((double)clip.getMicrosecondLength()/(double)videoStack.getFirst().getDuration())))) {
		    			//System.out.println(clip.getMicrosecondPosition());
		    			//System.out.println(frameCounter);
		    			img = videoStack.getFirst().getCurrentFrame().getFrameBytes();
		    			videoFrame.setIcon(new ImageIcon(img));
		    			videoFrame.repaint();  	
		    			try {
		    				videoStack.getFirst().setCurrentFrame(frameCounter);
		    			}catch(Exception ex) {}
		    			frameCounter++;
		    		}
		    	}
		    }
		};
		
		Timer videoTimer = new Timer(timerDelay, videoListener);
			
		videoFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x=e.getX();
			    int y=e.getY();
			    //System.out.println(x+","+y);
			    videoTimer.stop();
			    try {
			    	Video nextVideo = new Video("USCTwo");
		
			    	videoStack.push(nextVideo);
			    }catch(Exception ex) {}
			    videoTimer.start();
			    System.out.println(videoStack.size());
			    
			}
		});
		videoFrame.setBackground(Color.BLACK);
		videoFrame.setBounds(20, 20, 352, 288);
		frame.getContentPane().add(videoFrame);
		
		JButton playButton = new JButton("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clip.start();
				videoTimer.start();
			}
		});
		playButton.setBounds(423, 57, 117, 29);
		frame.getContentPane().add(playButton);
		
		JButton pauseButton = new JButton("Pause");
		pauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clip.stop();
				videoTimer.stop();
			}
		});
		pauseButton.setBounds(423, 137, 117, 29);
		frame.getContentPane().add(pauseButton);
		
		JButton stopButton = new JButton("Stop");
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					clip.stop();
					videoTimer.stop();
					clip.setMicrosecondPosition(0);
					videoStack.getFirst().setCurrentFrame(0);
					img = videoStack.getFirst().getCurrentFrame().getFrameBytes();
	  				videoFrame.setIcon(new ImageIcon(img));
					videoFrame.repaint();
					frameCounter = 0;
				}catch(Exception ex) {}
			}
		});
		stopButton.setBounds(423, 217, 117, 29);
		frame.getContentPane().add(stopButton);
	}
	
	public void loadVideo(String videoFolder) {
		try {
			Video video = new Video(videoFolder);
			videoStack.push(video);
		}catch (Exception e) {}
	}
		
	
}



