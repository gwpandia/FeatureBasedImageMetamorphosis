package ui;

import interfaces.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;
import data.*;

public class ImagePanel extends JPanel implements AdjustableLineStateListener, ImageStateListener, MouseListener, MouseMotionListener{
	static protected Graphics2D g2;
	private ArrayList<AdjustableLine> lineset;
	private BufferedImage image;
	private BufferedImage scaledimage;
	private Line2D.Double drawline;
	private SimpleModel sm;
	private AdjustableLine curline;
	private boolean circle1Click;
	private boolean circle2Click;
	private boolean lineClick;
	private Point2D.Double curclick;
	private Line2D.Double curclickline;
	
	public ImagePanel(SimpleModel sm, int i){
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(5000, 5000));
		this.sm = sm;
		if(i == 1){
			this.lineset = this.sm.getLineSet1();
			this.image = this.sm.getImage1();
		}
		else if(i == 2){
			this.lineset = this.sm.getLineSet2();
			this.image = this.sm.getImage2();
		}
		circle1Click = false;
		circle2Click = false;
		lineClick = false;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		//g2.setPaint(Color.WHITE);
		//g2.fillRect(0, 0, Constant.PANELWIDTH, Constant.PANELWIDTH);
		g2.setBackground(Color.WHITE);
		if(image != null){
			//AffineTransform at = new AffineTransform();
			//at.scale(0.5, 0.5);
			//AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			//atop.filter(image, scaledimage);
			//g2.drawImage(atop.filter(image, null), 0, 0, null);
			g2.drawImage(image, 0, 0, null);
		}
		g2.setPaint(Color.BLUE);
		Stroke curstroke = g2.getStroke();
		g2.setStroke(new BasicStroke(3));
		if(lineset != null){
			for(Iterator<AdjustableLine> it = lineset.iterator(); it.hasNext();){
				AdjustableLine line = (AdjustableLine) it.next();
				if(!sm.isDrawingMode && line.isSelect){
					g2.setPaint(Color.GREEN);
					g2.setStroke(new BasicStroke(3));
					g2.draw(line.getLine());
					//g2.fill(line.getDirectionCircle());
					g2.setPaint(Color.DARK_GRAY);
					g2.draw(line.getCircle1());
					g2.draw(line.getCircle2());
				}
				else{
					g2.setStroke(new BasicStroke(3));
					g2.setPaint(Color.BLUE);
					g2.draw(line.getLine());
					g2.fill(line.getDirectionCircle());
				}
			}
		}
		g2.setPaint(Color.RED);
		if(drawline != null){
			g2.draw(drawline);
			//System.out.println("DrawCur");
		}
		g2.setStroke(curstroke);
		g2.dispose();
	}

	public void updateImage(BufferedImage image) {
		this.image = image;
		repaint();
	}
	
	public void updateImageSize(int w, int h) {
		this.setPreferredSize(new Dimension(w, h));
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.isMetaDown() && curline != null){
			int i = lineset.indexOf(curline);
			lineset.remove(i);
			if(lineset == sm.getLineSet1() && sm.getLineSet2().size() > i){
				sm.getLineSet2().remove(i);
			}
			else if(lineset == sm.getLineSet2() && sm.getLineSet1().size() > i){
				sm.getLineSet1().remove(i);
			}
			curline = null;
			circle1Click = false;
			circle2Click = false;
			lineClick = false;
			sm.notifyAllListener();
		}
		else if(!e.isMetaDown() && lineset != null && !sm.isDrawingMode){
			//System.out.println("Hi");
			for(Iterator<AdjustableLine> it = sm.getLineSet1().iterator(); it.hasNext();){
				AdjustableLine line = (AdjustableLine) it.next();
				line.isSelect = false;
			}
			
			for(Iterator<AdjustableLine> it = sm.getLineSet2().iterator(); it.hasNext();){
				AdjustableLine line = (AdjustableLine) it.next();
				line.isSelect = false;
			}
			
			sm.notifyAllListener();
			
			for(Iterator<AdjustableLine> it = lineset.iterator(); it.hasNext();){
				AdjustableLine line = (AdjustableLine) it.next();
				if(line.getLine().intersects(e.getX()-3, e.getY()-3, 6, 6)){
					line.isSelect = true;
					curline = line;
					int index = lineset.indexOf(line);
					if(lineset == sm.getLineSet1() && sm.getLineSet2().size() > index){
						sm.getLineSet2().get(index).isSelect = true;
					}
					else if(lineset == sm.getLineSet2() && sm.getLineSet1().size() > index){
						sm.getLineSet1().get(index).isSelect = true;
					}
					
				}
			}
			sm.notifyAllListener();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent e) {
		if(sm.isDrawingMode){
			drawline = new Line2D.Double(e.getX(), e.getY(), e.getX(), e.getY());
		}
		if(curline != null){
			if(curline.getCircle1().intersects(e.getX()-3, e.getY()-3, 6, 6)){
				circle1Click = true;
			}
			else if(curline.getCircle2().intersects(e.getX()-3, e.getY()-3, 6, 6)){
				circle2Click = true;
			}
			else if(curline.getLine().intersects(e.getX()-3, e.getY()-3, 6, 6)){
				lineClick = true;
				curclick = new Point2D.Double(e.getX(), e.getY());
				curclickline = new Line2D.Double();
				curclickline.setLine(curline.getLine());
			}
		}
		sm.notifyAllListener();
	}

	public void mouseReleased(MouseEvent e) {
		if(drawline != null && sm.isDrawingMode){
			AdjustableLine al = new AdjustableLine(drawline);
			lineset.add(al);
			drawline = null;
		}
		circle1Click = false;
		circle2Click = false;
		lineClick = false;
		curclick = null;
		curclickline = null;
		sm.notifyAllListener();
	}

	public void mouseDragged(MouseEvent e) {
		if(drawline != null && sm.isDrawingMode){
			drawline.x2 = e.getX();
			drawline.y2 = e.getY();
		}
		if(curline != null && !sm.isDrawingMode){
			if(lineClick){
				Point2D.Double point = new Point2D.Double(e.getX(), e.getY());
				double x1 = curclickline.x1 + (point.x - curclick.x);
				double y1 = curclickline.y1 + (point.y - curclick.y);
				double x2 = curclickline.x2 + (point.x - curclick.x);
				double y2 = curclickline.y2 + (point.y - curclick.y);
				curline.setLine(x1, y1, x2, y2);
			}
			else if(circle1Click){
				curline.setLinePoint1(e.getX(), e.getY());
			}
			else if(circle2Click){
				curline.setLinePoint2(e.getX(), e.getY());
			}
		}
		sm.notifyAllListener();
	}

	public void mouseMoved(MouseEvent e) {

	}

	public void updateAdjustableLineSet(ArrayList<AdjustableLine> line) {
		this.lineset = line;
		repaint();
	}
}