package data;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel;

public class ImageMorpher{
	private BufferedImage image1;
	private ArrayList<AdjustableLine> lineset1;
	private BufferedImage image2;
	private ArrayList<AdjustableLine> lineset2;
	private ArrayList<BufferedImage> imagelist;
	private ArrayList<PointPair> pointpair;
	private double a;
	private double b;
	private double p;
	private int nframe;
	private int curframe;
	private int offset;
	private Timer time;
	private ArrayList<ArrayList<AdjustableLine>> interpolatedLine;
	private JFrame f;
	private JPanel messagePanel;
	private JScrollPane mspane;
	private JTextArea message;
	public ImageMorpher(BufferedImage image1, ArrayList<AdjustableLine> lineset1, 
			BufferedImage image2, ArrayList<AdjustableLine> lineset2, double a, double b, double p, int frame){
		this.image1 = image1;
		this.image2 = image2;
		this.lineset1 = lineset1;
		this.lineset2 = lineset2;
		imagelist = new ArrayList<BufferedImage>();
		pointpair = new ArrayList<PointPair>();
		this.a = a;
		this.b = b;
		this.p = p;
		this.nframe = frame;
		this.curframe = 0;
		this.offset = 0;
		message = new JTextArea();
		message.setColumns(30);
		mspane = new JScrollPane(message);
		messagePanel = new JPanel();
		messagePanel.setLayout(new FlowLayout());
		messagePanel.setBorder(BorderFactory.createTitledBorder("Message:"));
		messagePanel.add(mspane);
	}


	public boolean startMorphing(){
		if(a < 0 || b < 0.5 || b > 2 || p < 0 || p > 1 || nframe <= 0){
			JOptionPane.showMessageDialog(null, "Settings error!");
			return false;
		}
		if(!checkValidity()){
			JOptionPane.showMessageDialog(null, "Operation error!");
			return false;
		}
		//showMessage();
		message.setText("");
		System.out.println("Start Morphing...");
		message.append("Start Morphing...\n");
		interpolateLine();
		genetateImage();
		writeImage();
		System.out.println("All done...");
		message.append("All done...\n");
		showImages();
		return true;
	}
	
	public class TimerListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			f.repaint();
			curframe += offset;
			if(curframe == imagelist.size()-1){
				offset = -1;
			}
			if(curframe == 0){
				offset = 1;
			}
		}
	}
	
	public class PreviewPanel extends JPanel{
		protected Graphics2D g2;
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(imagelist.get(curframe), 0, 0, null);
		}
	}
	
	public void showMessage(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				try{
			        UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
			    }
				catch(Exception e){
			        System.out.println("Substance Raven Graphite failed to initialize");
			    }
				JFrame fr = new JFrame("Message");
				fr.getContentPane().add(mspane, BorderLayout.CENTER);
				fr.setPreferredSize(new Dimension(300, 30));
				fr.setVisible(true);
			}
		});
	}
	
	public void showImages(){
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				try{
			        UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
			    }
				catch(Exception e){
			        System.out.println("Substance Raven Graphite failed to initialize");
			    }
		time = new Timer(33, new TimerListener());
		f = new JFrame("Preview");
		f.getContentPane().setPreferredSize(new Dimension(image2.getWidth(), image2.getHeight()));
		f.getContentPane().add(new PreviewPanel(), BorderLayout.CENTER);
		f.pack();
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				time.stop();
			}
		});
		f.setResizable(false);
		f.setVisible(true);
		time.start();
			}
		});
	}
	
	public void writeImage(){
		int i = 1;
		imagelist.add(0, image2);
		imagelist.add(imagelist.size(), image1);
		System.out.println("    Writing Image...");
		message.append("    Writing Image...\n");
		for(Iterator<BufferedImage> it = imagelist.iterator(); it.hasNext();){
			BufferedImage image = it.next();
			try {
				System.out.println("    Writing Image "+ i);
				message.append("    Writing Image "+ i +"\n");
				ImageIO.write(image, "jpg", new File("image"+i+".jpg"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			i++;
		}
	}
	
	public void genetateImage(){
		imagelist.clear();
		System.out.println("Generating Image...");
		message.append("Generating Image...\n");
		for(int i = 1; i <= this.nframe; i++){
			if(i==16){
			double ratiof = (double)i/(double)this.nframe;
			double ratiob = 1.0 - ratiof;
			BufferedImage destImage = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			ArrayList<AdjustableLine> curlineset = interpolatedLine.get(i-1);
			pointpair.clear();
			System.out.println("    Mapping point image2 and Frame "+ i);
			message.append("    Mapping point image2 and Frame "+ i +"\n");
			for(int j = 0; j < destImage.getHeight(); j++){
				for(int k = 0; k < destImage.getWidth(); k++){
					mappingPoint(new Point2D.Double(k, j), lineset2, curlineset);
				}
			}
			
			System.out.println("    Rendering image2 and Frame "+ i);
			message.append("    Rendering image2 and Frame "+ i +"\n");
			for(Iterator<PointPair> it = pointpair.iterator(); it.hasNext();){
				PointPair ppair = it.next();
				int x = (int)Math.round(ppair.getX().x);
				int y = (int)Math.round(ppair.getX().y);
				int xp = (int)Math.round(ppair.getXp().x);
				int yp = (int)Math.round(ppair.getXp().y);
				x = ImageMorpher.adjustRange(x, 0, destImage.getWidth());
				y = ImageMorpher.adjustRange(y, 0, destImage.getHeight());
				xp = ImageMorpher.adjustRange(xp, 0, image2.getWidth());
				yp = ImageMorpher.adjustRange(yp, 0, image2.getHeight());
				if(x >= 0 && x < destImage.getWidth() && y >= 0 && y < destImage.getHeight() &&
				    xp >= 0 && xp <image2.getWidth() && yp >= 0 && yp < image2.getHeight()){
					Color tempc = new Color(image2.getRGB(xp, yp));
					int alpha = (int)Math.round(tempc.getAlpha() * ratiob);
					int red = (int)Math.round(tempc.getRed() * ratiob);
					int blue = (int)Math.round(tempc.getBlue() * ratiob);
					int green = (int)Math.round(tempc.getGreen() * ratiob);
					//System.out.println(red + " " + green + " " + blue);
					Color tempc2 = new Color(red, green, blue, alpha);
					destImage.setRGB(x, y, tempc2.getRGB());
				}
			}
			
			pointpair.clear();
			System.out.println("    Mapping point image1 and Frame "+ i);
			message.append("    Mapping point image1 and Frame "+ i+"\n");
			for(int j = 0; j < destImage.getHeight(); j++){
				for(int k = 0; k < destImage.getWidth(); k++){
					mappingPoint(new Point2D.Double(k, j), lineset1, curlineset);
				}
			}
			
			System.out.println("    Rendering image1 and Frame "+ i);
			message.append("    Rendering image1 and Frame "+ i+"\n");
			for(Iterator<PointPair> it = pointpair.iterator(); it.hasNext();){
				PointPair ppair = it.next();
				int x = (int)Math.round(ppair.getX().x);
				int y = (int)Math.round(ppair.getX().y);
				int xp = (int)Math.round(ppair.getXp().x);
				int yp = (int)Math.round(ppair.getXp().y);
				x = ImageMorpher.adjustRange(x, 0, destImage.getWidth());
				y = ImageMorpher.adjustRange(y, 0, destImage.getHeight());
				xp = ImageMorpher.adjustRange(xp, 0, image1.getWidth());
				yp = ImageMorpher.adjustRange(yp, 0, image1.getHeight());
				if(x >= 0 && x < destImage.getWidth() && y >= 0 && y < destImage.getHeight() &&
				    xp >= 0 && xp <image1.getWidth() && yp >= 0 && yp < image1.getHeight()){
					Color tempc = new Color(image1.getRGB(xp, yp));
					Color tempi = new Color(destImage.getRGB(x, y));
					int alpha = (int)Math.round(tempc.getAlpha() * ratiof) + tempi.getAlpha();
					int red = (int)Math.round(tempc.getRed() * ratiof) + tempi.getRed();
					int blue = (int)Math.round(tempc.getBlue() * ratiof) + tempi.getBlue();
					int green = (int)Math.round(tempc.getGreen() * ratiof) + tempi.getGreen();
					if(alpha > 255) alpha = 255;
					if(red > 255) red = 255;
					if(blue > 255) blue = 255;
					if(green > 255) green = 255;
					Color tempc2 = new Color(red, green, blue, alpha);
					destImage.setRGB(x, y, tempc2.getRGB());
				}
			}
			
			imagelist.add(destImage);
			
			}//
		}
	}
	
	private void interpolateLine(){
		interpolatedLine = new ArrayList<ArrayList<AdjustableLine>>();
		//interpolatedLine.add(this.lineset2);
		for(int i = 1; i <= this.nframe; i++){
			System.out.println("    Interpolate Lines of Frame "+ i);
			message.append("    Interpolate Lines of Frame "+ i+"\n");
			ArrayList<AdjustableLine> linesets = new ArrayList<AdjustableLine>();
			for(Iterator<AdjustableLine> it = lineset2.iterator(); it.hasNext();){
				AdjustableLine line = it.next();
				AdjustableLine linep = lineset1.get(lineset2.indexOf(line));
				double dx1 = (linep.getLine().x1 - line.getLine().x1) / (double)this.nframe;
				double dy1 = (linep.getLine().y1 - line.getLine().y1) / (double)this.nframe;
				double dx2 = (linep.getLine().x2 - line.getLine().x2) / (double)this.nframe;
				double dy2 = (linep.getLine().y2 - line.getLine().y2) / (double)this.nframe;
				double x1 = line.getLine().x1 + dx1*(double)i;
				double y1 = line.getLine().y1 + dy1*(double)i;
				double x2 = line.getLine().x2 + dx2*(double)i;
				double y2 = line.getLine().y2 + dy2*(double)i;
				Line2D.Double curline = new Line2D.Double(x1, y1, x2, y2);
				linesets.add(new AdjustableLine(curline));
			}
			interpolatedLine.add(linesets);
		}
		//interpolatedLine.add(this.lineset1);
	}
	
	private void mappingPoint(Point2D.Double X, ArrayList<AdjustableLine> slineset, ArrayList<AdjustableLine> dlineset){
		Point2D.Double DSUM = new Point2D.Double(0, 0);
		//pointpair.clear();
		double weightsum = 0;
		for(Iterator<AdjustableLine> it = dlineset.iterator(); it.hasNext(); ){
			AdjustableLine line = it.next();
			AdjustableLine linep = slineset.get(dlineset.indexOf(line));
			Point2D.Double P = new Point2D.Double(line.getLine().x1, line.getLine().y1);
			Point2D.Double Q = new Point2D.Double(line.getLine().x2, line.getLine().y2);
			Point2D.Double Pp = new Point2D.Double(linep.getLine().x1, linep.getLine().y1);
			Point2D.Double Qp = new Point2D.Double(linep.getLine().x2, linep.getLine().y2);
			double u = formula1(X, P, Q);
			double v = formula2(X, P, Q);
			Point2D.Double Xp = formula3(Pp, Qp, u, v);
			Point2D.Double D = vectorSubtract(Xp, X);
			//double dist = line.getLine().ptLineDist(X);
			double dist = line.getLine().ptSegDist(X);
			double length = getLineLength(line.getLine());
			double weight = Math.pow((Math.pow(length, p)/(a+dist)), b);
			DSUM = vectorAdd(DSUM, scalarMult(weight, D));
			weightsum += weight;
		}
		Point2D.Double Xp = vectorAdd(X, scalarDiv(weightsum, DSUM));
		pointpair.add(new PointPair(X, Xp));
	}
	
	private double getLineLength(Line2D.Double line){
		double x = Math.abs(line.x2 - line.x1);
		double y = Math.abs(line.y2 - line.y1);
		return Math.sqrt(x*x + y*y);
	}
	
	private double formula1(Point2D.Double X, Point2D.Double P, Point2D.Double Q){
		return ( vectorDot(vectorSubtract(X, P), vectorSubtract(Q, P)) / 
				getDistanceSQ(vectorSubtract(Q, P)));
	}
	
	private double formula2(Point2D.Double X, Point2D.Double P, Point2D.Double Q){
		return (vectorDot(vectorSubtract(X, P), getPerpendicular(vectorSubtract(Q, P))) / 
				getDistance(vectorSubtract(Q, P)));
	}
	
	private Point2D.Double formula3(Point2D.Double Pp, Point2D.Double Qp, double u, double v){
		Point2D.Double point = (vectorAdd(Pp, vectorAdd(scalarMult(u, vectorSubtract(Qp, Pp)), 
				scalarMult(v / getDistance(vectorSubtract(Qp, Pp)), getPerpendicular(vectorSubtract(Qp, Pp))))));
		return point;
	}
	
	private Point2D.Double vectorAdd(Point2D.Double vec1, Point2D.Double vec2){
		return new Point2D.Double(vec1.x + vec2.x, vec1.y + vec2.y);
	}
	
	private Point2D.Double vectorSubtract(Point2D.Double vec1, Point2D.Double vec2){
		return new Point2D.Double(vec1.x - vec2.x, vec1.y - vec2.y);
	}
	
	private Point2D.Double scalarMult(double scalar, Point2D.Double vector){
		return new Point2D.Double(vector.x * scalar, vector.y * scalar);
	}
	
	private Point2D.Double scalarDiv(double scalar, Point2D.Double vector){
		if(scalar == 0){
			return null;
		}
		return new Point2D.Double(vector.x / scalar, vector.y / scalar); 
	}
	
	private double vectorDot(Point2D.Double vec1, Point2D.Double vec2){
		return (vec1.x * vec2.x + vec1.y * vec2.y);
	}
	
	private double getDistance(Point2D.Double vector){
		return Math.sqrt(getDistanceSQ(vector));
	}
	
	private double getDistanceSQ(Point2D.Double vector){
		return (vector.x * vector.x + vector.y * vector.y);
	}
	
	private Point2D.Double getPerpendicular(Point2D.Double vector){
		return new Point2D.Double(-vector.y, vector.x);
	}
	
	private boolean checkValidity(){
		if( this.image1 == null || this.image2 == null ||
				this.lineset1 == null || this.lineset2 == null ||
				this.lineset1.size() != this.lineset2.size() ||
			this.image1.getWidth() != this.image2.getWidth() ||
			this.image1.getHeight() != this.image2.getHeight()){
				return false;
		}
		return true;
	}
	
	public double getA() {
		return a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}
	
	public static int adjustRange(int test, int min, int max){
		if(test >= max){
			test = max - 1;
		}
		if(test < min){
			test = min;
		}
		return test;
	}
}