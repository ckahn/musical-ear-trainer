import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 * An interactive keyboard that you can play with a mouse.
 *
 */
public class InteractiveKeyboard extends JFrame implements ActionListener {
	
	Keyboard keyboard = new Keyboard();
	JButton playButton = new JButton("Play");
	
	// Initialize window and add keyboard
	private InteractiveKeyboard() {
		setTitle("Interactive Keyboard");
		setSize(45*15+1, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getContentPane().add(keyboard, BorderLayout.CENTER);
		getContentPane().add(playButton, BorderLayout.PAGE_END);
		
		playButton.addActionListener(this);
	}
	
	public void actionPerformed (ActionEvent ev) {
		if (ev.getSource() == playButton) {
			keyboard.compTurn();
		}
	}

	// Create and show application window
	public static void main(String[] args) {

		// Code is run on an event-dispatching thread
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				InteractiveKeyboard window = new InteractiveKeyboard();
				window.setVisible(true);
			}
		});   
	}
}