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
	
	// Initialize window and add keyboard
	private MusicalEarTrainer() {
		setTitle("Interactive Keyboard");
		setSize(45*15+1, 350);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JTextArea area = keyboard.area;
				
		getContentPane().add(playButton, BorderLayout.PAGE_START);
		getContentPane().add(keyboard, BorderLayout.CENTER);
		keyboard.area.setLineWrap(true);
		getContentPane().add(area, BorderLayout.PAGE_END);
				
		playButton.addActionListener(this);
	}
	
	public void actionPerformed (ActionEvent ev) {
		if (ev.getSource() == playButton) {
			keyboard.playMelody();
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