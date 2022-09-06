/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Services;

/**
 *
 * @author Jose
 */
import Components.Movements;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class MovementsQuearys {

    public ArrayList<Movements> getMovements(Connection Con) throws SQLException {
        ArrayList<Movements> movements = new ArrayList<>();

        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery("select * from tb_movements");

        while (rs.next()) {
            Movements one_movement = new Movements();
            one_movement.setPk_id(rs.getInt("pk_id"));
            one_movement.setAbrev(rs.getString("abrev"));
            one_movement.setNombre(rs.getString("name"));
            movements.add(one_movement);
        }
        return movements;
    }

    public String getNameMovements(Connection Con, Integer pk_id) throws SQLException {
        String nombre = "";
        String queary = "select name from tb_movements where pk_id = " + pk_id;
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            nombre = rs.getString(1);
        }
        return nombre;
    }

    public Integer getPk_IdMovements(Connection Con, String name) throws SQLException {
        Integer pk_id = 0;
        String queary = "select pk_id from tb_movements where name = ('" + name + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);

        if (rs.next()) {
            pk_id = rs.getInt(1);
        }
        return pk_id;
    }

    public void setNameMovements(Connection Con, String name) throws SQLException {
        String abrev = "";
        String queary = "insert into tb_movements (name) " + "VALUE ('" + name + "','" + abrev + "')";
        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public Movements getMovementtoId(Connection Con, Integer pk_id) throws SQLException {
        Movements one_movement = new Movements();
        String queary = "select * from tb_movements where pk_id = " + pk_id;
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_movement.setPk_id(rs.getInt("pk_id"));
            one_movement.setAbrev(rs.getString("abrev"));
            one_movement.setNombre(rs.getString("name"));
        } else {
            one_movement = null;
        }

        return one_movement;
    }

    public Movements getMovementtoName(Connection Con, String name) throws SQLException {
        Movements one_movement = new Movements();
        String queary = "select * from tb_movements where name = ('" + name + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_movement.setPk_id(rs.getInt("pk_id"));
            one_movement.setAbrev(rs.getString("abrev"));
            one_movement.setNombre(rs.getString("name"));
        } else {
            one_movement = null;
        }

        return one_movement;
    }

    public Movements get(Connection Con, String name) throws SQLException {
        Movements one_movement = new Movements();
        String queary = "select * from tb_movements where name = '" + name + "' or abrev = '" + name + "'";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_movement.setPk_id(rs.getInt("pk_id"));
            one_movement.setAbrev(rs.getString("abrev"));
            one_movement.setNombre(rs.getString("name"));
        } else {
            one_movement = null;
        }

        return one_movement;
    }

    public Movements getMovementtoAbrev(Connection Con, String abrev) throws SQLException {
        Movements one_movement = new Movements();
        String queary = "select * from tb_movements where abrev = ('" + abrev + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_movement.setPk_id(rs.getInt("pk_id"));
            one_movement.setAbrev(rs.getString("abrev"));
            one_movement.setNombre(rs.getString("name"));
        } else {
            one_movement = null;
        }

        return one_movement;
    }

    public Boolean UniqueMovement(Connection Con, Integer num_movement, Integer Bank, Integer Mov) throws SQLException {

        String queary
                = "SELECT COUNT(*) FROM tb_rcd_" + Bank + " where num_movement = '" + num_movement + "' &&  movement = '" + Mov + "'";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        rs.next();
        if (rs.getInt("COUNT(*)") == 0) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public Boolean UniqueMovementName(Connection Con, String name) throws SQLException {

        String queary = "SELECT COUNT(*) FROM tb_movements where name = ('" + name + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        rs.next();
        if (rs.getInt("COUNT(*)") == 0) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
