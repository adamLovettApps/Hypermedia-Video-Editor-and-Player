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
	private Deque<Video> videoStack = new ArrayDeque<Video>();
	private VideoLink[][] linkArray;
	
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
			videoStack.push(new Video(videoFolder));
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
		    	if (videoStack.getFirst().getCurrentFrameNum() < videoStack.getFirst().getDuration()) {
		    		if ((videoStack.getFirst().getCurrentFrameNum() <= Math.floor(videoStack.getFirst().getClip().getMicrosecondPosition()
		    				/((double)videoStack.getFirst().getClip().getMicrosecondLength()/(double)videoStack.getFirst().getDuration())))) {
		    			//System.out.println(avStack.getFirst().getClip().getMicrosecondPosition());
		    			//System.out.println(frameCounter);
		    			img = videoStack.getFirst().getCurrentFrame().getFrameBytes(videoStack.getFirst().getCurrentVideoLinks());
		    			videoFrame.setIcon(new ImageIcon(img));		    	
		    			videoFrame.repaint();  
		    			
		    			try {
		    				videoStack.getFirst().setCurrentFrame(frameCounter);
		    			}catch(Exception ex) {}
		    			frameCounter++;
		    		}
		    	}
		    	else {
		    		if (videoStack.size() > 1) {
		    			videoStack.pop();
		    			frameCounter = videoStack.getFirst().getCurrentFrameNum();
		    			videoStack.getFirst().getClip().start();
		    		}
		    		else {
		    			videoStack.getFirst().getClip().stop();
		    			videoStack.getFirst().getClip().setMicrosecondPosition(0);
						try {
							videoStack.getFirst().setCurrentFrame(0);
						}catch(Exception excep) {}
						img = videoStack.getFirst().getCurrentFrame().getFrameBytes();
		  				videoFrame.setIcon(new ImageIcon(img));
						videoFrame.repaint();
						frameCounter = 0;
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
			    
			    if (videoStack.getFirst().isHyper()) {
			    	
			    	VideoLink[] links = videoStack.getFirst().getCurrentVideoLinks();
			    	
			    	for (int i = 0; i <links.length ; i++) {
			    		if (links[i] == null) {
			    			break;
			    		}
			    		else {
			    			if (links[i].getLinkLocation().contains(x,y)) {
			    				videoStack.getFirst().getClip().stop();
						    	videoTimer.stop();
						    	try {		
						    		//videoStack.push(links[i].getVideo());
						    		videoStack.push(new Video(links[i].getPath()));
						    		videoStack.getFirst().setCurrentFrame(links[i].getFrameStart());
						    	}catch(Exception ex) {}
						    	
						    	frameCounter = links[i].getFrameStart();		
						    	videoStack.getFirst().getClip().setMicrosecondPosition((long)Math.floor((double)videoStack.getFirst().getClip().getMicrosecondLength()
						    			*(double)videoStack.getFirst().getCurrentFrameNum()/(double)videoStack.getFirst().getDuration()));
						    	
						    	videoStack.getFirst().getClip().start();
						    	videoTimer.start();
						    	//System.out.println(videoStack.size());
			    			}			    						    			
			    		}
			    	}
			    }
			}
		});
		
		videoFrame.setBackground(Color.BLACK);
		videoFrame.setBounds(20, 20, 352, 288);
		frame.getContentPane().add(videoFrame);
		
		JButton playButton = new JButton("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				videoStack.getFirst().getClip().start();
				videoTimer.start();
			}
		});
		playButton.setBounds(423, 57, 117, 29);
		frame.getContentPane().add(playButton);
		
		JButton pauseButton = new JButton("Pause");
		pauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				videoStack.getFirst().getClip().stop();
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
					videoStack.getFirst().getClip().stop();
					videoTimer.stop();
					videoStack.getFirst().getClip().setMicrosecondPosition(0);
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
}