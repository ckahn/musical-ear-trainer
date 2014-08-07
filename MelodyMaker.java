/*
 * This class creates melodies and iterates through them.
 * The Piano class uses this class to generate melodies,
 * to know what melody to play, and to determine whether
 * a user is correctly repeating a melody.
 */
import java.util.ArrayList;

public class MelodyMaker {

    private ArrayList<Integer> melody = new ArrayList<Integer>();
    private int[] scalePattern;
    private int i = 0;
    private int melodyLength;
    private int firstNote;

    // set initial values
    // these must match the initial control settings in the window!
    public MelodyMaker() {
        setTonality(MAJOR);
        setLength(3);
        setKey(0);
    }

    /**
     * Returns true if the pressed key matches the expected key for the 
     * automatically played melody and moves melody forward.
     * @param keyID the index of the pressed key (0 = first key)
     * @return whether the pressed key matches the expected key
     */
    public boolean isGoodNote(int keyID) {
        if (i < melody.size() && keyID == getNextNote()) {
            return true;
        } else {
            i = 0;
            return false;
        }
    }

    /**
     * Returns the number of notes in the current melody.
     * @return the number of notes in the current melody
     */
    public int getMelodySize() {
        return melody.size();
    }

    /**
     * Returns the next note in the current melody and moves the melody forward.
     * @return the next note in the current melody and moves the melody forward
     */
    public int getNextNote() {
        return melody.get(i++);
    }

    /**
     * Sets the tonality of the melody's scale.
     * @param tonality the tonality of the melody
     */
    public void setTonality(int tonality) {
        int[] majorScale = {0, 2, 4, 5, 7, 9, 11, 12};
        int[] minorScale = {0, 2, 3, 5, 7, 8, 10, 12};
        int[] chromaticScale = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};

        if (tonality == MAJOR) {
            scalePattern = majorScale;
        } else if (tonality == MINOR) {
            scalePattern = minorScale;
        } else if (tonality == CHROMATIC) {
            scalePattern = chromaticScale;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Sets the key (e.g., C) for the melody's scale.
     * @param key the key of the melody
     */
    public void setKey(int key) {
        firstNote = key;
    }

    /**
     * Sets the length of the melody (i.e., number of notes).
     * @param len the length of the melody
     */
    public void setLength(int len) {
        melodyLength = len;
    }

    /**
     * Creates the melody that the piano will automatically play.
     */
    public void createMelody() {
        clearMelody();
        melody.add(firstNote);
        for (int i = 0; i < melodyLength-1; i++) {
            while (true) {
                int next = scalePattern[(int) (Math.random()*scalePattern.length)] + firstNote;
                if (next != melody.get(i)) {
                    melody.add(next);
                    break;
                }
            }
        }
    }

    /**
     * Set the melody to its first note.
     */
    public void restartMelody() {
        i = 0;
    }

    /**
     * Returns true if the melody is on its last note.
     * @return true if the melody is on its late note
     */
    public boolean isLastNote() {
        if (i == melody.size()) {
            return true;
        } else return false;
    }

    /**
     * Delete the melody.
     */
    public void clearMelody() {
        restartMelody();
        melody.clear();
    }

    private final int MAJOR = 0;
    private final int MINOR = 1;
    private final int CHROMATIC = 2;
}