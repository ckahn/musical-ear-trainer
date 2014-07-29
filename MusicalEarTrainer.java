/*
 * A program that helps you identify intervals on a piano.
 */

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class MusicalEarTrainer extends JFrame implements ActionListener, ItemListener {

    Keyboard keyboard = new Keyboard();

    JButton playButton = new JButton();

    JLabel keyLabel = new JLabel("Key: ");
    String[] keyArray = {"Low C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "Mid C"};
    JComboBox<String> keyList = new JComboBox<String>(keyArray);
    
    JLabel tonalityLabel = new JLabel("Scale: ");
    String[] tonalityArray = {"Major", "Minor", "Chromatic"};
    JComboBox<String> tonalityList = new JComboBox<String>(tonalityArray);
    
    JLabel lengthLabel = new JLabel("Length: ");
    Integer[] lengthArray = {2, 3, 4, 5, 6, 7, 8, 9, 10};
    JComboBox<Integer> lengthList = new JComboBox<Integer>(lengthArray);

    JCheckBox showBox = new JCheckBox("Only show first note");

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Musical Ear Trainer");
        setSize(45*15+1, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        keyList.setSelectedItem(keyArray[0]);
        lengthList.setSelectedItem(3);
        controlPanel.add(keyLabel);
        controlPanel.add(keyList);
        controlPanel.add(tonalityLabel);
        controlPanel.add(tonalityList);
        controlPanel.add(lengthLabel);
        controlPanel.add(lengthList);
        controlPanel.add(showBox);
        controlPanel.add(playButton);
        
        playButton.setText(PLAY_NEW);
        playButton.setMnemonic(KeyEvent.VK_P);
        playButton.addActionListener(this);
        lengthList.addActionListener(this);
        keyList.addActionListener(this);
        tonalityList.addActionListener(this);
        
        showBox.setSelected(false);
        keyboard.setFirstNoteOnly(false);
        showBox.addItemListener(this);

        getContentPane().add(keyboard, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.PAGE_START);

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
        lengthList.setEnabled(isEnabled);
        playButton.setEnabled(isEnabled);
        showBox.setEnabled(isEnabled);
    }

    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == playButton) {
            keyboard.playMelody();
        } else {
            if (e.getSource() == keyList) {
                keyboard.getMusicTeacher().setKey(keyList.getSelectedIndex());
            } else if (e.getSource() == lengthList) {
                keyboard.getMusicTeacher().setMelodyLength((int) lengthList.getSelectedItem());
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