import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

/**
 * This class is a JPanel with a keyboard painted on it. Each key is an 
 * independent shape object, and a mouseListener tracks which key is pressed 
 * and changes the key color accordingly.
 */
class Keyboard extends JPanel implements MouseListener {

	// Keyboard position in relation to containing panel
	private Point position; 

	// Arrays for the white and black keys in the keyboard
	private Rectangle2D[] whiteKeys;
	private Rectangle2D[] blackKeys;

	// Variables to track pressed keys
	private boolean whiteKeyIsPressed = false;
	private boolean blackKeyIsPressed = false;
	private int pressedWhiteKey = 0;
	private int pressedBlackKey = 0;
	
	// Map items from key arrays to notes (First key = 0)
	private int[] bKeyTrans = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
	int[] wKeyTrans = {0, 2, 4, 5, 7, 9, 11, 12, 14, 
			16, 17, 19, 21, 23, 24};

	// Color of pressed key
	private Color pressedKeyColor = Color.LIGHT_GRAY;

	// Sound source
	private final SoundGenerator synth = new SoundGenerator();
	
	// Data to control shifting from computer's turn to user's
	public static final String TIMER_COMPLETE = "timer complete";
	private static final int TIMER_DELAY = 1000;
	private Timer timer;
	
	// Constructors
	public Keyboard() {
		position = new Point(0, 0);
		initKeyboard();
	}

	public Keyboard(int x, int y) {
		position = new Point(x, y);
		initKeyboard();
	}

	private void initKeyboard() {
		setWhiteKeys();
		setBlackKeys();

		addMouseListener(this);
		
		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent pcEvt) {
				if (MyProgram.TIMER_COMPLETE.equals(pcEvt.getPropertyName()) && pcEvt.getNewValue() == Boolean.TRUE) {
					System.out.println("Your turn"); // Program starts analyzing what the user plays
				}
			}
		});
	}

	// Add all white keys to the array
	private void setWhiteKeys() {
		whiteKeys = new Rectangle2D[NUM_WHITE_KEYS];
		double x = position.getX();
		double y = position.getY();
		for (int i = 0; i < whiteKeys.length; i++) {
			whiteKeys[i] = new Rectangle2D.Double(x, y, WHITE_KEY_WIDTH, 
					WHITE_KEY_HEIGHT);
			x = x + WHITE_KEY_WIDTH;
		}
	}

	// Add all black keys to the array
	private void setBlackKeys() {
		blackKeys = new Rectangle2D[NUM_BLACK_KEYS];
		double x = position.getX() + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2;
		double y = position.getY();
		for (int i = 0; i < blackKeys.length; i++) {
			blackKeys[i] = new Rectangle2D.Double(x, y, BLACK_KEY_WIDTH, 
					BLACK_KEY_HEIGHT);
			x = x + WHITE_KEY_WIDTH;

			// Skip over for adjacent natural notes
			if (i%5 == 1 || i%5 == 4) {x = x + WHITE_KEY_WIDTH; }
		}
	}

	// Paint the JPanel
	@Override
	public void paintComponent(Graphics g) {
		// Cast g to Graphics2D to access RenderingHints, drawing objects, etc.
		Graphics2D g2 = (Graphics2D) g;

		// Change rendering so rectangle corners are clean
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(rh);

		// Draw the white keys
		for (int i = 0; i < whiteKeys.length; i++) {
			g2.setColor(Color.white);
			g2.fill(whiteKeys[i]);
			g2.setColor(Color.black);
			g2.draw(whiteKeys[i]);
		}

		// Color pressed white key, if applicable
		if (whiteKeyIsPressed) {
			g2.setColor(pressedKeyColor);
			g2.fill(whiteKeys[pressedWhiteKey]);
			g2.setColor(Color.black);
			g2.draw(whiteKeys[pressedWhiteKey]);
		}

		// Draw the black keys
		g2.setColor(Color.black);
		for (int i = 0; i < blackKeys.length; i++) {
			g2.fill(blackKeys[i]);
		}

		// Color pressed black key, if applicable
		if (blackKeyIsPressed) {
			g2.setColor(pressedKeyColor);
			g2.fill(blackKeys[pressedBlackKey]);
			g2.setColor(Color.black);
			g2.draw(blackKeys[pressedBlackKey]);
		}
	}

	// Update information about which key is pressed. Update
	// keyboard graphic and play sound.
	public void mousePressed(MouseEvent e) {

		// Get point location
		Point p = new Point(e.getX(), e.getY());

		// Check black keys first since they are on top of the white keys
		for (int i = 0; i < blackKeys.length; i++) {
			if (blackKeys[i].contains(p)) {
				startNote(false, i);
				return;
			}
		}
		for (int i = 0; i < whiteKeys.length; i++) {
			if (whiteKeys[i].contains(p)) {
				startNote(true, i);
				return;
			}
		}
		
		// If user clicked outside keyboard, auto-play melody TODO
		compTurn();
	}

	// Clear information about which key is pressed and reset keyboard
	public void mouseReleased(MouseEvent e) {
		endNote();
	}
	
	private void startNote(boolean keyIsWhite, int pressedKey) {
		if (keyIsWhite) {
			whiteKeyIsPressed = true;
			pressedWhiteKey = pressedKey;
			synth.playNote(LOW_C + wKeyTrans[pressedKey]);
		} else {
			blackKeyIsPressed = true;
			pressedBlackKey = pressedKey;
			synth.playNote(LOW_C + bKeyTrans[pressedKey]);
		}
		repaint();
	}
	
	private void endNote() {
		synth.stopNote();
		blackKeyIsPressed = false;
		whiteKeyIsPressed = false;
		repaint();
	}
	
	// Listens to timer
	class AutoPlay implements ActionListener {
		
		private int note = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			endNote();
			startNote(true, note);
			note++;
			if (note < 14) note++;
			else { 
				timer.stop();
				firePropertyChange(TIMER_COMPLETE, false, true);
			}
		}
	}
	
	// TODO - Play a melody for the user to repeat
	public void compTurn() {
		if (timer != null && timer.isRunning()) {
			return;
		}
		timer = new Timer(TIMER_DELAY, new TimerListener());
		timer.start();
	}
	
	private class TimerListener implements ActionListener {
		private int note = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			endNote();
			startNote(true, note);
			if (note < 7) note++;
			else { 
				timer.stop();
				firePropertyChange(TIMER_COMPLETE, false, true);
			}
		}
	}

	// All MouseListener implementations need these
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	private final int NUM_WHITE_KEYS = 15;
	private final double WHITE_KEY_WIDTH = 45;
	private final double WHITE_KEY_HEIGHT = 200;

	private final int NUM_BLACK_KEYS = 10;
	private final double BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT * 0.67;
	private final double BLACK_KEY_WIDTH = WHITE_KEY_WIDTH * 0.5;
	
	private final int LOW_C = 48;
}

// -------------------------------------------------------------- //

/** 
 * An interactive keyboard that you can play with a mouse.
 *
 */
public class InteractiveKeyboard extends JFrame {
	
	// Initialize window and add keyboard
	private InteractiveKeyboard() {
		setTitle("Interactive Keyboard");
		setSize(775, 300);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Create and show application window
	public static void main(String[] args) {

		// Code is run on an event-dispatching thread
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Keyboard kb = new Keyboard(50, 50);
				InteractiveKeyboard window = new InteractiveKeyboard();
				window.add(kb);
				window.setVisible(true);
			}
		});   
	}
}