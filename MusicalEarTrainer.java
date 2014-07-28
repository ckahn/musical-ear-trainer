import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/** 
 * An interactive keyboard that you can play with a mouse.
 */
@SuppressWarnings("serial")
public class MusicalEarTrainer extends JFrame implements ActionListener, ItemListener {

    Keyboard keyboard = new Keyboard();

    JButton playButton = new JButton("Play");

    JLabel keyLabel = new JLabel("Key: ");
    String[] keyArray = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C3"};
    JComboBox<String> keyList = new JComboBox<String>(keyArray);
    
    JLabel lengthLabel = new JLabel("Melody Length: ");
    Integer[] lengthArray = {2, 3, 4, 5, 6, 7, 8, 9};
    JComboBox<Integer> lengthList = new JComboBox<Integer>(lengthArray);
    
    JLabel tonalityLabel = new JLabel("Tonality: ");
    String[] tonalityArray = {"Major", "Minor"};
    JComboBox<String> tonalityList = new JComboBox<String>(tonalityArray);

    JCheckBox showBox = new JCheckBox("Only show first note");

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Musical Ear Trainer");
        setSize(45*15+1, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel controlPanel = new JPanel();
        keyList.setSelectedItem(keyArray[0]);
        lengthList.setSelectedItem(2);
        controlPanel.add(tonalityLabel);
        controlPanel.add(tonalityList);
        controlPanel.add(keyLabel);
        controlPanel.add(keyList);
        controlPanel.add(lengthLabel);
        controlPanel.add(lengthList);
        controlPanel.add(showBox);
        controlPanel.add(playButton);
        
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
    }

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
            keyboard.getMusicTeacher().createMelody();
        }
    }

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
}