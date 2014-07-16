import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** 
 * An interactive keyboard that you can play with a mouse.
 *
 */
public class MusicalEarTrainer extends JFrame implements ActionListener {
	
	Keyboard keyboard = new Keyboard();
	
	JButton playButton = new JButton("Play");
	JLabel listLabel = new JLabel("Melody Length: ");
	Integer[] lengthArray = {2, 3, 4, 5, 6};
	JComboBox lengthList = new JComboBox(lengthArray);
	
	// Initialize window and add keyboard
	private MusicalEarTrainer() {
		setTitle("Interactive Keyboard");
		setSize(45*15+1, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea area = keyboard.area;
		
		JPanel controlPanel = new JPanel();
		lengthList.setSelectedItem(2);
		controlPanel.add(listLabel);
		controlPanel.add(lengthList);
		controlPanel.add(playButton);
		
		playButton.addActionListener(this);
		lengthList.addActionListener(this);
				
		getContentPane().add(keyboard, BorderLayout.CENTER);
		keyboard.area.setLineWrap(true);
		getContentPane().add(area, BorderLayout.PAGE_END);
		getContentPane().add(controlPanel, BorderLayout.PAGE_START);
	}
	
	public void actionPerformed (ActionEvent e) {
		if (e.getSource() == playButton) {
			keyboard.playMelody();
		} else if (e.getSource() == lengthList) {
			int length = (int) lengthList.getSelectedItem();
			keyboard.setMelodyLength(length);
		}
	}

	// Create and show application window
	public static void main(String[] args) {

		// Code is run on an event-dispatching thread
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				MusicalEarTrainer window = new MusicalEarTrainer();
				window.setVisible(true);
			}
		});   
	}
}