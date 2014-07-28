import java.util.ArrayList;

class MusicTeacher {

    private ArrayList<Integer> melody = new ArrayList<Integer>();
    private int[] scalePattern;
    private boolean isBadNote = false;
    private int i = 0;
    private int melodyLength = 2;
    private int firstNote = 0;

    public MusicTeacher() {
        setTonality(MAJOR);
    }

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

    public void setTonality(int tonality) {
        int[] majorScale = {0, 2, 4, 5, 7, 9, 11, 12};
        int[] minorScale = {0, 2, 3, 5, 7, 8, 10, 12};

        if (tonality == MAJOR) {
            scalePattern = majorScale;
        } else if (tonality == MINOR) {
            scalePattern = minorScale;
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

    private final int MAJOR = 0;
    private final int MINOR = 1;
}