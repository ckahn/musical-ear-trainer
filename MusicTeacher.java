import java.util.*;

public class MusicTeacher {
	
	private ArrayList<Integer> melody = new ArrayList<Integer>();
	private boolean isBadNote = false;
	private int i = 0;
	
	
	public boolean getIsBadNote() {
		return isBadNote;
	}
	
	public boolean isGoodNote(int note) {
		if (i < getMelodySize() && note == getNextNote()) {
			return true;
		} else {
			i = 0;
			return false;
		}
	}

	public int getNextNote() {
		return melody.get(i++);
	}
	
	public void createMelody() {
		int note = 0;
		melody.add(note);
		for (int i = 0; i < 3; i++) {
			while (melody.contains(note)) {
				note = (int) (Math.random()*8);
			}
			melody.add(note);
		}
		return;
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
