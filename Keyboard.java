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
    
    // array of the piano keys
    Key keys[];

    // keyboard position in relation to containing panel
    private Point position; 

    // unique key IDs for white and black keys, in ascending order
    private int[] blackKeyIDs = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
    private int[] whiteKeyIDs = {0, 2, 4, 5, 7, 9, 11, 12, 14, 
            16, 17, 19, 21, 23, 24};

    // MIDI synthesizer
    private SoundGenerator synth;
    
    // timer for auto-playing notes
    private Timer timer;
    
    // make this a bound property so changes to it will affect the 
    // controls in the window
    private Modes mode;
    private PropertyChangeSupport rPcs = new PropertyChangeSupport(this);
    
    // whether to repeat the current melody or create a new one, bound it
    private boolean repeatMelody;
    private PropertyChangeSupport rRm = new PropertyChangeSupport(this);
    
    // whether to show only the first auto-played note, otherwise show all
    private boolean firstNoteOnly;

    // creates melodies and evaluates user's performance
    private MusicTeacher teacher = new MusicTeacher();
    
    // tempo of auto-played melody, in beats per minute
    private int tempo = 160;

    // constructors
    public Keyboard() {
        position = new Point(0, 0);
        initKeyboard();
    }
    
    private void initKeyboard() {
        createKeys();
        setPreferredSize(new Dimension((int)(WHITE_KEY_WIDTH*NUM_WHITE_KEYS+1), 
                (int)(WHITE_KEY_HEIGHT+1)));
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
    
    // add all keys to the array
    private void createKeys() {
        keys = new Key[NUM_WHITE_KEYS + NUM_BLACK_KEYS];
        createWhiteKeys();
        createBlackKeys();
    }

    // add all white keys to the array
    private void createWhiteKeys() {
        double x = position.getX();
        double y = position.getY();
        for (int i : whiteKeyIDs) {
            keys[i] = new Key(x, y, WHITE_KEY_WIDTH, 
                    WHITE_KEY_HEIGHT, Color.WHITE, true);
            x = x + WHITE_KEY_WIDTH;
        }
    }

    // add all black keys to the array
    private void createBlackKeys() {
        double x = position.getX() + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2;
        double y = position.getY();
        for (int i : blackKeyIDs) {
            keys[i] = new Key(x, y, BLACK_KEY_WIDTH, 
                    BLACK_KEY_HEIGHT, Color.BLACK, false);
            x = x + WHITE_KEY_WIDTH;

            // Skip over for adjacent white notes
            if (i == 3 || i == 10 || i == 15) {x = x + WHITE_KEY_WIDTH; }
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
        for (int i : whiteKeyIDs) {
            g2.setColor(keys[i].getColor());
            g2.fill(keys[i]);
            g2.setColor(Color.black);
            g2.draw(keys[i]);
        }

        // Draw the black keys
        for (int i : blackKeyIDs) {
            g2.setColor(keys[i].getColor());
            g2.fill(keys[i]);
            g2.setColor(Color.black);
            g2.draw(keys[i]);
        }
    }

    public void mousePressed(MouseEvent e) {
        
        // Get point location
        Point p = new Point(e.getX(), e.getY());       
        
        // Check black keys first since they are on top of the white keys
        for (int i : blackKeyIDs) {
            if (keys[i].contains(p)) {
                playNote(i);
                return;
            }
        }
        
        for (int i : whiteKeyIDs) {
            if (keys[i].contains(p)) {
                playNote(i);
                return;
            }
        }
    }

    private void playNote(int keyID) {
        if (mode == Modes.RECITE && !teacher.isGoodNote(keyID)) {
            keys[keyID].setColor(Color.red);
            teacher.resetMelody();
        } else {
            keys[keyID].setColor(Color.LIGHT_GRAY);
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
        for (Key key : keys) {
            key.resetColor();
        }
        repaint();
    }
    
    public void setRepeatMelody(boolean repeat) {
        boolean oldRepeat = repeatMelody;
        teacher.resetMelody();
        repeatMelody = repeat;
        rRm.firePropertyChange("repeatMelody", oldRepeat, repeatMelody);
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
                    playNote(teacher.getNextNote());
                else if (i < teacher.getMelodySize()) {
                    endNote();
                    if (firstNoteOnly) synth.playNote(teacher.getNextNote());
                    else playNote(teacher.getNextNote());
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

    public void setTempo(int newBpm) {
        tempo = newBpm;     
    }
    
    // returns the tempo of the auto-play, in milliseconds per note
    private int getTempo(int bpm) {
        return (int)(1/(bpm/60.0)*1000);
    }
    
    private class Key extends Rectangle2D.Double {
        Color color = Color.WHITE;
        boolean isNatural = true;
        
        public Key(double x, double y, double width, double height,
                Color color, boolean isNatural) {
            super(x, y, width, height);
            this.color = color;
            this.isNatural = isNatural;
        }
        
        public void setColor(Color color) {
            this.color = color;
        }
        
        public Color getColor() {
            return color;
        }
        
        public void resetColor() {
            if (isNatural) {
                color = Color.WHITE;
            } else {
                color = Color.BLACK;
            }
        }
    }
    
    private final int NUM_WHITE_KEYS = 15;
    private final double WHITE_KEY_WIDTH = 45;
    private final double WHITE_KEY_HEIGHT = 200;

    private final int NUM_BLACK_KEYS = 10;
    private final double BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT * 0.6;
    private final double BLACK_KEY_WIDTH = WHITE_KEY_WIDTH * 0.5;
}