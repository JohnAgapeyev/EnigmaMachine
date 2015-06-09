package machine.enigma;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainPanel extends JPanel {

    /**
     * Serial ID for the panel.
     */
    private static final long serialVersionUID = -8540885588003345092L;

    public MainPanel() throws IOException {
        setLayout(new BorderLayout());
        EnigmaPanel topPanel = new EnigmaPanel();
        PlugBoardPanel bottomPanel = new PlugBoardPanel();
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}
