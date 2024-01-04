package io.github.rudynakodach.JavaxElements;

import javax.swing.*;
import java.awt.*;

public class HorizontalComponentContainer extends JPanel {
    public HorizontalComponentContainer() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    public HorizontalComponentContainer(Component ...components) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        for(Component c : components) {
            add(c);
        }
    }
}
