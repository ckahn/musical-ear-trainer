/*
 * A program that helps you identify intervals on a piano.
 */

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class MusicalEarTrainer extends JFrame implements ActionListener, ItemListener, ChangeListener {

    Keyboard keyboard = new Keyboard();

    JButton playButton = new JButton();

    JLabel keyLabel = new JLabel("Key: ");
    String[] keyArray = {"Low C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "Mid C"};
    JComboBox<String> keyList = new JComboBox<String>(keyArray);
    
    JLabel tonalityLabel = new JLabel("Scale: ");
    String[] tonalityArray = {"Major", "Minor", "Chromatic"};
    JComboBox<String> tonalityList = new JComboBox<String>(tonalityArray);
    
    JLabel lengthLabel = new JLabel("Length: ");
    SpinnerModel lengthModel = new SpinnerNumberModel(3, 2, 20, 1);
    JSpinner lengthSpinner = new JSpinner(lengthModel);
    
    JLabel tempoLabel = new JLabel("Tempo: ");
    SpinnerModel tempoModel = new SpinnerNumberModel(120, 10, 300, 1);
    JSpinner tempoSpinner = new JSpinner(tempoModel);

    JCheckBox showBox = new JCheckBox("Only show first note");

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Musical Ear Trainer");
        setSize(45*15+1, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel melodyProperties = new JPanel();
        melodyProperties.setBorder(BorderFactory.createTitledBorder("Melody Properties"));
        keyList.setSelectedItem(keyArray[0]);
        melodyProperties.add(keyLabel);
        melodyProperties.add(keyList);
        melodyProperties.add(tonalityLabel);
        melodyProperties.add(tonalityList);
        melodyProperties.add(lengthLabel);
        melodyProperties.add(lengthSpinner);
        melodyProperties.add(tempoLabel);
        melodyProperties.add(tempoSpinner);
        
        JPanel playOptions = new JPanel((new FlowLayout(FlowLayout.RIGHT)));
        playOptions.add(showBox);
        playOptions.add(playButton);
        
        topPanel.add(melodyProperties);
        topPanel.add(playOptions);
        
        playButton.setText(PLAY_NEW);
        playButton.setMnemonic(KeyEvent.VK_P);
        playButton.addActionListener(this);
        keyList.addActionListener(this);
        tonalityList.addActionListener(this);
        tempoSpinner.addChangeListener(this);
        lengthSpinner.addChangeListener(this);
        
        showBox.setSelected(false);
        keyboard.setFirstNoteOnly(false);
        showBox.addItemListener(this);

        getContentPane().add(topPanel, BorderLayout.PAGE_START);
        getContentPane().add(keyboard, BorderLayout.CENTER);
        Dimension dim = new Dimension (5, 0);
        getContentPane().add(new JPanel().add(new Box.Filler(dim, dim, dim)),BorderLayout.PAGE_END);
        getContentPane().add(new JPanel().add(new Box.Filler(dim, dim, dim)),BorderLayout.LINE_START);
        getContentPane().add(new JPanel().add(new Box.Filler(dim, dim, dim)),BorderLayout.LINE_END);

        keyboard.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getNewValue() == Modes.AUTOPLAY) {
                    setEnabledAllListeners(false);
                } else if (e.getNewValue() == Modes.RECITE) {
                    setEnabledAllListeners(true);
                } else if (e.getNewValue().equals(false)) {
                    playButton.setText(PLAY_NEW);
                } else if (e.getNewValue().equals(true)) {
                    playButton.setText("Repeat");
                }
            }
        });
    }
    
    private void setEnabledAllListeners(boolean isEnabled) {
        tonalityList.setEnabled(isEnabled);
        keyList.setEnabled(isEnabled);
        playButton.setEnabled(isEnabled);
        showBox.setEnabled(isEnabled);
        tempoSpinner.setEnabled(isEnabled);
        lengthSpinner.setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == playButton) {
            keyboard.playMelody();
        } else {
            if (e.getSource() == keyList) {
                keyboard.getMusicTeacher().setKey(keyList.getSelectedIndex());
            } else if (e.getSource() == tonalityList) {
                keyboard.getMusicTeacher().setTonality(tonalityList.getSelectedIndex());
            }
            playButton.setText(PLAY_NEW);
            keyboard.getMusicTeacher().createMelody();
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            keyboard.setFirstNoteOnly(true);
        } else {
            keyboard.setFirstNoteOnly(false);
        }
    }
    

    // Listen to the spinners
    @Override
    public void stateChanged(ChangeEvent e) {
        JSpinner source = (JSpinner)e.getSource();
        if (source == lengthSpinner) {
            keyboard.getMusicTeacher().setMelodyLength((int)source.getValue());
            System.out.println("Length = " + (int)source.getValue());
        } else if (source == tempoSpinner) {
            keyboard.setTempo((int)source.getValue());
            System.out.println("Tempo = " + (int)source.getValue());
        }
        playButton.setText(PLAY_NEW);
        keyboard.getMusicTeacher().createMelody();
    }
    
    // Create and show application window
    public static void main(String[] args) {

        // Code is run on an event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MusicalEarTrainer window = new MusicalEarTrainer();
                window.setVisible(true);
            }
        });   
    }
    
    private final String PLAY_NEW = "Play New";

}