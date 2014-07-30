import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;

/**
 * This class is a JPanel with a keyboard painted on it. Each key is an 
 * independent shape object, and a mouseListener tracks which key is pressed 
 * and changes the key color accordingly.
 */
@SuppressWarnings("serial")
class Keyboard extends JPanel implements MouseListener {

    // keyboard position in relation to containing panel
    private Point position; 

    // arrays for the piano keys
    private Rectangle2D[] whiteKeys;
    private Rectangle2D[] blackKeys;

    // variables to track pressed keys
    private boolean whiteKeyIsPressed = false;
    private boolean blackKeyIsPressed = false;
    private int pressedWhiteKey;
    private int pressedBlackKey;
    
    private Color pressedKeyColor = Color.LIGHT_GRAY;

    // unique key IDs for white and black keys, in ascending order
    private int[] blackKeyIDs = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
    private int[] whiteKeyIDs = {0, 2, 4, 5, 7, 9, 11, 12, 14, 
            16, 17, 19, 21, 23, 24};

    // MIDI synthesizer
    private SoundGenerator synth;
    
    // timer for auto-playing notes
    private Timer timer;
    
    // set mode
    private Modes mode;
    private PropertyChangeSupport rPcs = new PropertyChangeSupport(this);
    
    // whether to repeat the current melody or create a new one
    private boolean repeatMelody;
    private PropertyChangeSupport rRm = new PropertyChangeSupport(this);
    
    // whether to show only the first auto-played note, otherwise show all
    private boolean firstNoteOnly;

    // creates melodies and evaluates user's performance
    private MusicTeacher teacher = new MusicTeacher();
    
    // tempo of auto-played melody, in beats per minute
    private int tempo = 100;

    // constructors
    public Keyboard() {
        position = new Point(0, 0);
        initKeyboard();
    }
    
    public Keyboard(int x, int y) {
        position = new Point(x, y);
        initKeyboard();
    }

    private void initKeyboard() {
        createWhiteKeys();
        createBlackKeys();
        addMouseListener(this);
        try {
            synth = new SoundGenerator();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not access your computer's MIDI synthesizer.");
            System.exit(-1);
        }
        setMode(Modes.IDLE);
        setRepeatMelody(false);
    }

    // add all white keys to the array
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

    // add all black keys to the array
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

    // paint the JPanel
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

    /**
     * Returns an integer that the synthesizer converts to a pitch.
     * @param isWhiteKey whether the clicked piano key is white
     * @param i the index of the key in its array
     * @return an integer representing the pitch of the clicked piano key
     */
    public int getNoteNumber(boolean isWhiteKey, int i) {
        if (isWhiteKey) 
            return LOW_C + whiteKeyIDs[i];
        else
            return LOW_C + blackKeyIDs[i];
    }

    public void mousePressed(MouseEvent e) {
        
        // Get point location
        Point p = new Point(e.getX(), e.getY());
        
        // Check black keys first since they are on top of the white keys
        for (int i = 0; i < blackKeys.length; i++) {
            if (blackKeys[i].contains(p)) {
                playNote(blackKeys, i);
                return;
            }
        }
        for (int i = 0; i < whiteKeys.length; i++) {
            if (whiteKeys[i].contains(p)) {
                playNote(whiteKeys, i);
                return;
            }
        }
    }

    private void playNote(Rectangle2D[] keyArray, int i) {
        int keyID = getKeyID(keyArray, i);
        if (mode == Modes.RECITE) {
            if (!teacher.isGoodNote(keyID)) {
                pressedKeyColor = Color.red;
                teacher.resetMelody();
            }
        }
        startNote(keyID);
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

    // Clear information about which key is pressed and reset keyboard
    public void mouseReleased(MouseEvent e) {
        endNote();
        if (mode == Modes.RECITE && teacher.isLastNote()) {
            teacher.clearMelody();
            teacher.resetMelody();
            setRepeatMelody(false);
            setMode(Modes.IDLE);
        }
    }

    private void endNote() {
        synth.stopNote();
        blackKeyIsPressed = false;
        whiteKeyIsPressed = false;
        pressedKeyColor = Color.LIGHT_GRAY;
        repaint();
    }
    
    private void setRepeatMelody(boolean repeat) {
        boolean oldRepeat = repeatMelody;
        repeatMelody = repeat;
        rRm.firePropertyChange("repeatMelody", oldRepeat, repeatMelody);
    }

    /*
    private void flashPiano() {
        pressedKeyColor = new Color(64, 110, 222);
        whiteKeyIsPressed = true;
        pressedWhiteKey = whiteKeys.length-1;
        timer = new Timer(30, new ActionListener() {
            int green = 100;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                pressedKeyColor = new Color(64, green, 222);
                repaint();
                green += 10;
                pressedWhiteKey--;
                if (pressedWhiteKey < 0) {
                    whiteKeyIsPressed = false;
                    pressedKeyColor = Color.LIGHT_GRAY;
                    repaint();
                    timer.stop();
                }
            }  
        });
        timer.start();
    }*/
    
    private int getKeyID(Rectangle2D[] keyArray, int i) {
        if (keyArray == whiteKeys) return whiteKeyIDs[i];
        else return blackKeyIDs[i];
    }

    private int getKeyIndex(int keyID) {
        for (int i = 0; i < whiteKeyIDs.length; i++)
            if (whiteKeyIDs[i] == keyID) return i;
        for (int i = 0; i < blackKeyIDs.length; i++)
            if (blackKeyIDs[i] == keyID) return i;
        return -1;
    }
    
    private boolean isWhiteKey(int keyID) {
        for (int i : whiteKeyIDs) {
            if (keyID == i) return true;
        }
        return false;
    }
    
    public void setFirstNoteOnly(boolean firstOnly) {
        firstNoteOnly = firstOnly;
    }
    
    public MusicTeacher getMusicTeacher() {
        return teacher;
    }

    // TODO - Play a melody for the user to repeat
    public void playMelody() {
        setMode(Modes.AUTOPLAY);
        setRepeatMelody(true);
        if (teacher.getMelodySize() == 0)
            teacher.createMelody();
        timer = new Timer(getTempo(tempo), new ActionListener() {
            private int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i == 0)
                    startNote(teacher.getNextNote());
                else if (i < teacher.getMelodySize()) {
                    endNote();
                    if (firstNoteOnly) synth.playNote(teacher.getNextNote());
                    else startNote(teacher.getNextNote());
                }
                else {
                    endNote();
                    timer.stop();
                    setMode(Modes.RECITE);
                }
                i++;
            }
        });
        timer.setInitialDelay(100);
        timer.start();
    }

    private void setMode(Modes newMode) {
        Modes oldMode = mode;
        mode = newMode;
        rPcs.firePropertyChange("mode", oldMode, mode);
        if (mode == Modes.RECITE) {
            teacher.resetMelody();
            addMouseListener(this);
        } else if (mode == Modes.AUTOPLAY){
            removeMouseListener(this);
        }
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        rRm.addPropertyChangeListener(listener);
        rPcs.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        rRm.removePropertyChangeListener(listener);
        rPcs.removePropertyChangeListener(listener);
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

    public void setTempo(int newBpm) {
        tempo = newBpm;
        
    }
    
    // returns the tempo of the auto-play, in milliseconds per note
    private int getTempo(int bpm) {
        return (int) (bpm * 4.167);
    }
}