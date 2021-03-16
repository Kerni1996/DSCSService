package com.Swing;

import Helper.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Test extends JFrame {

    private static final String registerQuery = "INSERT INTO Persons (email, password) VALUES (?, ?)";
    private static final String loginQuery = "SELECT PersonID FROM Persons WHERE email LIKE ? AND password LIKE ?";
    private static final String getCuisinesQuery = "SELECT * FROM cuisine";
    private static final String insertMeeting= "INSERT INTO Meeting (longitude, latitude, startDate, endDate) VALUES (?,?,?,?)";
    private static final String joinGroup = "REPLACE INTO meeting_person SET PersonID = ?, MeetingID = ?;";

    private int PersonID = 0;



    public final String LOGIN_PAGE = "login page";
    public final String GROUPS_PAGE= "groups page";
    public final String CREATE_GROUP_PAGE = "create groups page";
    public final String REGISTER_PAGE = "register user page";

    private final CardLayout cLayout;
    private JPanel mainPane;
    private JPanel loginPane;
    private JPanel GroupsPane;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton loginButton;
    private JButton registerButton;
    private JButton createGroupButton;
    private JButton joinGroupButton;
    private JPanel CreateGroupPane;
    private JTextField tF_longitude;
    private JTextField tF_latitude;
    private JTextField tF_startDate;
    private JTextField tF_endDate;
    private JScrollPane RegisterPane;
    private JList listCuisine;
    private JList list1;
    private JButton CONFIRMButton;

    public Test(){

        setTitle("Card Layout Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cLayout = new CardLayout();
        mainPane.setLayout(cLayout);

        mainPane.add(LOGIN_PAGE, loginPane);
        mainPane.add(GROUPS_PAGE, GroupsPane);
        mainPane.add(CREATE_GROUP_PAGE, CreateGroupPane);
        mainPane.add(REGISTER_PAGE,RegisterPane);
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
                    if (resultSet.next()) {
                        showWorldPane();
                        PersonID = resultSet.getInt("PersonID");
                        System.out.println("Person with id: " + PersonID + " loged in.");
                    }
                }catch (SQLException i){
                    i.printStackTrace();
                }
            }
        });


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateCuisineList();
                showRegisterPane();
                /*try {
                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(registerQuery);
                    preparedStatement.setString(1, textField1.getText());
                    preparedStatement.setString(2, passwordField1.getText());
                    preparedStatement.executeUpdate();
                }catch (SQLException i){
                    i.printStackTrace();
                }
                */
            }
        });
        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateGroupPane();
            }
        });


        ////INSERT INTO Meeting (longitude, latitude, startDate, endDate) VALUES ('4.3', '2.1', '2021-03-15', '2021-03-16')
        CONFIRMButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(insertMeeting, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1, tF_longitude.getText());
                    preparedStatement.setString(2, tF_latitude.getText());
                    preparedStatement.setDate(3, Date.valueOf(tF_startDate.getText()));
                    preparedStatement.setDate(4, Date.valueOf(tF_endDate.getText()));
                    preparedStatement.executeUpdate();
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    int id = resultSet.getInt(1);
                    System.out.println("Group id: " + id);
                    JOptionPane.showMessageDialog(null,"Group created with ID: " + id, "Group created",JOptionPane.WARNING_MESSAGE);
                    showWorldPane();
                }catch (SQLException a) {
                    a.printStackTrace();
                }
            }
        });
        joinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(null,"Enter Group ID:", "Join Group", JOptionPane.PLAIN_MESSAGE);
                if (input!=null){
                    try {

                        PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(joinGroup);
                        preparedStatement.setInt(1, PersonID);
                        preparedStatement.setInt(2, Integer.parseInt(input));
                        preparedStatement.executeUpdate();
                        System.out.println("Proceed with group ID: " + input);
                    } catch (SQLException a){
                        a.printStackTrace();
                    }
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

    void showCreateGroupPane(){
        cLayout.show(mainPane,CREATE_GROUP_PAGE);
    }

    void showRegisterPane(){
        cLayout.show(mainPane,REGISTER_PAGE);
    }

    public static void main(String[] args) {

        new Test();
    }

    void populateCuisineList(){


        System.out.println("testCuisimnr");
        DefaultListModel<String> model = new DefaultListModel<>();
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(getCuisinesQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.println(resultSet.getString("name"));
                model.addElement(resultSet.getString("name"));
            }
            listCuisine.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }





    }

    private void createUIComponents() {

        // TODO: place custom component creation code here
    }
}
