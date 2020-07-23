package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.*;

public class Main extends Application {

    private static Connection connection;
    public static TableView<Users> table;
    private static PreparedStatement pst;
    private static TableColumn idCol;
    private static TableColumn emriCol;
    private static TableColumn moshaCol;
    private static TableColumn datakrijumeCol;
    private static TextField tfemri;
    private static TextField tfmosha;
    private static TextField tfId;
    private static TextField tfSearch;
    private static String dbname = "CRUDtest";

    private static Connection getConnection() {
        String dbhost = "localhost";
        String dbport = "3306";
        String dbuser = "root";
        String dbpass = "";

        String conn = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(conn, dbuser, dbpass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private static java.sql.Date kthedaten() {
        java.util.Date utilDate = new java.util.Date();
        //        java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
        return new java.sql.Date(utilDate.getTime());
    }


    public static void main(String[] args) throws SQLException {
        krijoDbSchemen();
        launch(args);
    }

    public static void krijoDbSchemen() throws SQLException {
        connection = getConnection();
        String createDb = "CREATE DATABASE IF NOT EXISTS " + dbname;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createDb);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        String createTb = "CREATE TABLE IF NOT EXISTS " + dbname + ".users ("
                + "id integer PRIMARY KEY AUTO_INCREMENT,"
                + "emri VARCHAR(25) NOT NULL,"
                + "mosha INT NOT NULL,"
                + "date_created DATE NOT NULL"
                + ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTb);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertRecords() throws Exception {
        String query = String.format("INSERT INTO users(emri,mosha,date_created) VALUES ('%s',%d,'%s')", tfemri.getText(), Integer.parseInt(tfmosha.getText()), kthedaten());
        executeQuery(query);
        showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users");
    }

    public void deleteRecords() throws Exception {
        String query = "DELETE FROM users WHERE id =" + tfId.getText();
        executeQuery(query);
        showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users");
    }

    public void updateRecords() throws Exception {
        String query = "UPDATE users set emri='" + tfemri.getText() + "',mosha=" + Integer.parseInt(tfmosha.getText()) + " where id=" + tfId.getText();
        executeQuery(query);
        showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users");
    }

    public static void executeQuery(String query) throws SQLException {
        connection = getConnection();
        Statement st;
        try {
            st = connection.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("TableView example");

        tfemri = new TextField();
        tfmosha = new TextField();
        tfId = new TextField();
        tfSearch = new TextField();
        tfSearch.setPrefColumnCount(10);

        tfId.setPrefColumnCount(0);
        tfId.setVisible(false);
        HBox labelHb = new HBox(20);
        labelHb.setAlignment(Pos.CENTER);
        labelHb.getChildren().addAll(new Label("Emri"), tfemri, new Label("Mosha"), tfmosha, tfId);

        table = new TableView<>();

        idCol = new TableColumn("Id");
        emriCol = new TableColumn("Emri");
        moshaCol = new TableColumn("Mosha");
        datakrijumeCol = new TableColumn("Data e krijuar");
        showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users");

        table.getColumns().setAll(idCol, emriCol, moshaCol, datakrijumeCol);
        table.setPrefWidth(450);
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button addbtn = new Button("Add");
        Button updatebtn = new Button("Update");
        Button delbtn = new Button("Delete");
        Button searchbtn = new Button("Search");

        searchbtn.setOnAction(event -> {
            try {
                showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users WHERE emri LIKE '%" + tfSearch.getText() + "%'");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        tfSearch.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    showData(idCol, emriCol, moshaCol, datakrijumeCol, "SELECT * FROM users WHERE emri LIKE '%" + tfSearch.getText() + "%'");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        addbtn.setOnAction(event -> {
            try {
                insertRecords();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        delbtn.setOnAction(event -> {
            try {
                deleteRecords();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        updatebtn.setOnAction(event -> {
            try {
                updateRecords();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        table.setOnMouseClicked(event -> handleMouseAction(event));


        HBox buttonHb = new HBox(25);
        buttonHb.setAlignment(Pos.CENTER);
        buttonHb.getChildren().addAll(addbtn, delbtn, updatebtn);

        HBox searchbox = new HBox(10);
        searchbox.setAlignment(Pos.CENTER);
        searchbox.getChildren().addAll(tfSearch, searchbtn);

        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25, 25, 25, 25));

        vbox.getChildren().addAll(labelHb, table, buttonHb, searchbox);

        Scene scene = new Scene(vbox, 700, 550);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void showData(TableColumn idCol, TableColumn emriCol, TableColumn moshaCol, TableColumn datakrijumeCol, String query) throws Exception {
        ObservableList<Users> list = getUsersList(query);
        idCol.setCellValueFactory(new PropertyValueFactory<Users, Integer>("Id"));
        emriCol.setCellValueFactory(new PropertyValueFactory<Users, String>("Emri"));
        moshaCol.setCellValueFactory(new PropertyValueFactory<Users, Integer>("Mosha"));
        datakrijumeCol.setCellValueFactory(new PropertyValueFactory<Users, Date>("Datakrijuar"));
        table.setItems(list);
    }

    private static ObservableList<Users> getUsersList(String query) {
        ObservableList<Users> users = FXCollections.observableArrayList();
        connection = getConnection();

        try {
            pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            Users users1;
            while (rs.next()) {
                users1 = new Users(rs.getInt("id"), rs.getString("emri"), rs.getInt("mosha"), rs.getDate("date_created"));
                users.add(users1);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public void handleMouseAction(MouseEvent e) {
        Users selectedItem = table.getSelectionModel().getSelectedItem();
        tfId.setText("" + selectedItem.getId());
        tfemri.setText(selectedItem.getEmri());
        tfmosha.setText(String.valueOf(selectedItem.getMosha()));
    }

}


