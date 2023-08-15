package org.example;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is user class through we are going to perform required operations in user profile
 */

public class user {

    /**
     * The data members declared in this class as private
     */
    private String userName;
    private String pass;
    private String fullName;
    private  Double amount;

    /**
     * This function is used to fetch total amount a user have
     */

    public Double fetchMoney(int userId){
        double res =0.0;
        try (Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from user where id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                res = rs.getDouble("money");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * This function is used to fetch total number of stocks user is holding
     */
    public int fetchCurrentNoOfStocks(int userId){
        int res =0;
        try (Connection conn = DBUtil.provideConnection()) {
            PreparedStatement ps = conn.prepareStatement("select * from user where id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                res = rs.getInt("no_of_stock");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    /**
     * This function is used to display all the user details
     */
    void displayUserDetails(ResultSet rs) throws SQLException {

        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();
        new Main().accept(textIO, null);

        textTerminal.println(rs.getString("full_name")+" Your Details Are :\r\n");
        textTerminal.println(" User ID          :       " + rs.getString("id"));
        textTerminal.println(" User Name        :       " + rs.getString("u_name"));
        textTerminal.println(" Full Name        :       " + rs.getString("full_name"));
        textTerminal.println(" Available Amount :       " + rs.getDouble("money"));
        textTerminal.println(" Your Stocks      :       " + rs.getInt("c_id"));
        textTerminal.println(" No.of Stocks     :       " + rs.getInt("no_of_stock"));
        textTerminal.println(" Price            :       " + rs.getInt("single_stock"));

    }


    /**
     * Getters and setters for achieving the Abstraction
     */


        public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
