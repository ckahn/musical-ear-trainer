import java.util.*;

public class MusicTeacher {
	
	private ArrayList<Integer> melody = new ArrayList<Integer>();
	private boolean isBadNote = false;
	private ArrayList<Integer> record = new ArrayList<Integer>();
	private int i = 0;
	
	
	public boolean getIsBadNote() {
		return isBadNote;
	}
	
	public void addToRecord(int note) {
		record.add(note);
	}
	
	public void clearRecord() {
		record.clear();
	}
	
	public int getRecordSize() {
		return record.size();
	}
	
	public boolean checkNote(int note) {
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
		melody.add(0);
		for (int i = 0; i < 4; i++) {
			int note = (int) (Math.random()*5);
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
}
