package com.Swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JFrame {
    public final String LOGIN_PAGE = "login page";
    public final String GROUPS_PAGE= "groups page";
    private final CardLayout cLayout;
    private JPanel mainPane;
    private JButton SWITCHButton;
    private JPanel loginPane;
    private JPanel WorldPane;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton loginButton;
    private JButton registerButton;

    public Test(){
        setTitle("Card Layout Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cLayout = new CardLayout();
        mainPane.setLayout(cLayout);

        mainPane.add(LOGIN_PAGE, loginPane);
        mainPane.add(GROUPS_PAGE,WorldPane);
        showLoginPane();


        

        setLayout(new BorderLayout());
        add(mainPane,BorderLayout.CENTER);
        pack();
        setVisible(true);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWorldPane();
            }
        });
    }

    void showLoginPane(){
        cLayout.show(mainPane,LOGIN_PAGE);
    }
    void showWorldPane(){
        cLayout.show(mainPane,GROUPS_PAGE);
    }

    public static void main(String[] args) {
        new Test();
    }
}
