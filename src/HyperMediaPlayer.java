import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.io.*;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.image.*;
import java.io.InputStream;
import javax.swing.Timer;
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
	
	private BufferedImage img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private JFrame frame;
	private JLabel videoFrame;
	private static int frameCounter = 0;
	private static int timerDelay = 0;
	private Deque<VideoClipPair> videoClipStack = new ArrayDeque<VideoClipPair>();
	private VideoLink[][] linkArray;
	private Video currentVideo;
	private JButton pauseButton;
	private JButton playButton;
	
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
			currentVideo = new Video(videoFolder);
		}catch(Exception e) {}
		
				
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(200, 100, 560, 350);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		videoFrame = new JLabel(new ImageIcon(img));
		
		ActionListener videoListener = new ActionListener(){
		    public void actionPerformed(ActionEvent e){		    	
		    	if (currentVideo.getCurrentFrameNum() < currentVideo.getDuration()) {
		    		if ((currentVideo.getCurrentFrameNum() <= Math.floor(currentVideo.getClip().getMicrosecondPosition()
		    				/((double)currentVideo.getClip().getMicrosecondLength()/(double)currentVideo.getDuration())))) {
		    			//System.out.println(avStack.getFirst().getClip().getMicrosecondPosition());
		    			//System.out.println(frameCounter);
		    			if (currentVideo.isHyper()) {
		    				img = currentVideo.getCurrentFrame().getFrameBytes(currentVideo.getCurrentVideoLinks());
		    			}
		    			else {
		    				img = currentVideo.getCurrentFrame().getFrameBytes();
		    			}
		    			videoFrame.setIcon(new ImageIcon(img));		    	
		    			videoFrame.repaint();  
		    			
		    			try {
		    				/*currentVideo.setCurrentFrame((int)Math.floor(currentVideo.getClip().getMicrosecondPosition()
				    				/((double)currentVideo.getClip().getMicrosecondLength()/(double)currentVideo.getDuration())));*/
		    				if((currentVideo.getCurrentFrameNum() <= Math.floor(currentVideo.getClip().getMicrosecondPosition()
				    				/((double)currentVideo.getClip().getMicrosecondLength()/(double)currentVideo.getDuration())) + 1)) {
		    					currentVideo.setCurrentFrame((int)Math.floor(currentVideo.getClip().getMicrosecondPosition()
					    				/((double)currentVideo.getClip().getMicrosecondLength()/(double)currentVideo.getDuration())));
		    				}	
		    				else {
		    					currentVideo.setCurrentFrame(frameCounter);
		    				}
		    			}catch(Exception ex) {}
		    			frameCounter++;
		    		}
		    	}
		    	else {
		    		if (videoClipStack.size() >= 1) {		    			
		    			try {
		    				currentVideo = null;
		    				System.gc();
		                	System.runFinalization();
		    				currentVideo = new Video(videoClipStack.getFirst().getVideoName());
		    				//System.out.println(currentVideo.getPath());
		    				currentVideo.getClip().setMicrosecondPosition(videoClipStack.getFirst().getClipPosition());
		    				currentVideo.setCurrentFrame((int)Math.round(currentVideo.getClip().getMicrosecondPosition()
				    				/((double)currentVideo.getClip().getMicrosecondLength()/(double)currentVideo.getDuration())));
		    			}catch(Exception ex) {}
		    			frameCounter = currentVideo.getCurrentFrameNum();
		    			currentVideo.getClip().start();
		    			videoClipStack.pop();
		    		}
		    		else {
		    			currentVideo.getClip().stop();
		    			frameCounter = 0;
		    			currentVideo.getClip().setMicrosecondPosition(0);
						try {
							currentVideo.setCurrentFrame(0);
						}catch(Exception excep) {}
						img = currentVideo.getCurrentFrame().getFrameBytes();
		  				videoFrame.setIcon(new ImageIcon(img));
						videoFrame.repaint();
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
			    
			    if (currentVideo.isHyper()) {
			    	
			    	VideoLink[] links = currentVideo.getCurrentVideoLinks();
			    	
			    	for (int i = 0; i <links.length ; i++) {
			    		if (links[i] == null) {
			    			break;
			    		}
			    		else {
			    			if (links[i].getLinkLocation().contains(x,y)) {
			    				currentVideo.getClip().stop();
						    	videoTimer.stop();
						    	videoClipStack.push(new VideoClipPair(currentVideo.getPath(), (int)currentVideo.getClip().getMicrosecondPosition()));
						    	try {
						    		currentVideo = null;
						    		System.gc();
				                	System.runFinalization();
						    		currentVideo = new Video(links[i].getPath());
						    		//videoClipStack.push(new Video(links[i].getPath()));
						    		currentVideo.setCurrentFrame(links[i].getFrameStart());
						    		//videoStack.getFirst().setCurrentFrame(links[i].getFrameStart());
						    	}catch(Exception ex) {}
						    	
						    	frameCounter = links[i].getFrameStart();		
						    	currentVideo.getClip().setMicrosecondPosition((long)Math.floor((double)currentVideo.getClip().getMicrosecondLength()
						    			*(double)currentVideo.getCurrentFrameNum()/(double)currentVideo.getDuration()));					    
						    	currentVideo.getClip().start();
						    	videoTimer.start();
						    	
			    			}			    						    			
			    		}
			    	}
			    }
			}
		});
		
		videoFrame.setBackground(Color.BLACK);
		videoFrame.setBounds(20, 20, 352, 288);
		frame.getContentPane().add(videoFrame);
		
		playButton = new JButton("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				currentVideo.getClip().start();
				videoTimer.start();
			}
		});
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentVideo.getClip().start();
				videoTimer.start();
			}
		});
		playButton.setBounds(423, 57, 117, 29);
		frame.getContentPane().add(playButton);
		
		pauseButton = new JButton("Pause");
		pauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				currentVideo.getClip().stop();
				videoTimer.stop();
			}
		});
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentVideo.getClip().stop();
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
					currentVideo.getClip().stop();
					videoTimer.stop();
					currentVideo.getClip().setMicrosecondPosition(0);
					currentVideo.setCurrentFrame(0);
					img = currentVideo.getCurrentFrame().getFrameBytes();
	  				videoFrame.setIcon(new ImageIcon(img));
					videoFrame.repaint();
					frameCounter = 0;
				}catch(Exception ex) {}
			}
		});
		stopButton.setBounds(423, 217, 117, 29);
		frame.getContentPane().add(stopButton);
	}
	
	private class VideoClipPair{
		private String videoName;
		private int clipPosition;
		
		public VideoClipPair(String videoNameIn, int clipPositionIn) {
			this.videoName = videoNameIn;
			this.clipPosition = clipPositionIn;
		}
		
		public String getVideoName(){
			return this.videoName;
		}
		
		public int getClipPosition(){
			return this.clipPosition;
		}
	}
}