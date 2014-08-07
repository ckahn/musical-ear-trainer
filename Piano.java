/*
 * This class contains a virtual piano that is connected to a MIDISynth object 
 * to generate sound. The piano plays itself when appropriate using the 
 * melody created by the MelodyMaker object. The piano also evaluates what 
 * the user plays back when appropriate, again using MelodyMaker. 
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.beans.*;
import javax.swing.*;

@SuppressWarnings("serial")
class Piano extends JPanel implements MouseListener {

    // array of the piano keys
    Key keys[];

    // keyboard position in relation to containing panel
    private Point position; 

    // unique key IDs for white and black keys, in ascending order
    private int[] sharps = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22};
    private int[] naturals = {0, 2, 4, 5, 7, 9, 11, 12, 14, 
            16, 17, 19, 21, 23, 24};

    // MIDI synthesizer
    private MIDISynth synth;

    // timer for auto-playing notes
    private Timer timer;

    // whether to auto-play, evaluate user input, etc. (bound)
    private Modes mode;
    private PropertyChangeSupport rPcs = new PropertyChangeSupport(this);

    // whether to repeat the current melody or create a new one
    private boolean repeatMelody;

    // whether to show only the first auto-played note, otherwise show all
    private boolean firstNoteOnly;

    // creates melodies and compares them to user input
    private MelodyMaker melodyMaker = new MelodyMaker();

    // tempo of auto-played melody, in beats per minute
    private int tempo = 160;

    // constructors
    public Piano() {
        position = new Point(0, 0);
        initKeyboard();
    }

    private void initKeyboard() {
        createKeys();
        try {
            synth = new MIDISynth();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not access your computer's MIDI synthesizer.");
            System.exit(-1);
        }
        setMode(Modes.IDLE);
        repeatMelody = false;
        addMouseListener(this);
        setPreferredSize(new Dimension((int)(WHITE_KEY_WIDTH*NUM_WHITE_KEYS+1), 
                (int)(WHITE_KEY_HEIGHT+1)));
    }

    // add all keys to the array
    private void createKeys() {
        keys = new Key[NUM_WHITE_KEYS + NUM_BLACK_KEYS];
        addNaturalKeys();
        addSharpKeys();
    }
    
    // add all natural (white) keys to the array
    private void addNaturalKeys() {
        double x = position.getX();
        double y = position.getY();
        for (int i : naturals) {
            keys[i] = new Key(x, y, WHITE_KEY_WIDTH, 
                    WHITE_KEY_HEIGHT, Color.WHITE, true);
            x = x + WHITE_KEY_WIDTH;
        }
    }

    // add all sharp (black) keys to the array
    private void addSharpKeys() {
        double x = position.getX() + WHITE_KEY_WIDTH - BLACK_KEY_WIDTH/2;
        double y = position.getY();
        
        for (int i = 0; i < sharps.length; i++) {
            keys[sharps[i]] = new Key(x, y, BLACK_KEY_WIDTH, 
                    BLACK_KEY_HEIGHT, Color.BLACK, false);
            x = x + WHITE_KEY_WIDTH;

            // skip over for adjacent white notes
            if (sharps[i] == 3 || sharps[i] == 10 || sharps[i] == 15)
                x = x + WHITE_KEY_WIDTH;
        }
    }

    // paint the JPanel
    @Override
    public void paintComponent(Graphics g) {
        // cast g to Graphics2D to access RenderingHints, drawing objects, etc.
        Graphics2D g2 = (Graphics2D) g;

        // change rendering so rectangle corners are clean
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHints(rh);

        // draw the white keys
        for (int i : naturals) {
            g2.setColor(keys[i].getColor());
            g2.fill(keys[i]);
            g2.setColor(Color.black);
            g2.draw(keys[i]);
        }

        // draw the black keys
        for (int i : sharps) {
            g2.setColor(keys[i].getColor());
            g2.fill(keys[i]);
            g2.setColor(Color.black);
            g2.draw(keys[i]);
        }
    }

    public void mousePressed(MouseEvent e) {
        Point p = new Point(e.getX(), e.getY());       

        // check black keys first since they are on top of the white keys
        for (int i : sharps) {
            if (keys[i].contains(p)) {
                playNote(i);
                return;
            }
        }
        for (int i : naturals) {
            if (keys[i].contains(p)) {
                playNote(i);
                return;
            }
        }
    }

    // play the note on the piano, color the key as appropriate
    private void playNote(int keyID) {
        if (mode == Modes.RECITE && !melodyMaker.isGoodNote(keyID)) {
            keys[keyID].setColor(Color.RED);
            melodyMaker.restartMelody();
        } else {
            keys[keyID].setColor(Color.LIGHT_GRAY);
        }
        synth.playNote(keyID);
        if (mode == Modes.RECITE && melodyMaker.isLastNote())
            keys[keyID].setColor(Color.GREEN);
        repaint();
    }

    // clear information about which key is pressed and reset keyboard
    public void mouseReleased(MouseEvent e) {
        endNote();
        if (mode == Modes.RECITE && melodyMaker.isLastNote()) {
            melodyMaker.clearMelody();
            setMode(Modes.IDLE);
        }
    }

    // stop playing the current note
    private void endNote() {
        synth.stopNote();
        for (Key key : keys) {
            key.resetColor();
        }
        repaint();
    }

    /**
     * Sets whether the first note only will be shown during an automatically 
     * played melody, or whether all notes will be shown.
     * @param firstOnly whether only the first note will be shown
     */
    public void setFirstNoteOnly(boolean firstOnly) {
        firstNoteOnly = firstOnly;
    }

    /**
     * Gets this object's MelodyMaker.
     * @return this object's MelodyMaker
     */
    public MelodyMaker getMelodyMaker() {
        return melodyMaker;
    }

    /**
     * Initiates the process for automatically playing a melody. This method 
     * changes this object's state to Modes.AUTOPLAY while the melody is 
     * playing, and the state changes to Modes.RECITE once it is complete.
     */
    public void playMelody() {
        if (!repeatMelody) 
            melodyMaker.createMelody();
        setMode(Modes.AUTOPLAY);
        timer = new Timer(getTempo(tempo), new ActionListener() {
            private int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (i == 0)
                    playNote(melodyMaker.getNextNote());
                else if (i < melodyMaker.getMelodySize()) {
                    endNote();
                    if (firstNoteOnly) synth.playNote(melodyMaker.getNextNote());
                    else playNote(melodyMaker.getNextNote());
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

    /**
     * Stops the process for automatically playing a melody.
     */
    public void stopMelody() {
        endNote();
        timer.stop();
        setMode(Modes.RECITE);
    }

    // set the mode for this object; let any registered listeners know
    private void setMode(Modes newMode) {
        Modes oldMode = mode;
        mode = newMode;
        rPcs.firePropertyChange("mode", oldMode, mode);
        if (mode == Modes.RECITE) {
            melodyMaker.restartMelody();
            addMouseListener(this);
        } else if (mode == Modes.AUTOPLAY){
            removeMouseListener(this);
            setRepeatMelody(true);
        } else if (mode == Modes.IDLE){
            setRepeatMelody(false);
        }
    }

    /**
     * Gets the current mode of this object (AUTOPLAY, etc.)
     * @return the current mode of this object
     */
    public Modes getMode() {
        return mode;
    }

    /**
     * Sets whether the current melody should be repeated, or a new melody 
     * should be created.
     * @param repeat whether the current melody should be repeated
     */
    public void setRepeatMelody(boolean repeat) {
        melodyMaker.restartMelody();
        repeatMelody = repeat;
    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        rPcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
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

    // each instance of this class represents a key in the piano
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
    private final double WHITE_KEY_HEIGHT = 175;

    private final int NUM_BLACK_KEYS = 10;
    private final double BLACK_KEY_HEIGHT = WHITE_KEY_HEIGHT * 0.6;
    private final double BLACK_KEY_WIDTH = WHITE_KEY_WIDTH * 0.55;
}