import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.ImageIcon;

public class SourceVideoFrame extends JLabel {
	
	private Integer startingX = null;
	private Integer startingY = null;
	private Integer currentX = null;
	private Integer currentY = null;
	private ImageIcon imgIcon;
	private Rectangle currentRect;
	private Color rectColor = Color.green;
	
	public SourceVideoFrame(String image) {
	    this(new ImageIcon(image));
	  }
	
	public SourceVideoFrame(ImageIcon icon) {
		imgIcon = icon;
	    setIcon(icon);
	    DrawRectangle drawRectangle = new DrawRectangle();
	    addMouseListener(drawRectangle);
	    addMouseMotionListener(drawRectangle);
	  }
	
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (startingX != null && startingY != null && currentX != null && currentY != null) {
    	  int x;
          int y;
          int rectWidth;
          int rectHeight;        
          if (currentX < startingX) {
        	  x = currentX;
          }
          else {
        	  x = startingX;
          }
          if (currentY < startingY) {
        	  y = currentY;
          }
          else {
        	  y = startingY;
          }         
          if ((startingX - currentX) >= 0) {
        	  rectWidth = startingX - currentX;
          }
          else {
        	  rectWidth = currentX - startingX;
          }
          
          if ((startingY - currentY) >= 0) {
        	  rectHeight = startingY - currentY;
          }
          else {
        	  rectHeight = currentY - startingY;
          }
          currentRect = new Rectangle(x, y, rectWidth, rectHeight);
          g.setColor(Color.green); 
          g.drawRect(x, y, rectWidth, rectHeight);
       }   
       if(currentRect != null) {
    	   g.setColor(rectColor);
    	  // g.drawRect((int)currentRect.getX(), (int)currentRect.getY(), (int)currentRect.getWidth(), (int)currentRect.getHeight());
       }
       
   }
    
    public Rectangle getCurrentRect() {
    	return this.currentRect;
    }
    
    public void setCurrentRectNull() {
    	startingX = null;
    	startingY = null;
    	currentX = null;
    	currentY = null;	
    	rectColor = Color.green;
    	currentRect = new Rectangle(0, 0, 0, 0);
    	currentRect = null;
    	SourceVideoFrame.this.repaint();
    }
    
    public void setCurrentRectColor() {
    	rectColor = Color.red;
    	//paintComponent(this.getGraphics());
    }
    
    private class DrawRectangle extends MouseAdapter {
    	@Override
        public void mousePressed(MouseEvent e) {
            startingX = e.getX();
            startingY = e.getY();
            rectColor = Color.green;
        }
    	
        @Override
        public void mouseDragged(MouseEvent e) {
        	currentX = e.getX();
        	currentY = e.getY();
        	rectColor = Color.green;
        	SourceVideoFrame.this.repaint();
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
        	startingX = null;
        	startingY = null;
        	currentX = null;
        	currentY = null;	
        	rectColor = Color.green;
        	currentRect = new Rectangle(0, 0, 0, 0);
        	currentRect = null;
        	SourceVideoFrame.this.repaint();
        }
     }    
}