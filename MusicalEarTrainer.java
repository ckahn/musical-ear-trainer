import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class MusicalEarTrainer extends JFrame implements ActionListener, 
ItemListener, ChangeListener, PropertyChangeListener {

    JCheckBox showBox;
    JButton playButton;
    Piano keyboard;
    String[] keyArray = {"Low C", "C#", "D", "D#", "E", "F", "F#", 
            "G", "G#", "A", "A#", "B", "Mid C"};
    JComboBox<String> keyList = new JComboBox<String>(keyArray);    
    String[] tonalityArray = {"Major", "Minor", "Chromatic"};
    JComboBox<String> tonalityList = new JComboBox<String>(tonalityArray);   
    JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(3, 2, 20, 1));
    JSpinner tempoSpinner = new JSpinner(new SpinnerNumberModel(160, 60, 480, 1));  

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Musical Ear Trainer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel pane = new JPanel(new GridBagLayout());

        // create group box and add components
        JPanel melodyGroup = new JPanel();
        melodyGroup.setBorder(BorderFactory.createTitledBorder("Melody Properties"));
        keyList.setSelectedItem(keyArray[0]);
        melodyGroup.add(new JLabel("Key: "));
        melodyGroup.add(keyList);
        Dimension dim = new Dimension(15, 0);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(new JLabel("Scale: "));
        melodyGroup.add(tonalityList);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(new JLabel("Length: "));
        melodyGroup.add(lengthSpinner);
        melodyGroup.add(new Box.Filler(dim, dim, dim));
        melodyGroup.add(new JLabel("Tempo (bpm): "));
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

        // add check box
        showBox = new JCheckBox("Show first note only");
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 10);
        showBox.setSelected(false);
        pane.add(showBox, c);

        // add button
        playButton = new JButton("Play New");
        playButton.setMnemonic(KeyEvent.VK_P);
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 0, 7);
        pane.add(playButton, c);

        // add piano
        keyboard = new Piano();
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

        keyboard.addPropertyChangeListener(this);
        playButton.addActionListener(this);
        keyList.addActionListener(this);
        tonalityList.addActionListener(this);
        tempoSpinner.addChangeListener(this);
        lengthSpinner.addChangeListener(this);
        showBox.addItemListener(this);

        getContentPane().add(pane);
    }


    private void setEnabledControls(boolean isEnabled) {
        tonalityList.setEnabled(isEnabled);
        keyList.setEnabled(isEnabled);
        showBox.setEnabled(isEnabled);
        tempoSpinner.setEnabled(isEnabled);
        lengthSpinner.setEnabled(isEnabled);
    }

    // listen to controls in group box
    @Override
    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == playButton) {
            if (keyboard.getMode() == Modes.AUTOPLAY ) {
                keyboard.stopMelody();
            } else {
                keyboard.playMelody();
            }
        } else {
            if (e.getSource() == keyList)
                keyboard.getMusicTeacher().setKey(keyList.getSelectedIndex());
            else if (e.getSource() == tonalityList) 
                keyboard.getMusicTeacher().setTonality(tonalityList.getSelectedIndex());
            keyboard.setRepeatMelody(false);
            keyboard.getMusicTeacher().createMelody();
            playButton.setText("Play New");
        }
    }

    // listen to check box
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            keyboard.setFirstNoteOnly(true);
        } else {
            keyboard.setFirstNoteOnly(false);
        }
    }   

    // listen to the spinners
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
        playButton.setText("Play New");
    }

    // listen for property changes
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getNewValue() == Modes.AUTOPLAY) {
            setEnabledControls(false);
            playButton.setText("Stop");
        } else if (e.getNewValue() == Modes.RECITE) {
            setEnabledControls(true);
            playButton.setText("Repeat");
        } else {
            setEnabledControls(true);
            playButton.setText("Play New");
        }
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
}