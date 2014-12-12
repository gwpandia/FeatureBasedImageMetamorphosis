package data;

import interfaces.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class SimpleModel{
	private BufferedImage image1;
	private ArrayList<AdjustableLine> lineset1;
	private BufferedImage image2;
	private ArrayList<AdjustableLine> lineset2;
	private ArrayList<ImageStateListener> isl1;
	private ArrayList<ImageStateListener> isl2;
	private ArrayList<AdjustableLineStateListener> asl1;
	private ArrayList<AdjustableLineStateListener> asl2;
	public boolean isDrawingMode;
	private ImageMorpher morpher;
	
	public SimpleModel(){
		lineset1 = new ArrayList<AdjustableLine>();
		lineset2 = new ArrayList<AdjustableLine>();
		isl1 = new ArrayList<ImageStateListener>();
		isl2 = new ArrayList<ImageStateListener>();
		asl1 = new ArrayList<AdjustableLineStateListener>();
		asl2 = new ArrayList<AdjustableLineStateListener>();
		isDrawingMode = false;
	}
	
	public BufferedImage getImage2(){
		return image2;
	}
	
	public ArrayList<AdjustableLine> getLineSet2(){
		return lineset2;
	}
	
	public BufferedImage getImage1(){
		return image1;
	}
	
	public ArrayList<AdjustableLine> getLineSet1(){
		return lineset1;
	}
	
	public boolean startMorphing(double a, double b, double p, int f){
		morpher = new ImageMorpher(image1, lineset1, image2, lineset2, a, b, p, f);
		return morpher.startMorphing();
	}
	
	public void saveSettings(String filename){
		try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(new File(filename)));
			os.writeObject(this.lineset1);
			os.writeObject(this.lineset2);
			os.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Saving error!");
			e.printStackTrace();
		}
	}
	
	public void readSettings(String filename){
		try {
			ObjectInputStream os = new ObjectInputStream(new FileInputStream(new File(filename)));
			this.lineset1 = (ArrayList<AdjustableLine>)os.readObject();
			this.lineset2 = (ArrayList<AdjustableLine>)os.readObject();
			os.close();	
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Saving error!");
			e.printStackTrace();
		}
		notifyAllListener();
	}
	
	public void readPicture1(String file){
		try {
			image1 = ImageIO.read(new File(file));
			
			//AffineTransform at = new AffineTransform();
			//double scale = Math.min(Constant.PANELWIDTH/(double)image1.getWidth(), Constant.PANELWIDTH/(double)image1.getHeight());
			//at.scale(scale, scale);
			//AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			//image1 = atop.filter(image1, null);
		} catch (IOException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "File open error!");
		}
		notifyAllListener();
	}
	
	public void readPicture2(String file){
		try {
			image2 = ImageIO.read(new File(file));
			//AffineTransform at = new AffineTransform();
			//double scale = Math.min(Constant.PANELWIDTH/(double)image2.getWidth(), Constant.PANELWIDTH/(double)image2.getHeight());
			//at.scale(scale, scale);
			//AffineTransformOp atop = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
			//image2 = atop.filter(image2, null);
		} catch (IOException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "File open error!");
		}
		notifyAllListener();
	}
	
	public void addImage1StateListener(ImageStateListener i){
		this.isl1.add(i);
	}
	
	public void removeImage1StateListener(ImageStateListener i){
		int a = isl1.indexOf(i);
		if(a >= 0){
			isl1.remove(a);
		}
	}
	
	public void addImage2StateListener(ImageStateListener i){
		this.isl2.add(i);
	}
	
	public void removeImage2StateListener(ImageStateListener i){
		int a = isl2.indexOf(i);
		if(a >= 0){
			isl2.remove(a);
		}
	}
	
	public void addAdjustableLineSet1StateListener(AdjustableLineStateListener i){
		this.asl1.add(i);
	}
	
	public void removeAdjustableLineSet1StateListener(AdjustableLineStateListener i){
		int a = asl1.indexOf(i);
		if(a >= 0){
			asl1.remove(a);
		}
	}
	
	public void addAdjustableLineSet2StateListener(AdjustableLineStateListener i){
		this.asl2.add(i);
	}
	
	public void removeAdjustableLineSet2StateListener(AdjustableLineStateListener i){
		int a = asl2.indexOf(i);
		if(a >= 0){
			asl2.remove(a);
		}
	}
	
	public void notifyImage1StateListener(){
		Iterator<ImageStateListener> it = isl1.iterator();
		while(it.hasNext()){
			ImageStateListener t = it.next();
			t.updateImage(image1);
			if(image1 != null){
				t.updateImageSize(image1.getWidth(), image1.getHeight());
			}
		}
	}
	
	public void notifyImage2StateListener(){
		Iterator<ImageStateListener> it = isl2.iterator();
		while(it.hasNext()){
			ImageStateListener t = it.next();
			t.updateImage(image2);
			if(image2 != null){
				t.updateImageSize(image2.getWidth(), image2.getHeight());
			}
		}
	}
	
	public void notifyAdjustableLineSet1StateListener(){
		Iterator<AdjustableLineStateListener> it = asl1.iterator();
		while(it.hasNext()){
			AdjustableLineStateListener t = it.next();
			t.updateAdjustableLineSet(this.lineset1);
		}
	}
	
	public void notifyAdjustableLineSet2StateListener(){
		Iterator<AdjustableLineStateListener> it = asl2.iterator();
		while(it.hasNext()){
			AdjustableLineStateListener t = it.next();
			t.updateAdjustableLineSet(this.lineset2);
		}
	}
	
	public void notifyAllListener(){
		notifyImage1StateListener();
		notifyImage2StateListener();
		notifyAdjustableLineSet1StateListener();
		notifyAdjustableLineSet2StateListener();
	}
}