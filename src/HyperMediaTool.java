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

public class HyperMediaTool {

	private BufferedImage sourceImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private BufferedImage targetImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
	private JFrame frame;
	private JTextField sourceText;
	private JTextField targetText;
	private JTextField linkName;
	private Video sourceVideo;
	private Video targetVideo;
	private File[] directories;
	private ArrayList<String> inputFolders = new ArrayList<String>();
	private ArrayList<Hyperlink> links;
    private int currSourceFrameStartNum;
    private int curSourceFrameEndNum;
    private Rectangle currSourceRectStart;
    private Rectangle currSourceRectEnd;
    private String targetVideoPath;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		String sourceVideoFolder = args[0];
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HyperMediaTool window = new HyperMediaTool();
					window.initialize(sourceVideoFolder);
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
	    currSourceFrameStartNum = -1;
	    curSourceFrameEndNum = -1;
	    currSourceRectStart = new Rectangle();
	    currSourceRectEnd = new Rectangle();
	    targetVideoPath = null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String sourceVideoFolder) {
		
		try {
			sourceVideo = new Video(sourceVideoFolder);
		}catch(Exception e) {}
		
		directories = new File(System.getProperty("user.dir")).listFiles(File::isDirectory);
		
		inputFolders.add("Select a Video Folder");
		for (int i = 0; i < directories.length; i++) {
			inputFolders.add(directories[i].toString().substring(directories[i].toString().lastIndexOf("/") + 1));
		}
		
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.DARK_GRAY);
		frame.setBounds(200, 100, 950, 430);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		sourceImg = sourceVideo.getCurrentFrame().getFrameBytes();
		
		
		JLabel sourceVideoFrame = new JLabel(new ImageIcon(sourceImg));
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
		sourceSlider.setMaximum(9000);
		sourceSlider.setValue(0);
		sourceSlider.setPaintTicks(true);
		sourceSlider.setPaintLabels(true);
		sourceSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				sourceText.setText("Frame " + Integer.toString(sourceSlider.getValue()));
				try {
					sourceVideo.setCurrentFrame(sourceSlider.getValue() - 1);
					sourceImg = sourceVideo.getCurrentFrame().getFrameBytes();
					sourceVideoFrame.setIcon(new ImageIcon(sourceImg));
				}catch(Exception ex) {}
				sourceVideoFrame.repaint();
			}
		});
		sourceSlider.setBackground(Color.WHITE);
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
		targetSlider.setMaximum(9000);
		targetSlider.setValue(0);
		targetSlider.setPaintTicks(true);
		targetSlider.setPaintLabels(true);
		targetSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				targetText.setText("Frame " + Integer.toString(targetSlider.getValue()));
				try {
					targetVideo.setCurrentFrame(targetSlider.getValue() - 1);
					targetImg = targetVideo.getCurrentFrame().getFrameBytes();
					targetVideoFrame.setIcon(new ImageIcon(targetImg));
				}catch(Exception ex) {}
				targetVideoFrame.repaint();
			}
		});
		targetSlider.setBackground(Color.WHITE);
		targetSlider.setBounds(392, 328, 352, 40);
		frame.getContentPane().add(targetSlider);
		
		JComboBox selectTargetVideo = new JComboBox();
		selectTargetVideo.setBounds(756, 20, 176, 29);
		frame.getContentPane().add(selectTargetVideo);
		for (int i =0; i < inputFolders.size(); i++) {
			selectTargetVideo.addItem(inputFolders.get(i));
		}	
		selectTargetVideo.addItemListener(new ItemListener() {
	        @Override
	        public void itemStateChanged(ItemEvent e) {
	            if(e.getStateChange() == ItemEvent.SELECTED) {
	                String targetFolder = selectTargetVideo.getSelectedItem().toString();
	                try {
	                	if (!targetFolder.equals("Select a Video Folder")){
	                	targetVideo = new Video(targetFolder);
	                	targetVideo.setCurrentFrame(0);
	                	targetSlider.setValue(1);
						targetImg = targetVideo.getCurrentFrame().getFrameBytes();
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
			public void mouseClicked(MouseEvent e) {
			    try {
			        String name = linkName.getText();
			        Hyperlink newLink = new Hyperlink(name, currSourceFrameStartNum, curSourceFrameEndNum, currSourceRectStart, currSourceRectEnd, targetVideoPath, targetVideo.getCurrentFrameNum());
                    links.add(newLink);
                }catch(Exception ex) {}
			}
		});
		addLink.setBounds(756, 158, 176, 29);
		frame.getContentPane().add(addLink);
		
		JButton setStartBounds = new JButton("Set Start Bounds");
		setStartBounds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		setStartBounds.setBounds(756, 296, 176, 29);
		frame.getContentPane().add(setStartBounds);
		
		JButton setEndBounds = new JButton("Set End Bounds");
		setEndBounds.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		setEndBounds.setBounds(756, 365, 176, 29);
		frame.getContentPane().add(setEndBounds);
		
		JComboBox selectHyperLink = new JComboBox();
		selectHyperLink.setBounds(756, 89, 176, 27);
		frame.getContentPane().add(selectHyperLink);
		
		linkName = new JTextField();
		linkName.setBounds(760, 227, 168, 26);
		frame.getContentPane().add(linkName);
		linkName.setColumns(10);
	
	}
	
	private void saveHyp() throws IOException {
	    File dir = new File(sourceVideo.getPath());
	    String name = "0links.hyp";
	    File fullPath = new File(dir, name);
	    PrintWriter writer = new PrintWriter(fullPath, "UTF-8");
	    for (int i = 0; i < this.links.size(); i++) {
	        String line = this.links.get(i).getHypLine();
	        writer.println(line);
	    }
	    writer.close();
	}
	
}
