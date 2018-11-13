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
	private File imgFile = null;
	private InputStream imgStream;
	private Video startVideo;	
	private static int frameCounter = 0;
	private static int timerDelay = 0;
	private Deque<AVTuple> avStack = new ArrayDeque<AVTuple>();
	
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
			avStack.push(new AVTuple(new Video(videoFolder), videoFolder));
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
		    	if (avStack.getFirst().getVideo().getCurrentFrameNum() < avStack.getFirst().getVideo().getDuration()) {
		    		if ((avStack.getFirst().getVideo().getCurrentFrameNum() <= Math.floor(avStack.getFirst().getClip().getMicrosecondPosition()/((double)avStack.getFirst().getClip().getMicrosecondLength()/(double)avStack.getFirst().getVideo().getDuration())))) {
		    			//System.out.println(avStack.getFirst().getClip().getMicrosecondPosition());
		    			//System.out.println(frameCounter);
		    			img = avStack.getFirst().getVideo().getCurrentFrame().getFrameBytes();
		    			videoFrame.setIcon(new ImageIcon(img));
		    			videoFrame.repaint();  	
		    			try {
		    				avStack.getFirst().getVideo().setCurrentFrame(frameCounter);
		    			}catch(Exception ex) {}
		    			frameCounter++;
		    		}
		    	}
		    	else {
		    		if (avStack.size() > 1) {
		    			avStack.pop();
		    			frameCounter = avStack.getFirst().getVideo().getCurrentFrameNum();
		    			avStack.getFirst().getClip().start();
		    		}
		    		else {
		    			avStack.getFirst().getClip().stop();
						avStack.getFirst().getClip().setMicrosecondPosition(0);
						try {
							avStack.getFirst().getVideo().setCurrentFrame(0);
						}catch(Exception excep) {}
						img = avStack.getFirst().getVideo().getCurrentFrame().getFrameBytes();
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
			    //System.out.println(x+","+y);
			    avStack.getFirst().getClip().stop();
			    videoTimer.stop();
			    
			    try {
			    	Video nextVideo = new Video("USCTwo");
		
			    	avStack.push(new AVTuple(nextVideo, "USCTwo"));
			    }catch(Exception ex) {}
			    frameCounter = 0;
			    avStack.getFirst().getClip().start();
			    videoTimer.start();			    
			}
		});
		videoFrame.setBackground(Color.BLACK);
		videoFrame.setBounds(20, 20, 352, 288);
		frame.getContentPane().add(videoFrame);
		
		JButton playButton = new JButton("Play");
		playButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				avStack.getFirst().getClip().start();
				videoTimer.start();
			}
		});
		playButton.setBounds(423, 57, 117, 29);
		frame.getContentPane().add(playButton);
		
		JButton pauseButton = new JButton("Pause");
		pauseButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				avStack.getFirst().getClip().stop();
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
					avStack.getFirst().getClip().stop();
					videoTimer.stop();
					avStack.getFirst().getClip().setMicrosecondPosition(0);
					avStack.getFirst().getVideo().setCurrentFrame(0);
					img = avStack.getFirst().getVideo().getCurrentFrame().getFrameBytes();
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