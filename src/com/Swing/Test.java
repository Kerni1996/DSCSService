package com.Swing;

import Helper.*;
import com.mysql.cj.conf.ConnectionUrlParser;
import netscape.javascript.JSObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class Test extends JFrame {

    private static final String registerQuery = "INSERT INTO Persons (email, password) VALUES (?, ?)";
    private static final String loginQuery = "SELECT PersonID FROM Persons WHERE email LIKE ? AND password LIKE ?";
    private static final String getCuisinesQuery = "SELECT * FROM cuisine";
    private static final String insertMeeting= "INSERT INTO Meeting (longitude, latitude) VALUES (?,?)";
    private static final String joinGroup = "REPLACE INTO meeting_person SET PersonID = ?, MeetingID = ?;";
    private static final String createAppointment = "INSERT INTO datePlanner (PersonID, GroupID, date) VALUES (?,?,?)";
    private static final String userCuisineEntry = "INSERT INTO user_cuisine (userID, cuisineID) VALUES (?,?)";
    private static final String meetingsOfPerson = "SELECT * FROM Meeting m JOIN meeting_person mp ON m.ID = mp.MeetingID JOIN Persons p ON mp.PersonID = p.PersonID WHERE p.PersonID = ?";
    private static final String cuisinePopularity = "SELECT p.PersonID, c.ID, c.name FROM Meeting m JOIN meeting_person mp ON m.id = mp.MeetingID JOIN Persons p ON p.PersonID = mp.PersonID JOIN user_cuisine uc ON uc.userID = p.PersonID JOIN cuisine c ON c.id=uc.cuisineID WHERE m.id = ?";
    private static final String groupMembers= "SELECT COUNT(*) FROM meeting_person WHERE MeetingID = ?";
    private static final String groupDates = "SELECT * FROM datePlanner WHERE GroupID = ?";
    private static final String distinctGroupDates = "SELECT DISTINCT date FROM datePlanner WHERE GroupID = ?";
    private static final String groupSize = "SELECT COUNT(DISTINCT PersonID, MeetingID) FROM meeting_person WHERE MeetingID = ?;";
    private static final String docuMenu = "https://api.documenu.com/v2/restaurants/search/geo?";
    private static final String cuisineName = "SELECT name FROM cuisine WHERE ID = ?;";
    private static final String longitude = "SELECT longitude from Meeting WHERE ID = ?;";
    private static final String latitude = "SELECT latitude from Meeting WHERE ID = ?;";
    private static final String updateMeeting="UPDATE Meeting SET restaurant =?, address=? WHERE ID = ?";


    private int PersonID = 0;



    public final String LOGIN_PAGE = "login page";
    public final String GROUPS_PAGE= "groups page";
    public final String CREATE_GROUP_PAGE = "create groups page";
    public final String REGISTER_PAGE = "register user page";
    public final String GROUP_DETAIL_PAGE ="group detail page";
    public final String VOTE_DATE_PAGE = "vote date page";

    private final CardLayout cLayout;
    private int desiredGroup = 0;
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
    private JPanel GroupsDetailPane;
    private JButton BACKButton;
    private JLabel detail_id;
    private JLabel detail_date;
    private JLabel detail_restaurant;
    private JLabel detail_address;
    private JLabel detail_NumberParticipants;
    private JList voteDatesList;
    private JButton CONFIRMButtonVote;
    private JPanel votepane;
    private JButton logOutButton;
    private JPanel mapPanel;
    private LinkedList<Helper.Appointment> appointments = new LinkedList<Appointment>();
    private DefaultListModel<String> modelDatesCreateGroupPane = new DefaultListModel<>();
    private DefaultListModel<String> modelDatesVotePane = new DefaultListModel<>();
    private DefaultListModel<String> modelMeetingsPerUser = new DefaultListModel<>();

    public Test(){
        //System.out.println(calculateCuisines(3));
        //System.out.println(calculateDates(4));



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
        mainPane.add(GROUP_DETAIL_PAGE,GroupsDetailPane);
        mainPane.add(VOTE_DATE_PAGE,votepane);
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
                    desiredGroup = Integer.parseInt(input);
                    joinGroup(PersonID,Integer.parseInt(input));
                    showVoteDatePane(Integer.parseInt(input));

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
        openSelectedGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Group group = (Group)listGroups.getSelectedValue();
                updateGroup(group.getID());
                showGroupDetailsPane();



            }
        });
        CONFIRMButtonVote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List dates = voteDatesList.getSelectedValuesList();
                try {
                    for (int i = 0; i < dates.size(); i++) {
                        Date date = Date.valueOf(dates.get(i).toString());
                        PreparedStatement pS = DatabaseConnection.getInstance().getConnection().prepareStatement(createAppointment);
                        pS.setInt(1,PersonID);
                        pS.setInt(2,desiredGroup);
                        pS.setDate(3,date);
                        pS.executeUpdate();
                    }
                }catch (SQLException a){
                    a.printStackTrace();
                }

                showGroupsPane();
                modelDatesVotePane.clear();
            }
        });
        BACKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGroupsPane();
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField1.setText("");
                passwordField1.setText("");
                PersonID = -1;
                showLoginPane();
            }
        });
    }

    private void addMap(double longitude, double latitude){
        JXMapViewer mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the focus
        GeoPosition frankfurt = new GeoPosition(latitude, longitude);

        mapViewer.setZoom(7);
        mapViewer.setAddressLocation(frankfurt);
        mapPanel.removeAll();
        mapPanel.add(mapViewer);

    }


    private void joinGroup(int PersonID, int GroupID){
        try {

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(joinGroup);
            preparedStatement.setInt(1, PersonID);
            preparedStatement.setInt(2, GroupID);
            preparedStatement.executeUpdate();
            System.out.println("Proceed with group ID: " + GroupID);

            showGroupsPane();
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

    void updateGroup(int id){
        HashMap<Date,Integer> dates = calculateDates(id);
        System.out.println("preferred Dates: " + calculateDates(id));
        HashMap<Integer,Integer> cuisines = calculateCuisines(id);
        int groupSize = getGroupSize(id);
        Restaurant restaurant = getRestaurant(cuisines,getLongitude(id),getLatitude(id));
        try{
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(updateMeeting);
            preparedStatement.setString(1,restaurant.getName());
            preparedStatement.setString(2,restaurant.getAddress());
            preparedStatement.setInt(3,id);
            preparedStatement.executeUpdate();
        } catch (SQLException a){
            a.printStackTrace();
        }

        detail_id.setText("ID: "+id);
        Map.Entry<Date,Integer> entry = dates.entrySet().iterator().next();
        detail_date.setText("Date: "+entry.getKey().toString());
        detail_restaurant.setText("Restaurant: "+restaurant.getName());
        detail_address.setText("Address: "+restaurant.getAddress());
        detail_NumberParticipants.setText("Participants: "+entry.getValue());


        /*for (Map.Entry<Date,Integer> entry : dates.entrySet()){
            Date currentDate = entry.getKey();
            int currentCounter = entry.getValue();
        }*/





    }

    //HashMap<Integer,Integer> cuisines, String longitude, String latitude
    Restaurant getRestaurant (HashMap<Integer,Integer> cuisines, String longitude, String latitude){
        //extract cuisines:
        boolean firstFlag = true;
        String cuisineString = "";
        try {
            for (Map.Entry<Integer, Integer> entry : cuisines.entrySet()) {
                int currentID = entry.getKey();
                int currentCounter = entry.getValue();
                PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(cuisineName);
                preparedStatement.setInt(1, currentID);
                ResultSet resultSet = preparedStatement.executeQuery();
                resultSet.next();
                String name = resultSet.getString(1);
                if (firstFlag){
                    cuisineString = name;
                } else {
                    cuisineString +="," + name;
                }
            }
        }catch (SQLException a){
            a.printStackTrace();
        }


        try {
            //https://api.documenu.com/v2/restaurants/search/geo?lat=40.688072&lon=-73.997385&distance=99999&key=df1082ac3bfa0e339773ec134a339aef&cuisine=Italian
            URL url = new URL(docuMenu+"lat=" + latitude + "&lon=" + longitude + "&distance=99999&key=df1082ac3bfa0e339773ec134a339aef&cuisine=" + cuisineString);
            System.out.println(url);


            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            //System.out.println(content);
            JSONObject obj = new JSONObject(content.toString());
            System.out.println(obj.getJSONArray("data").get(0));
            JSONObject t1 = new JSONObject(obj.getJSONArray("data").get(0).toString());
            Restaurant restaurant = new Restaurant(t1.getString("restaurant_name"),t1.getJSONObject("address").getString("formatted"),t1.getJSONObject("geo").getDouble("lat"),t1.getJSONObject("geo").getDouble("lon"));
            System.out.println("latitude: " + restaurant.getLatitude());
            System.out.println("longitude: " + restaurant.getLongitude());

            //updateMap
            addMap(restaurant.getLongitude(), restaurant.getLatitude());

            return restaurant;
        }catch (IOException i){
            i.printStackTrace();
        }
        return null;
    }

    String getLongitude(int groupID){
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(longitude);
            preparedStatement.setInt(1, groupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        }catch (SQLException a){
            a.printStackTrace();
        }
        return null;
    }

    String getLatitude(int groupID){
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(latitude);
            preparedStatement.setInt(1, groupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString(1);
        }catch (SQLException a){
            a.printStackTrace();
        }
        return null;
    }

    int getGroupSize(int id){
        try {

            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(groupSize);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        }catch (SQLException a){
            a.printStackTrace();
        }
        return -1;
    }

    void showCreateGroupPane(){
        cLayout.show(mainPane,CREATE_GROUP_PAGE);
    }

    void showRegisterPane(){
        cLayout.show(mainPane,REGISTER_PAGE);
    }

    void showGroupDetailsPane(){

        cLayout.show(mainPane,GROUP_DETAIL_PAGE);
    }

    void showVoteDatePane(int groupID) {

        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(distinctGroupDates);
            preparedStatement.setInt(1, groupID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Date date = resultSet.getDate("date");
                modelDatesVotePane.addElement(date.toString());

            }
            voteDatesList.setModel(modelDatesVotePane);

        } catch (SQLException a) {
            a.printStackTrace();
        }
        cLayout.show(mainPane, VOTE_DATE_PAGE);
    }

    public static void main(String[] args) {
        new Test();

    }

    int getNumberOfMembersInGroup(int groupID){
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(groupMembers);
            preparedStatement.setInt(1,groupID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getInt(1);
        } catch (SQLException a){
            a.printStackTrace();
        }
        //if error occurs return negative number
        return -1;
    }

     HashMap<Integer,Integer>calculateCuisines(int groupID){
        //create HashMap to count how many (V, counter) users prefer which cuisine (K, cuisine ID) => HashMap(cuisineID, counter)
        HashMap<Integer, Integer > hashMap = new HashMap<Integer, Integer>();
        try {
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(cuisinePopularity);
            preparedStatement.setInt(1,groupID);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int cuisineID = resultSet.getInt("ID");

                //if cuisine is already in hashMap increment counter by 1. otherwise create entry for cuisine with counter = 1
                if (hashMap.containsKey(cuisineID)){
                    int counter = hashMap.get(cuisineID)+1;
                    hashMap.put(cuisineID,counter);
                } else hashMap.put(cuisineID,1);
            }
            System.out.println(hashMap);
        } catch (SQLException a){
            a.printStackTrace();
        }


        //get highest score
        int highestScore = -1;
         for (Map.Entry<Integer,Integer> entry : hashMap.entrySet()){
             int currentID = entry.getKey();
             int currentCounter = entry.getValue();
             if (currentCounter>highestScore){
                 highestScore = currentCounter;
             }
        }

         HashMap<Integer,Integer> preferredCuisine = new HashMap<>();
         //return cuisines with highest score
         for (Map.Entry<Integer,Integer> entry : hashMap.entrySet()) {
             int currentCounter = entry.getValue();
             int currentID = entry.getKey();
             if (currentCounter == highestScore) {
                 preferredCuisine.put(currentID, currentCounter);
             }
         }

         return preferredCuisine;
         //int highest = hashMap.get

    }

    /**
     * calculate preferred Dates for meeting
     * @param groupID
     * @return HasMap with preferrred Dates as keys and number of people who voted for dates as values
     */
    HashMap<Date,Integer> calculateDates(int groupID){
        try {

            HashMap<Date, LinkedList> hashMap = new HashMap<>();
            PreparedStatement preparedStatement = DatabaseConnection.getInstance().getConnection().prepareStatement(groupDates);
            preparedStatement.setInt(1,groupID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                int personID = resultSet.getInt("PersonID");
                Date date = resultSet.getDate("date");
                //B bSystem.out.println("date: " + date + " person: " + PersonID);
                //if Date entry exists in Map...
                if (hashMap.containsKey(date)){
                    //and the Person did not vote several times for the same date..
                    if (!hashMap.get(date).contains(personID)){
                        //add the Person to the date
                        hashMap.get(date).add(personID);
                    }
                }else {
                    //if date is not in map yet, create entry with user
                    hashMap.put(date, new LinkedList<Integer>(Arrays.asList(personID)));
                }
            }

            System.out.println("Hashmap with Dates and UserID: " + hashMap);

            int highestMatching = -1;
            for (Map.Entry<Date,LinkedList> entry : hashMap.entrySet()){
                Date currentDate = entry.getKey();
                LinkedList currentUsers = entry.getValue();
                if (currentUsers.size()>highestMatching){
                    highestMatching = currentUsers.size();
                }
            }

            //create List of preferred Dates
            HashMap<Date,Integer> preferredDates = new HashMap<>();
            for (Map.Entry<Date,LinkedList> entry : hashMap.entrySet()) {
                Date currentDate = entry.getKey();
                LinkedList currentUsers = entry.getValue();
                if (currentUsers.size()==highestMatching){
                    preferredDates.put(currentDate,highestMatching);
                }
            }
            return preferredDates;


        }catch (SQLException a){
            a.printStackTrace();
        }

        return null;
    }

    void populateCuisineList(){


        //System.out.println("testCuisimnr");
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
