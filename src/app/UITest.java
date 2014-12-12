package app;

import java.awt.*;
import javax.swing.*;
import org.jvnet.substance.skin.*;
import ui.*;
import data.Constant;
import data.SimpleModel;

public class UITest{
	public UITest(){
		
	}
	
	public static void main(String [] args){
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				try{
			        UIManager.setLookAndFeel(new SubstanceOfficeBlue2007LookAndFeel());
			    }
				catch(Exception e){
			        System.out.println("Substance Raven Graphite failed to initialize");
			    }
				SimpleModel sm = new SimpleModel();
				ButtonPanel bp = new ButtonPanel(sm);
				ImagePanel ip1 = new ImagePanel(sm, 1);
				sm.addImage1StateListener(ip1);
				sm.addAdjustableLineSet1StateListener(ip1);
				ImagePanel ip2 = new ImagePanel(sm, 2);
				sm.addImage2StateListener(ip2);
				sm.addAdjustableLineSet2StateListener(ip2);
				JFrame frame = new JFrame("Feature-Based Image Morpher by Pandia. Chih-Hung, Liu");
				JScrollPane jsp1 = new JScrollPane(ip1);
				JScrollPane jsp2 = new JScrollPane(ip2);
				jsp1.setPreferredSize(new Dimension(Constant.PANELWIDTH, Constant.PANELWIDTH));
				jsp2.setPreferredSize(new Dimension(Constant.PANELWIDTH, Constant.PANELWIDTH));
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(jsp1, BorderLayout.WEST);
				frame.getContentPane().add(jsp2, BorderLayout.CENTER);
				frame.getContentPane().add(bp, BorderLayout.EAST);
				frame.pack();
				frame.setBackground(Color.WHITE);
				frame.setResizable(false);
				frame.setVisible(true);
			}
		});
	}
}