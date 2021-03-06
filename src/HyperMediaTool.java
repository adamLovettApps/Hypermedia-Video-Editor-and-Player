import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.image.*;
import java.io.*;
import java.lang.String;
import java.util.ArrayList;

public class HyperMediaTool extends JFrame{

	private BufferedImage sourceImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private BufferedImage targetImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private JFrame frame;
	private JTextField sourceText;
	private JTextField targetText;
	private JTextField jumpSourceText;
	private JTextField jumpTargetText;
	private JTextField linkName;
	private Video sourceVideo;
	private Video targetVideo;
	private File[] directories;
	private ArrayList<String> sourceInputFolders = new ArrayList<String>();
	private ArrayList<String> targetInputFolders = new ArrayList<String>();
	private ArrayList<Hyperlink> links;
	private Hyperlink currentHyperlink;
    private int currSourceFrameStartNum;
    private int curSourceFrameEndNum;
    private Rectangle currSourceRectStart;
    private Rectangle currSourceRectEnd;
    private Rectangle startRect;
    private Rectangle endRect;
    private Hyperlink newLink;
    private JComboBox<String> selectHyperLink = new JComboBox<String>();
    private static String sourceVideoFolder;
  
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HyperMediaTool window = new HyperMediaTool();
					window.initialize();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HyperMediaTool() {
	    links = new ArrayList<Hyperlink>();
	    currSourceFrameStartNum = 0;
	    curSourceFrameEndNum = 0;
	    currSourceRectStart = new Rectangle(0, 0, 0, 0);
	    currSourceRectEnd = new Rectangle(0, 0, 0, 0);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		
		directories = new File(System.getProperty("user.dir")).listFiles(File::isDirectory);
		
		targetInputFolders.add("Select a Target Video");
		for (int i = 0; i < directories.length; i++) {
			targetInputFolders.add(directories[i].toString().substring(directories[i].toString().lastIndexOf("/") + 1));
		}
		
		sourceInputFolders.add("Select a Source Video");
		for (int i = 0; i < directories.length; i++) {
			sourceInputFolders.add(directories[i].toString().substring(directories[i].toString().lastIndexOf("/") + 1));
		}
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(200, 100, 1156, 465);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);	
		
		SourceVideoFrame sourceVideoFrame = new SourceVideoFrame(new ImageIcon(sourceImg));
		sourceVideoFrame.setBackground(Color.BLACK);
		sourceVideoFrame.setBounds(20, 20, 352, 288);
		frame.getContentPane().add(sourceVideoFrame);
		
		
		
		JLabel targetVideoFrame = new JLabel(new ImageIcon(targetImg));
		targetVideoFrame.setForeground(Color.BLACK);
		targetVideoFrame.setBackground(Color.BLACK);
		targetVideoFrame.setBounds(392, 20, 352, 288);
		frame.getContentPane().add(targetVideoFrame);
				
		sourceText = new JTextField();
		sourceText.setForeground(Color.WHITE);
		sourceText.setBackground(Color.DARK_GRAY);
		sourceText.setBounds(156, 364, 130, 26);
		sourceText.setText("Frame 1");
		sourceText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		frame.getContentPane().add(sourceText);
		sourceText.setColumns(10);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		final JSlider sourceSlider = new JSlider();
		sourceSlider.setMinimum(1);
		sourceSlider.setMaximum(1);
		sourceSlider.setValue(1);
		sourceSlider.setPaintTicks(true);
		sourceSlider.setPaintLabels(true);
		sourceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sourceText.setText("Frame " + Integer.toString(sourceSlider.getValue()));
				try {
					//sourceVideo.setCurrentFrame(sourceSlider.getValue() - 1);					
					sourceImg = sourceVideo.getCurrentFrameTool(sourceSlider.getValue() - 1).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
					sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
				}catch(Exception ex) {}
				sourceVideoFrame.repaint();
			}
		});
		sourceSlider.setBackground(Color.DARK_GRAY);
		sourceSlider.setBounds(20, 328, 352, 40);
		frame.getContentPane().add(sourceSlider);
		
		targetText = new JTextField();
		targetText.setForeground(Color.WHITE);
		targetText.setBackground(Color.DARK_GRAY);
		targetText.setBounds(528, 364, 130, 26);
		targetText.setText("Frame 1");
		targetText.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		frame.getContentPane().add(targetText);
		targetText.setColumns(10);
		
		
		final JSlider targetSlider = new JSlider();
		targetSlider.setMinimum(1);
		if(targetVideo != null) {
			targetSlider.setMaximum(targetVideo.getDuration());
		}
		else {
			targetSlider.setMaximum(1);
		}
		targetSlider.setValue(1);
		targetSlider.setPaintTicks(true);
		targetSlider.setPaintLabels(true);
		targetSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				targetText.setText("Frame " + Integer.toString(targetSlider.getValue()));
				try {
					//targetVideo.setCurrentFrame(targetSlider.getValue() - 1);
					targetImg = targetVideo.getCurrentFrameTool(targetSlider.getValue() - 1).getFrameBytes();
					targetVideoFrame.setIcon(new ImageIcon(targetImg));
					targetSlider.setMaximum(targetVideo.getDuration());
				}catch(Exception ex) {}
				targetVideoFrame.repaint();
			}
		});
		targetSlider.setBackground(Color.DARK_GRAY);
		targetSlider.setBounds(392, 328, 352, 40);
		frame.getContentPane().add(targetSlider);
		
		
		
		JComboBox<String> selectSourceVideo = new JComboBox<String>();
		selectSourceVideo.setBounds(962, 20, 186, 29);
		frame.getContentPane().add(selectSourceVideo);
		for (int i =0; i < targetInputFolders.size(); i++) {
			selectSourceVideo.addItem((String)sourceInputFolders.get(i));
		}	
		selectSourceVideo.addItemListener(new ItemListener() {
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	            if(e.getStateChange() == ItemEvent.SELECTED) {
	                String sourceFolder = selectSourceVideo.getSelectedItem().toString();
	                try {
	                	if (!sourceFolder.equals("Select a Source Video")){
	                		System.gc();
	                		System.runFinalization(); 
	                		sourceVideo = null;
	                		sourceVideo = new Video(sourceFolder);
	                		for (int i = links.size() - 1; i >= 0 ; i--) {
	                			links.remove(i);
	                		}
	                		selectHyperLink.removeAllItems();
	                		selectHyperLink.addItem("Select a Link");
	                		if (sourceVideo.isHyper()) {
	                			for (int i = 0; i < sourceVideo.getNumLinks(); i++) {
	                				startRect = new Rectangle(Integer.parseInt(sourceVideo.hypVals[i][2]), Integer.parseInt(sourceVideo.hypVals[i][3]), Integer.parseInt(sourceVideo.hypVals[i][4]), Integer.parseInt(sourceVideo.hypVals[i][5]));
	                				endRect = new Rectangle(Integer.parseInt(sourceVideo.hypVals[i][6]), Integer.parseInt(sourceVideo.hypVals[i][7]), Integer.parseInt(sourceVideo.hypVals[i][8]), Integer.parseInt(sourceVideo.hypVals[i][9]));
	                				newLink = new Hyperlink(sourceVideo.hypVals[i][12], Integer.parseInt(sourceVideo.hypVals[i][0]), Integer.parseInt(sourceVideo.hypVals[i][1]), startRect, endRect, sourceVideo.hypVals[i][10], Integer.parseInt(sourceVideo.hypVals[i][11]));
	                				links.add(newLink);
	                			}
	                		}
	                		for (int i = 0; i < links.size(); i++) {
	                			selectHyperLink.addItem(links.get(i).getName());
	                		}
	                		//targetVideo.setCurrentFrame(0);
	                		sourceSlider.setValue(1);
	                		sourceSlider.setMaximum(sourceVideo.getDuration());
	                		sourceImg = sourceVideo.getCurrentFrameTool(0).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
	                		sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
	                		}
	                	else {
	                		sourceSlider.setValue(1);
	                		sourceSlider.setMaximum(1);
	                		for (int i = links.size() - 1; i >= 0 ; i--) {
	                			links.remove(i);
	                		}
	                		selectHyperLink.removeAllItems();
	                		selectHyperLink.addItem("Select a Link");
	                		sourceImg = getBlackFrame();
	                		sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
	                	}
	                }catch(Exception ex){}
	                sourceVideoFrame.repaint();
	            }
	        }
	    });
		
		
		
		JComboBox<String> selectTargetVideo = new JComboBox<String>();
		selectTargetVideo.setBounds(962, 89, 186, 29);
		frame.getContentPane().add(selectTargetVideo);
		for (int i =0; i < targetInputFolders.size(); i++) {
			selectTargetVideo.addItem((String)targetInputFolders.get(i));
		}	
		selectTargetVideo.addItemListener(new ItemListener() {
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	            if(e.getStateChange() == ItemEvent.SELECTED) {
	                String targetFolder = selectTargetVideo.getSelectedItem().toString();
	                try {
	                	if (!targetFolder.equals("Select a Target Video")){
	                		System.gc();
	                		System.runFinalization(); 
	                		targetVideo = null;
	                		targetVideo = new Video(targetFolder);
	                		//targetVideo.setCurrentFrame(0);
	                		targetSlider.setValue(1);
	                		targetSlider.setMaximum(targetVideo.getDuration());
	                		targetImg = targetVideo.getCurrentFrameTool(0).getFrameBytes();
	                		targetVideoFrame.setIcon(new ImageIcon(targetImg));
	                	}
	                	else {
	                		targetSlider.setValue(1);
	                		targetSlider.setMaximum(1);
	                		for (int i = links.size() - 1; i >= 0 ; i--) {
	                			links.remove(i);
	                		}
	                		selectHyperLink.removeAllItems();
	                		selectHyperLink.addItem("Select a Link");
	                		targetImg = getBlackFrame();
	                		targetVideoFrame.setIcon(new ImageIcon(targetImg));
	                	}
	                }catch(Exception ex){}
	                targetVideoFrame.repaint();
	            }
	        }
	    });

		
		JButton addLink = new JButton("Add New HyperLink");
		addLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
			    try {
			    	boolean match = false;
			        String name = linkName.getText();
			        if (!name.equals("")){
			        	for (int i = 0; i< links.size(); i++) {
			        		if (links.get(i).getName().equals(name)){
			        			match = true;
			        			linkName.setText("");
			        		}
			        	}
			        	if(match == false) {
			        		Hyperlink newLink = new Hyperlink(name, sourceVideo.getCurrentFrameNum(), sourceVideo.getCurrentFrameNum(), currSourceRectStart, currSourceRectEnd, selectTargetVideo.getSelectedItem().toString(), targetVideo.getCurrentFrameNum());
			        		links.add(newLink);
			        		selectHyperLink.addItem(newLink.getName());
			        		selectHyperLink.setSelectedItem(newLink.getName());
			        		linkName.setText("");
			        	}
			        	sourceImg = sourceVideo.getCurrentFrameTool(sourceVideo.getCurrentFrameNum()).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
						sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
						sourceVideoFrame.repaint();
			        	
			        }
                }catch(Exception ex) {}
			}
		});
		addLink.setBounds(962, 227, 186, 29);
		frame.getContentPane().add(addLink);
		
		JButton setStartBounds = new JButton("Set Start Bounds");
		setStartBounds.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(sourceVideoFrame.getCurrentRect() != null) {
					if(currentHyperlink != null) {
						currentHyperlink.setStartFrame(sourceVideo.getCurrentFrameNum());
						currentHyperlink.setStartRect(sourceVideoFrame.getCurrentRect());
						sourceVideoFrame.setCurrentRectColor();
						if(currentHyperlink.getEndFrame() <= currentHyperlink.getStartFrame()) {
							currentHyperlink.setEndFrame(sourceVideo.getDuration() - 1);
							currentHyperlink.setEndRect(sourceVideoFrame.getCurrentRect());
						}
						
						sourceImg = sourceVideo.getCurrentFrameTool(sourceVideo.getCurrentFrameNum()).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
						sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
						sourceVideoFrame.setCurrentRectNull();
						sourceVideoFrame.repaint();
					
					}
				}
			}
		});
		setStartBounds.setBounds(756, 20, 186, 29);
		frame.getContentPane().add(setStartBounds);
		
		JButton setEndBounds = new JButton("Set End Bounds");
		setEndBounds.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(sourceVideoFrame.getCurrentRect() != null) {
					if(sourceVideoFrame.getCurrentRect() != null) {
						if(currentHyperlink != null) {
							currentHyperlink.setEndFrame(sourceVideo.getCurrentFrameNum());
							currentHyperlink.setEndRect(sourceVideoFrame.getCurrentRect());
							if(currentHyperlink.getEndFrame() <= currentHyperlink.getStartFrame()) {
								currentHyperlink.setStartFrame(0);
								currentHyperlink.setStartRect(sourceVideoFrame.getCurrentRect());
							}
							sourceImg = sourceVideo.getCurrentFrameTool(sourceVideo.getCurrentFrameNum()).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
							sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
							sourceVideoFrame.repaint();
							sourceVideoFrame.setCurrentRectNull();
						}
					}
				}
			}
		});
		setEndBounds.setBounds(756, 89, 186, 29);
		frame.getContentPane().add(setEndBounds);
		
		JButton setTargetFrame = new JButton("Set Target Frame");
		setTargetFrame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(currentHyperlink != null) {
					currentHyperlink.setTargetFrame(targetVideo.getCurrentFrameNum());
					currentHyperlink.setVideoPath(targetVideo.getPath());
				}
				
			}
		});
		setTargetFrame.setBounds(756, 158, 186, 29);
		frame.getContentPane().add(setTargetFrame);
		
		JButton deleteCurrentLink = new JButton("Delete Current Link");
		deleteCurrentLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				for (int i=0; i < links.size(); i++) {
	            	   if (links.get(i).getName().equals(selectHyperLink.getSelectedItem().toString())) {
	            		   links.remove(i);
	            	   }
				}
				selectHyperLink.removeAllItems();
            	selectHyperLink.addItem("Select a Link");	
            	for (int i = 0; i < links.size(); i++) {
        		    selectHyperLink.addItem(links.get(i).getName());
        		}
            	sourceImg = sourceVideo.getCurrentFrameTool(sourceVideo.getCurrentFrameNum()).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
				sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
				sourceVideoFrame.repaint();
			}
		});
		deleteCurrentLink.setBounds(756, 227, 186, 29);
		frame.getContentPane().add(deleteCurrentLink);
		
		JButton deleteAllLinks = new JButton("Delete All Links");
		deleteAllLinks.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				//System.out.println(links.size());
				for (int i = links.size() -1 ; i >= 0 ; i--) {
            		links.remove(i);
            	}
            	selectHyperLink.removeAllItems();
            	selectHyperLink.addItem("Select a Link");	
            	sourceImg = sourceVideo.getCurrentFrameTool(sourceVideo.getCurrentFrameNum()).getFrameBytes(links, sourceVideo.getCurrentFrameNum(), selectHyperLink.getSelectedItem().toString());
				sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
				sourceVideoFrame.repaint();
			}
		});
		deleteAllLinks.setBounds(756, 296, 186, 29);
		frame.getContentPane().add(deleteAllLinks);
		
		JButton saveCurrentVideo = new JButton("Save Source Video Links");
		saveCurrentVideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (sourceVideo != null) {
					try {
						saveHyp();
					}catch(Exception ex) {}
				}
			}
		});
		saveCurrentVideo.setBounds(855, 365, 186, 29);
		frame.getContentPane().add(saveCurrentVideo);
		
		
		selectHyperLink.addItem("Select a Link");
		for (int i = 0; i < links.size(); i++) {
		    selectHyperLink.addItem(links.get(i).getName());
		}
		selectHyperLink.setBounds(962, 158, 186, 27);
		frame.getContentPane().add(selectHyperLink);
		selectHyperLink.addItemListener(new ItemListener() {
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	            if(e.getStateChange() == ItemEvent.SELECTED) {
	               for (int i=0; i < links.size(); i++) {
	            	   if (links.get(i).getName().equals(selectHyperLink.getSelectedItem().toString())) {
	            		   	currentHyperlink = links.get(i);
	            		   	sourceImg = sourceVideo.getCurrentFrameTool(currentHyperlink.getStartFrame()).getFrameBytes(links, currentHyperlink.getStartFrame(), selectHyperLink.getSelectedItem().toString());
							sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
							sourceVideoFrame.repaint();
							sourceSlider.setMaximum(sourceVideo.getDuration());
							sourceSlider.setValue(currentHyperlink.getStartFrame() + 1);
							selectTargetVideo.setSelectedItem(currentHyperlink.getVideoPath());
							targetImg = targetVideo.getCurrentFrameTool(currentHyperlink.getTargetFrame()).getFrameBytes();
							targetVideoFrame.setIcon(new ImageIcon(targetImg));
							targetVideoFrame.repaint();
							targetSlider.setMaximum(targetVideo.getDuration());
							targetSlider.setValue(currentHyperlink.getTargetFrame() + 1);
	            	   }
	               }
	            }
	        }
	    });
		
		linkName = new JTextField();
		linkName.setBounds(965, 296, 178, 26);
		frame.getContentPane().add(linkName);
		linkName.setColumns(10);
		
		jumpSourceText = new JTextField();
		jumpSourceText.setBounds(209, 402, 45, 26);
		frame.getContentPane().add(jumpSourceText);
		jumpSourceText.setColumns(10);
		
		JButton jumpSourceButton = new JButton("Go to Frame");
		jumpSourceButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					int jumpSourceTextInt = Integer.parseInt(jumpSourceText.getText());
					if ((jumpSourceTextInt >= 1) && (jumpSourceTextInt <= sourceSlider.getMaximum())){
						sourceSlider.setValue(jumpSourceTextInt);
					}
					
				}catch(Exception ex) {}
				jumpSourceText.setText("");
			}
		});
		jumpSourceButton.setBounds(95, 402, 117, 29);
		frame.getContentPane().add(jumpSourceButton);
		
		jumpTargetText = new JTextField();
		jumpTargetText.setBounds(580, 402, 45, 26);
		frame.getContentPane().add(jumpTargetText);
		jumpTargetText.setColumns(10);
		
		JButton jumpTargetButton = new JButton("Go to Frame");
		jumpTargetButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				try {
					int jumpTargetTextInt = Integer.parseInt(jumpTargetText.getText());
					if ((jumpTargetTextInt >= 1) && (jumpTargetTextInt <= targetSlider.getMaximum())){
						targetSlider.setValue(jumpTargetTextInt);
					}
					
				}catch(Exception ex) {}
				jumpTargetText.setText("");
			}
		});
		jumpTargetButton.setBounds(467, 402, 117, 29);
		frame.getContentPane().add(jumpTargetButton);
		
		
		
		
		
	
	}
	
	private void saveHyp() throws IOException {
	    File dir = new File(sourceVideo.getPath());
	    String name = sourceVideo.getPath() + ".hyp";
	    File fullPath = new File(dir, name);
	    PrintWriter writer = new PrintWriter(fullPath, "UTF-8");
	    for (int i = 0; i < this.links.size(); i++) {
	        String line = this.links.get(i).getHypLine();
	        writer.println(line);
	    }
	    writer.close();
	}
	
	public BufferedImage getBlackFrame() {
        BufferedImage img = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        int ind = 0; 
        for(int y = 0; y < 288; y++){
            for(int x = 0; x < 352; x++){
                byte r = (byte)0;
                byte g = (byte)0;
                byte b = (byte)0;
                int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                img.setRGB(x,y,pix);
                ind++;
            }
        } 
        return img;
    }
	
	
}
