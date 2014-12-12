package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.SimpleModel;

public class ButtonPanel extends JComponent implements ActionListener{
	private SimpleModel sm;
	private JButton loadImage1Button;
	private JButton loadImage2Button;
	private JButton morphButton;
	private JButton clearButton;
	private JButton exitButton;
	private JButton saveSettingButton;
	private JButton loadSettingButton;
	private JButton helpButton;
	private JToggleButton toggleDrawButton;
	private JTextField atextfield;
	private JTextField btextfield;
	private JTextField ptextfield;
	private JTextField ftextfield;
	public ButtonPanel(SimpleModel sm){
		this.sm = sm;
		this.setLayout(new GridLayout(13,1));
		createUI();
	}
	
	private void createUI(){
		loadImage1Button = new JButton("Load Pic1");
		loadImage1Button.addActionListener(this);
		this.add(loadImage1Button);
		loadImage2Button = new JButton("Load Pic2");
		loadImage2Button.addActionListener(this);
		this.add(loadImage2Button);
		toggleDrawButton = new JToggleButton("DrawMode");
		toggleDrawButton.setSelected(sm.isDrawingMode);
		toggleDrawButton.addActionListener(this);
		this.add(toggleDrawButton);
		clearButton = new JButton("Clear Lines");
		clearButton.addActionListener(this);
		this.add(clearButton);
		loadSettingButton = new JButton("Load Settings");
		loadSettingButton.addActionListener(this);
		this.add(loadSettingButton);
		saveSettingButton = new JButton("Save Settings");
		saveSettingButton.addActionListener(this);
		this.add(saveSettingButton);
		JPanel apanel = new JPanel();
		apanel.setBorder(BorderFactory.createTitledBorder("Value A [0..n]:"));
		atextfield = new JTextField("0");
		atextfield.setColumns(11);
		apanel.add(atextfield);
		this.add(apanel);
		JPanel bpanel = new JPanel();
		bpanel.setBorder(BorderFactory.createTitledBorder("Value B [0.5, 2]:"));
		btextfield = new JTextField("1.25");
		btextfield.setColumns(11);
		bpanel.add(btextfield);
		this.add(bpanel);
		JPanel ppanel = new JPanel();
		ppanel.setBorder(BorderFactory.createTitledBorder("Value P [0, 1]:"));
		ptextfield = new JTextField("0");
		ptextfield.setColumns(11);
		ppanel.add(ptextfield);
		this.add(ppanel);
		JPanel fpanel = new JPanel();
		fpanel.setBorder(BorderFactory.createTitledBorder("Frame No."));
		ftextfield = new JTextField("10");
		ftextfield.setColumns(11);
		fpanel.add(ftextfield);
		this.add(fpanel);
		morphButton = new JButton("Morph");
		morphButton.addActionListener(this);
		this.add(morphButton);
		helpButton = new JButton("Help");
		helpButton.addActionListener(this);
		this.add(helpButton);
		exitButton = new JButton("Quit");
		exitButton.addActionListener(this);
		this.add(exitButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loadImage1Button){
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images (*.jpg)", "jpg");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Load Image1");
			int option = chooser.showOpenDialog(this);
			if(option == JFileChooser.APPROVE_OPTION){
				String openfilename = chooser.getSelectedFile().getPath();
				sm.readPicture1(openfilename);
			}
		}
		else if(e.getSource() == loadImage2Button){
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG Images (*.jpg)", "jpg");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Load Image2");
			int option = chooser.showOpenDialog(this);
			if(option == JFileChooser.APPROVE_OPTION){
				String openfilename = chooser.getSelectedFile().getPath();
				sm.readPicture2(openfilename);
			}
		}
		else if(e.getSource() == toggleDrawButton){
			sm.isDrawingMode = !sm.isDrawingMode;
			sm.notifyAllListener();
			toggleDrawButton.setSelected(sm.isDrawingMode);
			if(sm.isDrawingMode){
				toggleDrawButton.setText("DrawMode");
			}
			else{
				toggleDrawButton.setText("EditMode");
			}
		}
		else if(e.getSource() == morphButton){
			double a = Double.parseDouble(atextfield.getText());
			double b = Double.parseDouble(btextfield.getText());
			double p = Double.parseDouble(ptextfield.getText());
			int f = Integer.parseInt(ftextfield.getText());
			MorphJob mj = new MorphJob(sm, a, b, p, f);
			mj.start();
			//sm.startMorphing(a, b, p, f);
		}
		else if(e.getSource() == clearButton){
			sm.getLineSet1().clear();
			sm.getLineSet2().clear();
			sm.notifyAllListener();
		}
		else if(e.getSource() == loadSettingButton){
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Morph Setting (*.fbi)", "fbi");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Load Settings");
			int option = chooser.showOpenDialog(this);
			if(option == JFileChooser.APPROVE_OPTION){
				String openfilename = chooser.getSelectedFile().getPath();
				sm.readSettings(openfilename);
			}
		}
		else if(e.getSource() == saveSettingButton){
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Morph Setting (*.fbi)", "fbi");
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File("."));
			chooser.setDialogTitle("Save Settings");
			int option = chooser.showSaveDialog(this);
			if(option == JFileChooser.APPROVE_OPTION){
				String savefilename = chooser.getSelectedFile().getPath();
				if(savefilename.lastIndexOf(".fbi") != savefilename.length()-4){
					savefilename += ".fbi";
				}
				sm.saveSettings(savefilename);
			}
		}
		else if(e.getSource() == helpButton){
			JFrame f = new JFrame("Help");
			JTextArea t = new JTextArea();
			t.setText("[Help]\n\n1. Load two images of same size. (Both width and height.)\n" +
					"2. Then draw the line pairs which mapped on both image.\n" +
					"   You can swith to EditMode to select line for moving, editing and even deleting(right click).\n" +
					"3. Adjust the parameters of a, b, p, and how many frames you want to generate.\n" +
					"4. Clicked Morph Button and wait. You can save or load the line settings.\n\n\n\n" +
					"This software was based on Feature-Based Image Metamorphosis, ACM SIGGraph 1992.\n" +
					"App was made by Chih-Hung, Liu. NCCU, R.O.C.\n");
			JScrollPane s = new JScrollPane(t);
			f.getContentPane().add(s, BorderLayout.CENTER);
			f.setResizable(false);
			f.setSize(550, 300);
			f.setVisible(true);
		}
		else if(e.getSource() == exitButton){
			if(JOptionPane.showConfirmDialog(this, "Really want to quit?", "Quit", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
				System.exit(0);
			}
		}
	}
	
	class MorphJob extends Thread{
		SimpleModel sm;
		double a, b, p;
		int f;
		public MorphJob(SimpleModel s, double a, double b, double p, int f){
			sm = s;
			this.a = a;
			this.b = b;
			this.p = p;
			this.f = f;
		}
		
		public void run(){
			sm.startMorphing(a, b, p, f);
		}
	}
}