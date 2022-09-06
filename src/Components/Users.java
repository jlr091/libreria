/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Components;

import java.sql.Connection;

import Services.UsersQuearys;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.sql.SQLException;

/**
 *
 * @author Jose
 */
public class Users {

    String user_name;
    String user_password;

    public Users() {
        this.user_name = null;
        this.user_password = null;
    }

    public Users(Integer pk_id, String user_name, String user_password) {
        this.user_name = user_name;
        this.user_password = user_password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public Boolean Create(Connection con, String name, String password) throws SQLException {
        UsersQuearys user = new UsersQuearys();
        Boolean exist = TRUE;
        exist = user.OneUserExist(con, name);
        if (exist) {
            exist = FALSE;
            return exist;
        } else {
            user.CreateUsers(con, name, password);
            //System.out.println("usuario creado");
            return exist;
        }
    }

    public Boolean OneUserExist(Connection con, String name) throws SQLException {
        UsersQuearys user = new UsersQuearys();
        Boolean exist;
        if (user.OneUserExist(con, name)) {
            exist = TRUE;
        } else {
            exist = FALSE;
        }
        return exist;
    }

    public Boolean InitSesion(Connection con, String name, String password) throws SQLException {
        UsersQuearys user = new UsersQuearys();
        Boolean result = FALSE;
        result = user.getSesion(con, name, password);

        return result;
    }

    public void ResetPassword(Connection Con, String name, String password) throws SQLException {
        UsersQuearys user = new UsersQuearys();
        user.ResetPassword(Con, name, password);
    }

    public Boolean UserExist(Connection Con) throws SQLException {
        UsersQuearys user = new UsersQuearys();
        return user.UsersExist(Con);
    }

}
