package org.example;
import java.sql.*;
import java.io.*;
import java.util.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.web.RunnerData;
import java.sql.*;
import java.util.function.BiConsumer;

public class UserUtil extends user{

    public user userLogin(String userName, String password, String fullName) throws SQLException {
        final TextIO textIo;
        textIo = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIo.getTextTerminal();
        TextIO textIO = TextIoFactory.getTextIO();
        new Main().accept(textIO, null);
        user u = null;

        try(Connection conn = DBUtil.provideConnection()){
            PreparedStatement ps = conn.prepareStatement("select * from user where userName = ? and name = ? and password = ?; ");
            ps.setString(1,userName);
            ps.setString(2,fullName);
            ps.setString(3,password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                textTerminal.println(" Welcome " + fullName);
            }
            else {
                textTerminal.println(" Wrong Credentials ");
            }
        }


        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return u;
    }


}
