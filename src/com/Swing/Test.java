package com.Swing;

import Helper.Appointment;
import Helper.Cuisine;
import Helper.DatabaseConnection;
import Helper.Group;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.LinkedList;

public class Test extends JFrame {

    private static final String registerQuery = "INSERT INTO Persons (email, password) VALUES (?, ?)";
    private static final String loginQuery = "SELECT PersonID FROM Persons WHERE email LIKE ? AND password LIKE ?";
    private static final String getCuisinesQuery = "SELECT * FROM cuisine";
    private static final String insertMeeting= "INSERT INTO Meeting (longitude, latitude) VALUES (?,?)";
    private static final String joinGroup = "REPLACE INTO meeting_person SET PersonID = ?, MeetingID = ?;";
    private static final String createAppointment = "INSERT INTO datePlanner (PersonID, GroupID, date) VALUES (?,?,?)";
    private static final String userCuisineEntry = "INSERT INTO user_cuisine (userID, cuisineID) VALUES (?,?)";
    private static final String meetingsOfPerson = "SELECT * FROM Meeting m JOIN meeting_person mp ON m.ID = mp.MeetingID JOIN Persons p ON mp.PersonID = p.PersonID WHERE p.PersonID = ?";




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
    private JList listCuisine;
    private JList listGroups;
    private JButton CONFIRMButton;
    private JTextField tfYear;
    private JTextField tfMonth;
    private JTextField tfDay;
    private JButton addDateButton;
    private JList listDates;
    private JTextField tfEmailRegister;
    private JPasswordField tfPasswordRegister;
    private JPanel RegisterPane;
    private JButton btn_register;
    private JButton openSelectedGroupButton;
    private LinkedList<Helper.Appointment> appointments = new LinkedList<Appointment>();
    private DefaultListModel<String> modelDatesCreateGroupPane = new DefaultListModel<>();
    private DefaultListModel<String> modelMeetingsPerUser = new DefaultListModel<>();

    public Test(){



        listGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setTitle("Card Layout Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cLayout = new CardLayout();
        mainPane.setLayout(cLayout);

        mainPane.add(LOGIN_PAGE, loginPane);
        mainPane.add(GROUPS_PAGE, GroupsPane);
        mainPane.add(CREATE_GROUP_PAGE, CreateGroupPane);
        mainPane.add(REGISTER_PAGE, RegisterPane);
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
                        PersonID = resultSet.getInt("PersonID");
                        System.out.println("Person with id: " + PersonID + " loged in.");
                        showGroupsPane();
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
                    preparedStatement.executeUpdate();
                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    int GroupID = resultSet.getInt(1);
                    System.out.println("Group id: " + GroupID);



                    while (!appointments.isEmpty()){
                        Appointment temp = appointments.remove();
                        PreparedStatement pS = DatabaseConnection.getInstance().getConnection().prepareStatement(createAppointment);
                        pS.setInt(1,temp.getPersonID());
                        pS.setInt(2,GroupID);
                        pS.setDate(3,temp.getDate());
                        pS.executeUpdate();
                    }

                    joinGroup(PersonID,GroupID);



                    JOptionPane.showMessageDialog(null,"Group created with ID: " + GroupID, "Group created",JOptionPane.WARNING_MESSAGE);
                    modelDatesCreateGroupPane.clear();
                    showGroupsPane();
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
                    joinGroup(PersonID,Integer.parseInt(input));

                }
            }
        });




        addDateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Appointment appointment = new Appointment(Date.valueOf(tfYear.getText()+"-"+tfMonth.getText()+"-"+tfDay.getText()),PersonID);
                appointments.add(appointment);
                modelDatesCreateGroupPane.addElement(appointment.getDate().toString());
                listDates.setModel(modelDatesCreateGroupPane);
            }
        });


        btn_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //TODO check if user already exists
                    //register user
                    PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(registerQuery, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setString(1,tfEmailRegister.getText());
                    preparedStatement.setString(2,tfPasswordRegister.getText());
                    preparedStatement.executeUpdate();

                    ResultSet resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    PersonID = resultSet.getInt(1);
                    System.out.println("Person id: " + PersonID);

                    //register cuisine preferences
                    java.util.List cuisines = listCuisine.getSelectedValuesList();
                    for (int i = 0; i<cuisines.size(); i++){
                        Cuisine cuisine = (Cuisine)cuisines.get(i);
                        PreparedStatement stmtUserCuisine = DatabaseConnection.getInstance().getConnection().prepareStatement(userCuisineEntry);
                        stmtUserCuisine.setInt(1,PersonID);
                        stmtUserCuisine.setInt(2,cuisine.getID());
                        stmtUserCuisine.executeUpdate();
                    }

                    showGroupsPane();



                } catch (SQLException a){
                    a.printStackTrace();
                }
            }
        });
    }


    private void joinGroup(int PersonID, int GroupID){
        try {

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(joinGroup);
            preparedStatement.setInt(1, PersonID);
            preparedStatement.setInt(2, GroupID);
            preparedStatement.executeUpdate();
            System.out.println("Proceed with group ID: " + GroupID);
        } catch (SQLException a){
            a.printStackTrace();
        }
    }


    void registerCuisines(int PID, String cuisine){

    }
    void showLoginPane(){
        cLayout.show(mainPane,LOGIN_PAGE);
    }
    void showGroupsPane(){
        DefaultListModel<Group> model = new DefaultListModel<>();
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(meetingsOfPerson);
            preparedStatement.setInt(1,PersonID);
            System.out.println("Query: " + preparedStatement.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                model.addElement(new Group(resultSet.getInt("MeetingID")));
                System.out.println("Group found: " + resultSet.getInt("MeetingID"));

            }
        } catch (SQLException a){
            a.printStackTrace();
        }
        listGroups.setModel(model);
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
        DefaultListModel<Cuisine> model = new DefaultListModel<>();
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(getCuisinesQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.println(resultSet.getString("name"));
                model.addElement(new Cuisine(resultSet.getInt("ID"),resultSet.getString("name")));
                //model.addElement(resultSet.getString("name"));
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
