package org.example;


/**
 * TRADING SYSTEM AS A MAVEN PROJECT USING JAVA AND JDBC
 * SQL DRIVERS JAR FILE IS LOADED IN THE MAVEN PROJECT DEPENDENCIES
 * AND THE JAR FILE FOR TEXTIO ARE ALSO IMPORTED
 */

import java.io.*;
import java.util.*;
/**
HERE NECESSARY LIBRARIES ARE IMPORTED
 */
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.web.RunnerData;
import java.sql.*;
import java.time.Month;
import java.util.function.BiConsumer;

/**
Main class for the start
 */
public class Main implements BiConsumer<TextIO, RunnerData> {

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {

        /**
         *TextIo library is used for displaying and getting input from user
         * Here the textIo object is initialised
         */
        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();

        /**
         * Here we have provided the user different choices
         */

        boolean f = true;
        while(f){
            user usr = new user();
            textTerminal.getProperties().setPromptColor("cyan");

            textTerminal.println("****************************************************");
            textTerminal.println("Welcome To Trading System");
            textTerminal.println("****************************************************");
            textTerminal.println("1. User Login");
            textTerminal.println("2. Register New User");
            textTerminal.println("3. View Company Stocks");
            textTerminal.println("4. To exit portal");

            int choice = textIO.newIntInputReader()
                    .withMaxVal(4)
                    .read("Choose your option");

    //
            if (choice == 1) {

                /**
                 * In this we are getting the user credentials to login into portal
                 */

                textTerminal.println("****************************************************");
                textTerminal.println("LOGIN <<---->> CUSTOMER");
                textTerminal.println("****************************************************");

                String userName = textIO.newStringInputReader()
                        .read("Enter UserName");
                String password = textIO.newStringInputReader()
                        .withInputMasking(true)
                        .read("Enter Password");

                textTerminal.println("-----------------------------------------------------");

                /**
                 * we are connecting to mySql database for checking credentials
                 */
                try(Connection conn = DBUtil.provideConnection()){
                    PreparedStatement ps = conn.prepareStatement("select * from user where u_name = ? and password = ?; ");
                    ps.setString(1,userName);
                    ps.setString(2,password);

                    ResultSet rs = ps.executeQuery();

                    if (rs.next()){
                        textTerminal.println(" Welcome " + rs.getString("full_name"));
                        int userId = rs.getInt("id");

                        boolean m = true;
                        /**
                         * Now after successful login the following operations are allowed by the user
                         */
                        while (m) {
                            textTerminal.println("****************************************************");
                            textTerminal.println("1. View User Details");
                            textTerminal.println("2. View Order Book");
                            textTerminal.println("3. View Companies");
                            textTerminal.println("4. Sell Stocks");
                            textTerminal.println("5. Buy Stocks");
                            textTerminal.println("6. View Pending Orders");
                            textTerminal.println("7. Log Out");

                            int x = textIO.newIntInputReader()
                                    .read("Choose your option");

                            if (x == 1) {
                                /**
                                 * For displaying the users details of the user
                                 */
                                textTerminal.println("-----------------------------------------------------");
                                textTerminal.println("User Details");
                                usr.displayUserDetails(rs);

                            }
                            if (x == 2) {
                                /**
                                 * For displaying order book for getting history of all bought and sold stocks
                                 */
                                textTerminal.println("-----------------------------------------------------");
                                textTerminal.println("Order Book");
                                OrderBook order = new OrderBook();
                                order.displayOrderBook();
                            }
                            if (x == 3) {
                                /**
                                 * Here we are displaying the current listed stocks in the system
                                 */
                                textTerminal.println("-----------------------------------------------------");
                                textTerminal.println("Listed Companies");
                                Company cmp = new Company();
                                cmp.printCompanyDetails();
                            }
                            if (x == 4) {

                                /**
                                 * Here the functionality for selling stocks is implemented
                                 */
                                textTerminal.println("-----------------------------------------------------");

                                textTerminal.println("Enter Details to sell stock");
                                /**
                                 * Accepting the details for initiating the order
                                 */
                                int companyId = textIO.newIntInputReader()
                                        .read("Enter Company Id");

                                int noOfShares = textIO.newIntInputReader()
                                        .read("Enter No. of Shares to Sell");

                                int price = textIO.newIntInputReader()
                                        .read("Enter price at which you want to sell");

                                /**
                                 * Checking if any Buy order for the given data set is present in execution table
                                 */
                                PreparedStatement ps2 = conn.prepareStatement("select * from exchange where c_id = ? AND single_stock = ? AND status = 'Buy' AND no_of_stock <= ?; ");

                                ps2.setInt(1,companyId);
                                ps2.setInt(2,price);
                                ps2.setInt(3,noOfShares);

                                /**
                                 * If buy order is found then execute the following queries
                                 */
                                ResultSet rs2 = ps2.executeQuery();
                                if (rs2.next()){
                                    Company cp = new Company();

                                    textTerminal.println(cp.fetchCompanyName(companyId));
                                    /**
                                     * Here calculation is made to update the final results after the transaction
                                     */

                                    int nsu1 =  usr.fetchCurrentNoOfStocks(userId)-rs2.getInt("no_of_stock");
                                    double mou1 = usr.fetchMoney(userId)+rs2.getDouble("single_stock")*rs2.getInt("no_of_stock");

                                    int nsu2 = usr.fetchCurrentNoOfStocks(rs2.getInt("u_id"))+rs2.getInt("no_of_stock");
                                    double mou2 = usr.fetchMoney(rs2.getInt("u_id"))-rs2.getDouble("single_stock")*rs2.getInt("no_of_stock");


                                    if (noOfShares==rs2.getInt("no_of_stock")){
                                        /**
                                         * Here the query for updating the profile of user whose selling order is present
                                         */
                                        try(Connection con = DBUtil.provideConnection()) {
                                            PreparedStatement ps3 = con.prepareStatement("UPDATE user SET c_id = ?, no_of_stock = ?, single_stock = ?,money = ? WHERE id = ?;");
                                            ps3.setInt(1, companyId);
                                            ps3.setInt(2, nsu2);
                                            ps3.setDouble(3, price);
                                            ps3.setDouble(4, mou2);
                                            ps3.setDouble(5, rs2.getInt("u_id"));
                                            ps3.execute();
                                            ps3.close();

                                            /**
                                             * Here the query for updating the profile of current user
                                             */
                                            PreparedStatement ps4 = con.prepareStatement("UPDATE user SET c_id = 0, no_of_stock = 0, single_stock = 0,money = ? WHERE id = ?;");
                                            ps4.setDouble(1, mou1);
                                            ps4.setInt(2, rs2.getInt("u_id"));
                                            ps4.execute();
                                            ps4.close();

                                            /**
                                             * Query to update orderbook for this the transaction
                                             */

                                            PreparedStatement ps5 = con.prepareStatement("insert into orderbook(u_id,c_id,no_of_stock,single_stock,order_type) select u_id,c_id,no_of_stock,single_stock,status from exchange where ex_id = ?;");
                                            ps5.setInt(1, rs2.getInt("ex_id"));
                                            ps5.execute();
                                            ps5.close();

                                            /**
                                             * And Finally remove the current pending order as it is completed
                                             */

                                            PreparedStatement ps6 = conn.prepareStatement("delete from exchange where ex_id = ?;");
                                            ps6.setInt(1,rs2.getInt("ex_id"));
                                            ps6.execute();
                                            ps6.close();


                                        }
                                        catch (SQLException e){
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        /**
                                         * Here the query for updating the profile of user whose selling order is present
                                         */
                                        try(Connection con = DBUtil.provideConnection()) {
                                            PreparedStatement ps3 = con.prepareStatement("UPDATE user SET c_id = ?, no_of_stock = ?, single_stock = ?,money = ? WHERE id = ?;");
                                            ps3.setInt(1, companyId);
                                            ps3.setInt(2, nsu2);
                                            ps3.setDouble(3, price);
                                            ps3.setDouble(4, mou2);
                                            ps3.setDouble(5,  rs2.getInt("u_id"));
                                            ps3.execute();
                                            ps3.close();

                                            /**
                                             * Here the query for updating the profile of current user
                                             */

                                            PreparedStatement ps4 = con.prepareStatement("UPDATE user SET   no_of_stock = ?,money = ? WHERE id = ?;");
                                            ps4.setInt(1, nsu1);
                                            ps4.setDouble(2, mou1);
                                            ps4.setInt(3, userId);
                                            ps4.execute();
                                            ps4.close();

                                            /**
                                             * Query to update orderbook for this the transaction
                                             */

                                            PreparedStatement ps5 = con.prepareStatement("insert into orderbook(u_id,c_id,no_of_stock,single_stock,order_type) select u_id,c_id,no_of_stock,single_stock,status from exchange where ex_id = ?;");
                                            ps5.setInt(1, rs2.getInt("ex_id"));
                                            ps5.execute();
                                            ps5.close();

                                            /**
                                             * And Finally make changes in the current pending order
                                             */

                                            PreparedStatement ps7 = con.prepareStatement("UPDATE exchange SET no_of_stock = ?,status = 'Sell' WHERE ex_id = ?;");
                                            ps7.setInt(1, noOfShares-rs2.getInt("no_of_stock"));
                                            ps7.setDouble(2, rs2.getInt("ex_id"));
                                            ps7.execute();
                                            ps7.close();

                                        }
                                        catch (SQLException e){
                                            e.printStackTrace();
                                        }
                                    }


                                    textTerminal.println("Your Order has been executed");

                                }
                                else{

                                    /**
                                     * If the corresponding order not found then add this order as pending order in execution table
                                     */

                                    try(Connection con = DBUtil.provideConnection()) {
                                        PreparedStatement ps3 = con.prepareStatement("INSERT INTO exchange (u_id,c_id,no_of_stock,single_stock,status) values (?,?,?,?,'Sell');");
                                        ps3.setInt(1, userId);
                                        ps3.setInt(2, companyId);
                                        ps3.setDouble(3, noOfShares);
                                        ps3.setDouble(4, price);

                                        ps3.execute();
                                        ps3.close();
                                        textTerminal.println("Your Order has been initiated.");

                                    }
                                }
                            }
                            if (x == 5) {
                                textTerminal.println("-----------------------------------------------------");

                                /**
                                 * Here the functionality for buying stocks is implemented
                                 */

                                textTerminal.println("Enter Details to buy stock");

                                /**
                                 * Accepting the details for initiating the order
                                 */
                                int companyId = textIO.newIntInputReader()
                                        .read("Enter Company Id");

                                int noOfShares = textIO.newIntInputReader()
                                        .read("Enter No. of Shares to buy");
                                double price = textIO.newDoubleInputReader()
                                        .read("Enter price at which you want to buy");

                                /**
                                 * Checking if any Buy order for the given data set is present in execution table
                                 */

                                PreparedStatement ps2 = conn.prepareStatement("select * from exchange where c_id = ? AND single_stock = ? AND status = 'Sell' AND no_of_stock >= ?; ");

                                ps2.setInt(1,companyId);
                                ps2.setDouble(2,price);
                                ps2.setInt(3,noOfShares);

                                /**
                                 * If buy order is found then execute the following queries
                                 */

                                ResultSet rs2 = ps2.executeQuery();
                                if (rs2.next()){

                                    Company cp = new Company();

                                    textTerminal.println(cp.fetchCompanyName(companyId));

                                    /**
                                     * Here calculation is made to update the final results after the transaction
                                     */

                                    int nsu1 =  usr.fetchCurrentNoOfStocks(userId)+noOfShares;
                                    double mou1 = usr.fetchMoney(userId)-rs2.getDouble("single_stock")*noOfShares;

                                    int nsu2 = usr.fetchCurrentNoOfStocks(rs2.getInt("u_id"))-noOfShares;
                                    double mou2 = usr.fetchMoney(rs2.getInt("u_id"))+rs2.getDouble("single_stock")*noOfShares;



                                    if (noOfShares==rs2.getInt("no_of_stock")){
                                        /**
                                         * Here the query for updating the profile of user whose selling order is present
                                         */

                                        try(Connection con = DBUtil.provideConnection()) {
                                            PreparedStatement ps3 = con.prepareStatement("UPDATE user SET c_id = ?, no_of_stock = ?, money = ?, single_stock = ? WHERE id = ?;");
                                            ps3.setInt(1, companyId);
                                            ps3.setInt(2, nsu1);
                                            ps3.setDouble(3, mou1);
                                            ps3.setDouble(4, price);
                                            ps3.setInt(5, userId);
                                            ps3.execute();
                                            ps3.close();

                                            /**
                                             * Here the query for updating the profile of current user
                                             */

                                            PreparedStatement ps4 = con.prepareStatement("UPDATE user SET no_of_stock = ?, money = ? WHERE id = ?;");
                                            ps4.setInt(1, nsu2);
                                            ps4.setDouble(2, mou2);
                                            ps4.setInt(3, rs2.getInt("u_id"));
                                            ps4.execute();
                                            ps4.close();

                                            /**
                                             * Query to update orderbook for this the transaction
                                             */

                                            PreparedStatement ps5 = con.prepareStatement("insert into orderbook(u_id,c_id,no_of_stock,single_stock,order_type) select u_id,c_id,no_of_stock,single_stock,status from exchange where ex_id = ?;");
                                            ps5.setInt(1, rs2.getInt("ex_id"));
                                            ps5.execute();
                                            ps5.close();

                                            /**
                                             * And Finally remove the current pending order as it is completed
                                             */

                                            PreparedStatement ps6 = conn.prepareStatement("delete from exchange where ex_id = ?;");
                                            ps6.setInt(1,rs2.getInt("ex_id"));
                                            ps6.execute();
                                            ps6.close();


                                        }
                                        catch (SQLException e){
                                            e.printStackTrace();
                                        }
                                    }

                                    else {
                                        /**
                                         * Here the query for updating the profile of user whose selling order is present
                                         */
                                        try(Connection con = DBUtil.provideConnection()) {
                                            PreparedStatement ps3 = con.prepareStatement("UPDATE user SET c_id = ?, no_of_stock = ?, single_stock = ?,money = ? WHERE id = ?;");
                                            ps3.setInt(1, companyId);
                                            ps3.setInt(2, nsu2);
                                            ps3.setDouble(3, price);
                                            ps3.setDouble(4, mou2);
                                            ps3.setDouble(5,  userId);
                                            ps3.execute();
                                            ps3.close();

                                            /**
                                             * Here the query for updating the profile of current user
                                             */

                                            PreparedStatement ps4 = con.prepareStatement("UPDATE user SET   no_of_stock = ?,money = ? WHERE id = ?;");
                                            ps4.setInt(1, nsu1);
                                            ps4.setDouble(2, mou1);
                                            ps4.setInt(3, rs2.getInt("u_id"));
                                            ps4.execute();
                                            ps4.close();

                                            /**
                                             * Query to update orderbook for this the transaction
                                             */

                                            PreparedStatement ps5 = con.prepareStatement("insert into orderbook(u_id,c_id,no_of_stock,single_stock,order_type) select u_id,c_id,no_of_stock,single_stock,status from exchange where ex_id = ?;");
                                            ps5.setInt(1, rs2.getInt("ex_id"));
                                            ps5.execute();
                                            ps5.close();

                                            /**
                                             * And Finally make changes in the current pending order in execution table
                                             */

                                            PreparedStatement ps7 = con.prepareStatement("UPDATE exchange SET no_of_stock = ? WHERE ex_id = ?;");
                                            ps7.setInt(1, rs2.getInt("no_of_stock")-noOfShares);
                                            ps7.setDouble(2, rs2.getInt("ex_id"));
                                            ps7.execute();
                                            ps7.close();
                                        }
                                        catch (SQLException e){
                                            e.printStackTrace();
                                        }
                                    }


                                    textTerminal.println("Your Order has been executed");
                                }
                                else{

                                    /**
                                     * If the corresponding order not found then add this order as pending order in execution table
                                     */

                                    try(Connection con = DBUtil.provideConnection()) {
                                        PreparedStatement ps3 = con.prepareStatement("INSERT INTO exchange (u_id,c_id,no_of_stock,single_stock,status) values (?,?,?,?,'Buy');");
                                        ps3.setInt(1, userId);
                                        ps3.setInt(2, companyId);
                                        ps3.setDouble(3, noOfShares);
                                        ps3.setDouble(4, price);
                                        ps3.execute();
                                        ps3.close();
                                        textTerminal.println("Your Order has been initiated.");


                                    }
                                    catch (SQLException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if(x==6){

                                /**
                                 * For Printing current pending orders in execution table
                                 */

                                textTerminal.println("Current Pending Orders.....");
                                Execution ex = new Execution();
                                ex.displayExecutionTable();
                            }
                            if (x == 7) {

                                /**
                                 * In this option we logout from portal by
                                 * simply changing the value of flag variable to false
                                 */

                                textTerminal.println("Logging Out.........");
                                m = false;
                            }
                        }
                    }
                    else {

                        /**
                         * If user has entered the wrong credentials then again ask him to enter correct details
                         */
                        textTerminal.println(" Wrong Credentials !!!! Please check the details and Login again");

                    }
                }


                catch (SQLException e) {
                    throw new RuntimeException(e);
                }


        }
        if(choice == 2) {

            /**
             * In this block we will register new user
             * First we will get details from user
             * After that we will create user account and update details in database
             */

            textTerminal.println("****************************************************");
            String userName = textIO.newStringInputReader()
                    .read("Enter Username");
            String password = textIO.newStringInputReader()
                    .withMinLength(6)
                    .withInputMasking(true)
                    .read("Create Password");
            String password2 = textIO.newStringInputReader()
                    .withMinLength(6)
                    .read("Confirm again");

            if (password.equals(password2)) {
                textTerminal.println("-----------------------------------------------------");

                textTerminal.println("Hi "+userName+" Your account is created");
                textTerminal.println("-----------------------------------------------------");
                String fullName = textIO.newStringInputReader()
                        .read("Enter Your Full Name");
                Double amount = textIO.newDoubleInputReader()
                        .read("Enter total amount you have");

                /**
                 * After getting details from user we will connect to database and perform Insert query for adding in database
                 */

                try(Connection conn = DBUtil.provideConnection()) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO user (u_name,full_name,password,money)VALUES (?,?,?,?);");
                    ps.setString(1, userName);
                    ps.setString(2, fullName);
                    ps.setString(3, password);
                    ps.setDouble(4, amount);
                    ps.execute();
                    ps.close();
                }
                /**
                 * On successfully account creation we will ask user to login again
                 */
                textTerminal.println("Your account is created successfully. Please login again .......");

            }
            else {
                /**
                 * If password didn't match show error message
                 */

                textTerminal.println("Your password didn't match");
            }

        }
        if (choice == 3) {
            textTerminal.println("-----------------------------------------------------");

            /**
             * In this we display all the listed and available stocks in the system
             */
            textTerminal.println("All available Company Stocks");
            textTerminal.println("-----------------------------------------------------");

            textTerminal.println("Listed Companies");

            Company cmp = new Company();
            cmp.printCompanyDetails();

        }
        if(choice==4) {

            /**
             * Here the user gets exit from the portal and the program gets stopped
             * and simultaneously the TextIO terminal also gets terminated
             */

            textIO.dispose();
            f = false;
        }
    }
    }

    @Override
    public void accept(TextIO textIO, RunnerData runnerData) {

    }
}