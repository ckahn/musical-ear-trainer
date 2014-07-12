import javax.sound.midi.*;

/**
 * A simple class for generating sound.
 */
public class SoundGenerator {
	
	private Synthesizer synth;
	private MidiChannel channel;
	private int note;
	
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
				System.out.print("c = " + c + " ");
				if (c != null) {	
					channel = c;
					System.out.println("Channel assigned!"); // TEST
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			System.err.println(e.getStackTrace());
			return;
		}
	}
	
	public void playNote(int n) {
		note = n;
		channel.noteOn(note, VELOCITY);
	}
	
	public void stopNote() {
		channel.noteOff(note);
	}
	
	private final int VELOCITY = 100;
}