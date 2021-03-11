package com.Swing;

import javax.swing.*;

public class Card extends JFrame{
    static JFrame frame;
    private JPanel panelCard;

    public JFrame getFrame(){
        return this.frame;
    }
    public static void main(String[] args) {
        frame = new JFrame("DSCS");
        App app = new App();
        frame.setContentPane(app.getPanelMain());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

