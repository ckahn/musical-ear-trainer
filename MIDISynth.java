import javax.sound.midi.*;

/**
 * A simple class for generating sound.
 */
public class MIDISynth {
	
	private Synthesizer synth;
	private MidiChannel channel;
	private int noteNumber;
	
	// Connect to the computer's MIDI synthesizer.
	public MIDISynth() throws Exception {
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
			throw new Exception();
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