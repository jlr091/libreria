/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Services;

import Components.Banks;
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
public class BanksQuearys {

    public ArrayList<Banks> getListBanks(Connection Con) throws SQLException {
        ArrayList<Banks> banks = new ArrayList<>();

        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery("select * from tb_banks");

        while (rs.next()) {
            Banks one_bank = new Banks();
            one_bank.setPk_id(rs.getInt("pk_id"));
            one_bank.setCuil(rs.getString("cuil"));
            one_bank.setNombre(rs.getString("name"));
            banks.add(one_bank);
        }
        return banks;
    }

    public Banks getBanktoId(Connection Con, Integer pk_id) throws SQLException {
        Banks one_bank = new Banks();
        String queary = "select * from tb_banks where pk_id = ('" + pk_id + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_bank.setPk_id(rs.getInt("pk_id"));
            one_bank.setCuil(rs.getString("cuil"));
            one_bank.setNombre(rs.getString("name"));
            one_bank.setInitbalance(rs.getDouble("initbalance"));
        } else {
            one_bank = null;
        }
        return one_bank;
    }

    public Banks getBanktoCuil(Connection Con, String cuil) throws SQLException {
        Banks one_bank = new Banks();
        String queary = "select * from tb_banks where cuil = ('" + cuil + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_bank.setPk_id(rs.getInt("pk_id"));
            one_bank.setCuil(rs.getString("cuil"));
            one_bank.setNombre(rs.getString("name"));
            one_bank.setInitbalance(rs.getDouble("initbalance"));
        } else {
            one_bank = null;
        }
        return one_bank;
    }

    public void UpdateBank(Connection Con, String cuil, double Balance) throws SQLException {
        Statement s = Con.createStatement();
        String queary = "UPDATE tb_banks SET "
                + " initbalance = ('" + Balance + "')"
                + "WHERE  cuil = '" + cuil + "'";
        s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public Banks getBanktoName(Connection Con, String name) throws SQLException {
        Banks one_bank = new Banks();
        String queary = "select * from tb_banks where name = ('" + name + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        if (rs.next()) {
            one_bank.setPk_id(rs.getInt("pk_id"));
            one_bank.setCuil(rs.getString("cuil"));
            one_bank.setNombre(rs.getString("name"));
            one_bank.setInitbalance(rs.getDouble("initbalance"));
        } else {
            one_bank = null;
        }
        return one_bank;
    }

    public void Create(Connection Con, String name, String cuil, double initbalance) throws SQLException {

        String queary = "insert into tb_banks (name, cuil, initbalance) VALUE ('" + name + "','" + cuil + "','" + initbalance + "')";

        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public Boolean UniqueBank(Connection Con, String cuil) throws SQLException {

        String queary = "SELECT COUNT(*) FROM tb_banks where cuil = ('" + cuil + "')";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        rs.next();
        if (rs.getInt("COUNT(*)") == 0) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    public Boolean ExistBanks(Connection Con) throws SQLException {
        String queary = "SELECT COUNT(*) FROM tb_banks ";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        rs.next();
        if (rs.getInt("COUNT(*)") == 0) {
            return FALSE;
        } else {
            return TRUE;
        }
    }
}
