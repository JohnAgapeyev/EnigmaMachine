package machine.enigma;

import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PlugBoardPanel extends JPanel {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz"
            .toUpperCase().toCharArray();

    private FontMetrics metrics;

    private JTextField[] plugs = new JTextField[26];
    private JLabel[] plugLabels = new JLabel[26];

    /**
     * Serial ID for the Panel.
     */
    private static final long serialVersionUID = -1785742581932840671L;

    public PlugBoardPanel() {
//        setLayout(null);

        for (int i = 0; i < plugs.length; i++) {
            plugs[i] = new JTextField();
            plugLabels[i] = new JLabel(String.valueOf(ALPHABET[i]) + ":");
        }

        metrics = plugs[0].getFontMetrics(plugs[0].getFont());
        int fontHeight = metrics.getHeight();
        int fontWidth = metrics.getMaxAdvance();

        plugs[0].setBounds(10, 10, fontWidth, fontHeight);

        for (JTextField plug : plugs) {
            add(plug);
        }
        
        add(new JTextArea(10, 5));

    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
