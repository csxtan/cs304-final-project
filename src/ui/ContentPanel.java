package ui;

import javax.swing.*;
import java.awt.*;

public class ContentPanel extends JPanel {

    ContentPanel() {
        super();
    }

    ContentPanel(LayoutManager lm) {
        super(lm);
    }

    public JLabel addText(String text) {
        JPanel textPanel = new JPanel();
        JLabel label = new JLabel("<html><strong>" + text + "</strong></html>");
        textPanel.add(label);
        this.add(textPanel);
        return label;
    }

    public JTextField addTextField(String label, int columns) {
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel(label + ":"));
        JTextField field = new JTextField(columns);
        inputPanel.add(field);
        this.add(inputPanel);
        return field;
    }
}
