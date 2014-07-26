import javax.sound.midi.*;

/**
 * A simple class for generating sound.
 */
public class SoundGenerator {
	
	private Synthesizer synth;
	private MidiChannel channel;
	private int noteNumber;
	
	// Connect to the computer's MIDI synthesizer.
	public SoundGenerator() {
		try {
			// Get synthesizer and open it
			synth = MidiSystem.getSynthesizer();
			synth.open();
			
			// Get array of channels
			MidiChannel channels[] = synth.getChannels();

			// Use first available channel
			for (MidiChannel c : channels) {
				if (c != null) {	
					channel = c;
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			e.getStackTrace();
			return;
		}
	}
	
	public void playNote(int keyID) {
	    noteNumber = keyID + LOW_C_NOTE_NUMBER;
		channel.noteOn(noteNumber, VELOCITY);
	}
	
	public void stopNote() {
		channel.noteOff(noteNumber);
	}
	
	private final int LOW_C_NOTE_NUMBER = 48;
	private final int VELOCITY = 100;
}