import java.util.ArrayList;

public class MelodyMaker {

    private ArrayList<Integer> melody = new ArrayList<Integer>();
    private int[] scalePattern;
    private int i = 0;
    private int melodyLength = 3;
    private int firstNote = 0;
    
    public MelodyMaker() {
        setTonality(MAJOR);
    }

    public boolean isGoodNote(int keyID) {
        if (i < melody.size() && keyID == getNextNote()) {
            return true;
        } else {
            i = 0;
            return false;
        }
    }

    public int getMelodySize() {
        return melody.size();
    }

    public int getNextNote() {
        return melody.get(i++);
    }

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

    public void setKey(int key) {
        firstNote = key;
    }

    public void setMelodyLength(int len) {
        melodyLength = len;
    }

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

    public void resetMelody() {
        i = 0;
    }

    public boolean isLastNote() {
        if (i == melody.size()) {
            return true;
        } else return false;
    }

    public void clearMelody() {
        melody.clear();
    }

    private final int MAJOR = 0;
    private final int MINOR = 1;
    private final int CHROMATIC = 2;
}