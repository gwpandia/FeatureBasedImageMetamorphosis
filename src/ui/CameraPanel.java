package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.JPanel;

public class CameraPanel extends JPanel implements ActionListener{
	static protected Graphics2D g2;
	private Player player = null;
	private BufferedImage buffImg = null;
	private Timer timer;
	private FrameGrabbingControl frameGrabber;

	public CameraPanel(){
		/*Vector devices = CaptureDeviceManager.getDeviceList(null);
		CaptureDeviceInfo cdi = null;
		for (Iterator i = devices.iterator(); i.hasNext();) {
			cdi = (CaptureDeviceInfo) i.next();
			if (cdi.getName().startsWith("vfw:")){
				System.out.println(cdi.getName());
				//break;
			}
		}*/
		/*CaptureDeviceInfo cdi = null;
		String str1 = "vfw:Logitech USB Video Camera:0";
		String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
		cdi = CaptureDeviceManager.getDevice(str2);*/
		CaptureDeviceInfo cdi =
			CaptureDeviceManager.getDevice("vfw:Microsoft WDM Image Capture (Win32):0");

		new Timer(3000, this).start();
		try {
			player = Manager.createRealizedPlayer(cdi.getLocator());
			player.start();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		frameGrabber = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
		this.setPreferredSize(new Dimension(500, 500));
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setBackground(Color.WHITE);
		if (buffImg != null) {
			g.drawImage(buffImg, 0, 0, this);
		}

	}

	private void grab() {
		Buffer buf = frameGrabber.grabFrame();
		// Convert frame to an buffered image so it can be processed and saved
		Image img = (new BufferToImage((VideoFormat) buf.getFormat())
				.createImage(buf));
		buffImg = new BufferedImage(img.getWidth(this), img.getHeight(this),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buffImg.createGraphics();
		g.drawImage(img, null, null);
		g.setColor(Color.darkGray);
		g.setFont(new Font("Tahoma", Font.PLAIN, 12)
				.deriveFont(AffineTransform.getRotateInstance(1.57)));
		g.drawString((new Date()).toString(), 5, 5);
	}

	public static void createAndShowGui() {
		JFrame frame = new JFrame("Frame Grabber");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new CameraPanel());
		frame.setSize(328, 270);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGui();
			}
		});
	}
	
	public void actionPerformed(ActionEvent arg0) {
		grab();
		repaint();
	}
}