package ui;

import javax.swing.*;
import java.awt.*;

public class Screen extends JPanel {
    ContentPanel contentPanel;
    private static final int PANELHEIGHT = 15;

    Screen() {
        super(new BorderLayout());
        contentPanel = new ContentPanel(new GridLayout(PANELHEIGHT, 1));
        this.add(contentPanel, BorderLayout.NORTH);
    }
}
