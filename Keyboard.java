import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

/**
 * This class is a JPanel with a keyboard painted on it. Each key is an 
 * independent shape object, and a mouseListener tracks which key is pressed 
 * and changes the key color accordingly.
 */
class Keyboard extends JPanel implements MouseListener {

    // For testing
    JTextArea area = new JTextArea(5, 5);

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

    // unique key IDs for white and black keys, in ascending order
    private int[] bKeyTrans = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
    private int[] wKeyTrans = {0, 2, 4, 5, 7, 9, 11, 12, 14, 
            16, 17, 19, 21, 23, 24};

    // Color of pressed key
    private Color pressedKeyColor = Color.LIGHT_GRAY;

    // Sound source
    private final SoundGenerator synth = new SoundGenerator();

    // Data to control shifting from computer's turn to user's
    private static final int TIMER_DELAY = 500;
    private Timer timer;

    // Current state of the game, will record keys pressed when true
    private boolean reciteMode = false;

    // Melody creator and evaluator
    private MusicTeacher teacher = new MusicTeacher();

    // Constructors
    public Keyboard() {
        position = new Point(0, 0);
        initKeyboard();
    }

    private void initKeyboard() {
        createWhiteKeys();
        createBlackKeys();
        addMouseListener(this);
    }

    // Add all white keys to the array
    private void createWhiteKeys() {
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
    private void createBlackKeys() {
        blackKeys = new Rectangle2D[NUM_BLACK_KEYS];
        double x = position.getX() + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2;
        double y = position.getY();
        for (int i = 0; i < blackKeys.length; i++) {
            blackKeys[i] = new Rectangle2D.Double(x, y, BLACK_KEY_WIDTH, 
                    BLACK_KEY_HEIGHT);
            x = x + WHITE_KEY_WIDTH;

            // Skip over for adjacent white notes
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

    public int getNote(boolean isWhiteKey, int i) {
        if (isWhiteKey) 
            return LOW_C + wKeyTrans[i];
        else
            return LOW_C + bKeyTrans[i];
    }

    // Update information about which key is pressed. Update
    // keyboard graphic and play sound.
    public void mousePressed(MouseEvent e) {

        // Get point location
        Point p = new Point(e.getX(), e.getY());
        int keyID;
        
        // Check black keys first since they are on top of the white keys
        for (int i = 0; i < blackKeys.length; i++) {
            if (blackKeys[i].contains(p)) {
                keyID = getKeyID(blackKeys, i);
                if (reciteMode) {
                    if (!teacher.isGoodNote(keyID)) {
                        pressedKeyColor = Color.red;
                        area.append("Bad note. Start over. ");
                        teacher.resetMelody();
                    }
                }
                startNote(keyID);
                return;
            }
        }
        for (int i = 0; i < whiteKeys.length; i++) {
            if (whiteKeys[i].contains(p)) {
                keyID = getKeyID(whiteKeys, i);
                if (reciteMode) {
                    if (!teacher.isGoodNote(keyID)) {
                        pressedKeyColor = Color.red;
                        area.append("Bad note. Start over. ");
                        teacher.resetMelody();
                    }
                }
                startNote(keyID);
                return;
            }
        }
    }

    // Clear information about which key is pressed and reset keyboard
    public void mouseReleased(MouseEvent e) {
        endNote();
        if (reciteMode && teacher.isLasteNote()) {
            area.append("You did it! Get a new melody. ");
            teacher.clearMelody();
            teacher.resetMelody();
            reciteMode = false;
        }
    }
    
    private int getKeyID(Rectangle2D[] keyArray, int i) {
        if (keyArray == whiteKeys) return wKeyTrans[i];
        else return bKeyTrans[i];
    }

    private int getKeyIndex(int keyID) {
        for (int i = 0; i < wKeyTrans.length; i++)
            if (wKeyTrans[i] == keyID) return i;
        for (int i = 0; i < bKeyTrans.length; i++)
            if (bKeyTrans[i] == keyID) return i;
        return -1;
    }
    
    private boolean isWhiteKey(int keyID) {
        for (int i : wKeyTrans) {
            if (keyID == i) return true;
        }
        return false;
    }

    private void startNote(int keyID) {
        if (isWhiteKey(keyID)) {
            whiteKeyIsPressed = true;
            pressedWhiteKey = getKeyIndex(keyID);
        } else {
            blackKeyIsPressed = true;
            pressedBlackKey = getKeyIndex(keyID);
        }
        synth.playNote(keyID);
        repaint();
    }

    private void endNote() {
        synth.stopNote();
        blackKeyIsPressed = false;
        whiteKeyIsPressed = false;
        pressedKeyColor = Color.LIGHT_GRAY;
        repaint();
    }

    public void setMelodyLength(int length) {
        teacher.setMelodyLength(length);
    }

    public void setKey(int key) {
        teacher.setKey(key);
    }

    public void createMelody() {
        teacher.createMelody();
    }

    // TODO - Play a melody for the user to repeat
    public void playMelody() {
        area.setText("");
        area.append("Teacher plays melody. ");
        removeMouseListener(this);
        if (teacher.getMelodySize() == 0)
            teacher.createMelody();
        timer = new Timer(TIMER_DELAY, new TimerListener());
        timer.setInitialDelay(100);
        timer.start();
    }

    private void enterReciteMode() {
        area.append("Your turn! ");
        addMouseListener(this);
        reciteMode = true;
        teacher.resetMelody();
    }

    private class TimerListener implements ActionListener {
        private int i = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (i == 0) {
                startNote(teacher.getNextNote());
            }
            else if (i < teacher.getMelodySize()) {
                endNote();
                startNote(teacher.getNextNote());
            }
            else if (i == teacher.getMelodySize()) {
                endNote();
            }
            else {
                timer.stop();
                enterReciteMode();
            }
            i++;
        }
    }

    // All MouseListener implementations need these
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }

    class MusicTeacher {

        private ArrayList<Integer> melody = new ArrayList<Integer>();
        private int[] majorScale = {0, 2, 4, 5, 7, 9, 11, 12};
        private boolean isBadNote = false;
        private int i = 0;
        private int length = 2;
        private int firstNote = 0;

        public boolean getIsBadNote() {
            return isBadNote;
        }

        public boolean isGoodNote(int keyID) {
            if (i < getMelodySize() && keyID == getNextNote()) {
                return true;
            } else {
                i = 0;
                return false;
            }
        }

        public int getNextNote() {
            return melody.get(i++);
        }

        public void setKey(int key) {
            firstNote = key;
        }

        public void setMelodyLength(int len) {
            length = len;
        }

        public void createMelody() {
            clearMelody();
            melody.add(firstNote);
            for (int i = 0; i < length-1; i++) {
                while (true) {
                    int next = majorScale[(int) (Math.random()*8)] + firstNote;
                    if (next != melody.get(i)) {
                        melody.add(next);
                        break;
                    }
                }
            }
        }

        public int getMelodySize() {
            return melody.size();
        }

        public void resetMelody() {
            i = 0;
        }

        public boolean isLasteNote() {
            if (i == getMelodySize()) {
                return true;
            } else return false;
        }

        public void clearMelody() {
            melody.clear();
        }
    }


    private final int NUM_WHITE_KEYS = 15;
    private final double WHITE_KEY_WIDTH = 45;
    private final double WHITE_KEY_HEIGHT = 200;

    private final int NUM_BLACK_KEYS = 10;
    private final double BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT * 0.67;
    private final double BLACK_KEY_WIDTH = WHITE_KEY_WIDTH * 0.5;

    private final int LOW_C = 48;
}