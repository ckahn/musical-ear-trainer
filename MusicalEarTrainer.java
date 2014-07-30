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

    Keyboard keyboard;

    JButton playButton = new JButton("Play New");

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
    SpinnerModel tempoModel = new SpinnerNumberModel(160, 60, 480, 1);
    JSpinner tempoSpinner = new JSpinner(tempoModel);

    JCheckBox showBox = new JCheckBox("Only show first note");

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Musical Ear Trainer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel pane = new JPanel(new GridBagLayout());
        
        // create group box and add components
        JPanel melodyGroup = new JPanel();
        melodyGroup.setBorder(BorderFactory.createTitledBorder("Melody Properties"));
        Dimension dim = new Dimension(15, 0);
        melodyGroup.add(keyLabel);
        melodyGroup.add(keyList);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(tonalityLabel);
        melodyGroup.add(tonalityList);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(lengthLabel);
        melodyGroup.add(lengthSpinner);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(tempoLabel);
        melodyGroup.add(tempoSpinner);

        // add group box to pane
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        pane.add(melodyGroup, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 10);
        pane.add(showBox, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 7);
        pane.add(playButton, c);
        
        keyboard = new Keyboard();
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_END;
        c.insets = new Insets(7, 7, 7, 7);
        pane.add(keyboard, c);

        setResizable(false);
        getContentPane().add(pane);
        
        keyList.setSelectedItem(keyArray[0]);
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
            keyboard.setRepeatMelody(false);
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
        } else if (source == tempoSpinner) {
            keyboard.setTempo((int)source.getValue());
        }
        keyboard.setRepeatMelody(false);
        keyboard.getMusicTeacher().createMelody();
    }
    
    // Create and show application window
    public static void main(String[] args) {

        // Code is run on an event-dispatching thread
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                MusicalEarTrainer window = new MusicalEarTrainer();
                window.pack();
                window.setVisible(true);
            }
        });   
    }
    
    private final String PLAY_NEW = "Play New";
}