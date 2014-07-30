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
        GridBagConstraints mgc = new GridBagConstraints();
        mgc.gridx = 0;
        mgc.gridy = 0;
        mgc.gridwidth = 2;
        mgc.weightx = 0.5;
        mgc.fill = GridBagConstraints.HORIZONTAL;
        mgc.insets = new Insets(5, 5, 5, 5);
        pane.add(melodyGroup, mgc);
        
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0;
        cc.gridy = 1;
        cc.weightx = 0.5;
        cc.anchor = GridBagConstraints.LINE_END;
        cc.insets = new Insets(0, 0, 0, 10);
        pane.add(showBox, cc);
        
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 1;
        bc.gridy = 1;
        cc.weightx = 0.5;
        cc.anchor = GridBagConstraints.LINE_END;
        bc.insets = new Insets(0, 0, 0, 7);
        pane.add(playButton, bc);
        
        keyboard = new Keyboard();
        GridBagConstraints kbc = new GridBagConstraints();
        kbc.gridx = 0;
        kbc.gridy = 2;
        kbc.gridwidth = 2;
        kbc.weightx = 0.5;
        kbc.weighty = 0.5;
        kbc.fill = GridBagConstraints.HORIZONTAL;
        kbc.anchor = GridBagConstraints.PAGE_END;
        kbc.insets = new Insets(7, 7, 7, 7);
        pane.add(keyboard, kbc);

        setSize(pane.getPreferredSize().width, pane.getPreferredSize().height);
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
        } else if (source == tempoSpinner) {
            keyboard.setTempo((int)source.getValue());
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