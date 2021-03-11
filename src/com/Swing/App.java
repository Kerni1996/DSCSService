package com.Swing;

import Helper.DatabaseConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class App {
    private static final String registerQuery = "INSERT INTO Persons (email, password) VALUES (?, ?)";
    private static final String loginQuery = "SELECT PersonID FROM Persons WHERE email LIKE ? AND password LIKE ?";

    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton registerButton;
    private JButton loginButton;
    private JPanel panelMain;


    public JPanel getPanelMain(){
        return this.panelMain;
    }

    public App() {

        //register button clicked
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
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu menu = new Menu();
                Card card = new Card();
                //card.getFrame().setContentPane(menu.getPanelMenu());
                card.getFrame().pack();
                System.out.println("LOLOL");
                try {

                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(loginQuery);
                    preparedStatement.setString(1, textField1.getText());
                    preparedStatement.setString(2, passwordField1.getText());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    //if (resultSet.next()) ;//open new JPane
                }catch (SQLException i){
                    i.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args){
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
