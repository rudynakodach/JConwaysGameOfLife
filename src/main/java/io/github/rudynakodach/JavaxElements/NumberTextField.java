package io.github.rudynakodach.JavaxElements;

import io.github.rudynakodach.Main;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class NumberTextField extends JTextField {
    public NumberTextField() {
        super();

        addActionListener(e -> {
            if(isInputNumeric()) {
                setBorder(new LineBorder(Color.black, 1));
            } else {
                setBorder(new LineBorder(Color.red, 2));
            }
        });
    }

    public Long numericValue() {
        if(isInputNumeric()) {
            return Long.parseLong(getText());
        }
        return null;
    }

    public boolean isInputNumeric() {
        try {
            Long.parseLong(getText());
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}
