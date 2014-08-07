/*
 * A simple class for generating piano-like tones sound using your operating 
 * system's built-in MIDI synthesizer.
 */

import javax.sound.midi.*;

public class MIDISynth {

    private Synthesizer synth;
    private MidiChannel channel;
    private int noteNumber;

    /**
     * Create a new MIDISynth object for generating sound.
     * @throws Exception if the operating system's MIDI synthesizer could 
     * not be accessed.
     */
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

    /**
     * Generates a piano-like sound with the synthesizer.
     * @param keyID the ID of the piano key that was pressed (0 = first key)
     */
    public void playNote(int keyID) {
        noteNumber = keyID + LOW_C_NOTE_NUMBER;
        channel.noteOn(noteNumber, VELOCITY);
    }

    /**
     * Stop playing the current note.
     */
    public void stopNote() {
        channel.noteOff(noteNumber);
    }

    private final int LOW_C_NOTE_NUMBER = 48;
    private final int VELOCITY = 100;
}