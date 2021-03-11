package com.Swing;

import Helper.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test extends JFrame {

    private static final String registerQuery = "INSERT INTO Persons (email, password) VALUES (?, ?)";
    private static final String loginQuery = "SELECT PersonID FROM Persons WHERE email LIKE ? AND password LIKE ?";



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
                try {

                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(loginQuery);
                    preparedStatement.setString(1, textField1.getText());
                    preparedStatement.setString(2, passwordField1.getText());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) showWorldPane();
                }catch (SQLException i){
                    i.printStackTrace();
                }
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(registerQuery);
                    preparedStatement.setString(1, textField1.getText());
                    preparedStatement.setString(2, passwordField1.getText());
                    preparedStatement.executeUpdate();
                }catch (SQLException i){
                    i.printStackTrace();
                }
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
