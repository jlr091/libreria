/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Services;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jose
 */
public class UsersQuearys {

    public Boolean getSesion(Connection Con, String name, String password) throws SQLException {
        Boolean sesion = FALSE;
        String queary = "select user_name, user_password from tb_users where user_name = ('" + name + "') and user_password = ('" + password + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            sesion = TRUE;
            ////System.out.print("correcto");
            return sesion;
        } else {
            ////System.out.print("inccrrecto");
            return sesion;
        }
    }

    public Boolean UsersExist(Connection Con) throws SQLException {
        Boolean sesion = FALSE;
        String queary = "select * from tb_users";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            sesion = TRUE;
            ////System.out.print("usuairos existen");
            return sesion;
        } else {
            ////System.out.print("no existen usuarios");
            return sesion;
        }
    }

    public void ResetPassword(Connection Con, String name, String password) throws SQLException {
        String queary = "UPDATE tb_users SET user_password = ('" + password + "') WHERE (user_name = ('" + name + "'))";
        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public void CreateUsers(Connection Con, String name, String password) throws SQLException {
        String queary = "insert into tb_users (user_name, user_password) " + "VALUE ('" + name + "','" + password + "')";
        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public Boolean OneUserExist(Connection Con, String name) throws SQLException {
        Boolean sesion = FALSE;
        String queary = "select * from tb_users WHERE (user_name = ('" + name + "'))";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            sesion = TRUE;
            ////System.out.print("usuario no existe");
            return sesion;
        } else {
            ////System.out.print("usuario existe");
            return sesion;
        }
    }

}
