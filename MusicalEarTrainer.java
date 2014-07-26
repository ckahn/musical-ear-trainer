import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** 
 * An interactive keyboard that you can play with a mouse.
 */
public class MusicalEarTrainer extends JFrame implements ActionListener {

    Keyboard keyboard = new Keyboard();

    JButton playButton = new JButton("Play");

    JLabel lengthLabel = new JLabel("Melody Length: ");
    Integer[] lengthArray = {2, 3, 4, 5, 6, 7, 8, 9};
    JComboBox<Integer> lengthList = new JComboBox<Integer>(lengthArray);

    JLabel keyLabel = new JLabel("(Major) Key: ");
    String[] keyArray = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B", "C3"};
    JComboBox<String> keyList = new JComboBox<String>(keyArray);

    // Initialize window and add keyboard
    private MusicalEarTrainer() {
        setTitle("Interactive Keyboard");
        setSize(45*15+1, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTextArea area = keyboard.area;

        JPanel controlPanel = new JPanel();
        keyList.setSelectedItem(keyArray[0]);
        lengthList.setSelectedItem(2);
        controlPanel.add(keyLabel);
        controlPanel.add(keyList);
        controlPanel.add(lengthLabel);
        controlPanel.add(lengthList);
        controlPanel.add(playButton);
        playButton.addActionListener(this);
        lengthList.addActionListener(this);
        keyList.addActionListener(this);

        getContentPane().add(keyboard, BorderLayout.CENTER);
        keyboard.area.setLineWrap(true);
        getContentPane().add(area, BorderLayout.PAGE_END);
        getContentPane().add(controlPanel, BorderLayout.PAGE_START);
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getSource() == playButton) {
            keyboard.playMelody();
        } else {
            if (e.getSource() == keyList) {
                keyboard.setKey(keyList.getSelectedIndex());
            } else if (e.getSource() == lengthList) {
                keyboard.setMelodyLength((int) lengthList.getSelectedItem());
            }
            keyboard.createMelody();
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