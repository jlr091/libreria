/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Services;

import Components.Records;
import Components.Banks;
import Components.Movements;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class RecordsQuearys {

    public ArrayList<Records> getListRecords(Connection Con, Integer Bank) throws SQLException {
        ArrayList<Records> records = new ArrayList<>();

        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery("select * from tb_rcd_" + Bank);

        while (rs.next()) {
            Records one_record = new Records();
            Banks one_bank = new Banks();
            Movements one_movement = new Movements();
            one_movement = new Movements();
            one_record.setPk_id(rs.getInt("pk_id"));

            one_bank = one_bank.GetToId(Con, rs.getInt("bank"));
            one_record.setBanco(one_bank);

            one_movement = one_movement.GetToId(Con, rs.getInt("movement"));
            one_record.setMovimiento(one_movement);

            one_record.setNumero_movimiento(rs.getInt("num_movement"));
            one_record.setDetail(rs.getString("detail"));
            one_record.setMonto(rs.getDouble("value_movement"));
            one_record.setFechaEmision(rs.getString("date_movement"));
            one_record.setFechaEntradaBanco(rs.getString("date_create"));

            records.add(one_record);

            one_record = null;
            one_bank = null;
            one_movement = null;
        }

        return records;
    }

    public void createRecord(Connection Con, Integer Bank, Integer Mov, Records record) throws SQLException {

        String queary;
        queary = "insert into tb_rcd_" + Bank + " (bank, value_movement, num_movement,"
                + " movement, date_movement, date_create, detail) "
                + "VALUE ('" + record.getBanco().getPk_id() + "',"
                + "'" + record.getMonto() + "',"
                + "'" + record.getNumero_movimiento() + "',"
                + "'" + record.getMovimiento().getPk_id() + "',"
                + "'" + record.getFechaEmision() + "',"
                + "'" + record.getFechaEntradaBanco() + "',"
                + "'" + record.getDetail() + "')";
        ////System.out.println(queary);
        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public Records getRecord(Connection Con, Integer num_movement, Integer Bank, Integer Mov
    ) throws SQLException {
        String queary = "select * from tb_rcd_" + Bank + " where num_movement = '" + num_movement + "' && movement = '" + Mov + "'";

        Records one_record = null;
        Banks one_bank = new Banks();
        Movements one_movement = new Movements();

        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);

        if (rs.next()) {
            one_record = new Records();
            one_record.setPk_id(rs.getInt("pk_id"));

            one_bank = one_bank.GetToId(Con, rs.getInt("bank"));
            one_record.setBanco(one_bank);

            one_movement = one_movement.GetToId(Con, rs.getInt("movement"));
            one_record.setMovimiento(one_movement);

            one_record.setDetail(rs.getString("detail"));
            one_record.setNumero_movimiento(rs.getInt("num_movement"));
            one_record.setMonto(rs.getDouble("value_movement"));
            one_record.setFechaEmision(rs.getString("date_movement"));
            one_record.setFechaEntradaBanco(rs.getString("date_create"));

            one_bank = null;
            one_movement = null;
        }
        return one_record;
    }

    public void UpdateRecord(Connection Con, Integer num_movement, Integer Bank,
            Integer Mov, Records NewRecord) throws SQLException {

        Records one_record = new Records();
        one_record = null;
        double up_monto = -1;
        Integer up_num_movimiento = null, up_movement = null;
        String up_fecha_movimiento = null;
        String up_detail = null;
        String fecha_up = null;
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery("select * from tb_rcd_" + Bank + " where  num_movement = '" + num_movement + "' && movement = '" + Mov + "'");

        if (rs.next()) {

            up_monto = (NewRecord.getMonto() != -1) ? NewRecord.getMonto(): null;            
            up_num_movimiento = (NewRecord.getNumero_movimiento() != null) ? NewRecord.getNumero_movimiento() : null;            
            up_movement = (NewRecord.getMovimiento() != null) ? NewRecord.getMovimiento().getPk_id() : null;            
            up_fecha_movimiento = (NewRecord.getFechaEmision() != null) ? NewRecord.getFechaEmision():null;            
            fecha_up = (NewRecord.getFechaEntradaBanco() != null) ? NewRecord.getFechaEntradaBanco():null;            
            up_detail = (NewRecord.getDetail() != null) ? NewRecord.getDetail():null;            

            String queary = "UPDATE tb_rcd_" + Bank + " SET "
                    + " value_movement = ('" + up_monto + "'),"
                    + " num_movement = ('" + up_num_movimiento + "'),"
                    + " movement = ('" + up_movement + "'),"
                    + " date_movement = ('" + up_fecha_movimiento + "'),"
                    + " detail = ('" + up_detail + "'),"
                    + " date_create = ('" + fecha_up + "')"
                    + "WHERE  num_movement = '" + num_movement + "' && movement = '" + Mov + "'";
            s = Con.createStatement();
            s.executeUpdate(queary);
        }
    }

    public void DeleteRecord(Connection Con, Integer num_movement, Integer Bank, Integer Mov) throws SQLException {
        String queary = "Delete from tb_rcd_" + Bank + " where num_movement = '" + num_movement + "' && movement = '" + Mov + "'";
        Statement s = Con.createStatement();
        s.executeUpdate(queary);
    }

    public String Current_Date(Connection Con) throws SQLException {
        String result = null;
        String queary = "select CURDATE()";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);

        if (rs.next()) {
            result = rs.getString("CURDATE()");
        }
        return result;
    }

    public Integer Last_id(Connection Con, Integer Bank, Integer Mov) throws SQLException {
        String queary = "select MAX(num_movement) from tb_rcd_" + Bank + " where movement = '" + Mov + "'";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);
        Integer result = null;
        if (rs.next()) {
            result = rs.getInt("MAX(num_movement)");
        }
        return result;
    }

    public ArrayList<Records> GetListLastTeeN(Connection Con, Integer Bank) throws SQLException {
        ArrayList<Records> records = new ArrayList<>();

        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM tb_rcd_" + Bank + " ORDER BY pk_id DESC Limit 10");

        while (rs.next()) {
            Records one_record = new Records();
            Banks one_bank = new Banks();
            Movements one_movement = new Movements();
            one_record.setPk_id(rs.getInt("pk_id"));

            one_bank = one_bank.GetToId(Con, rs.getInt("bank"));
            one_record.setBanco(one_bank);

            one_movement = one_movement.GetToId(Con, rs.getInt("movement"));
            one_record.setMovimiento(one_movement);

            one_record.setNumero_movimiento(rs.getInt("num_movement"));
            one_record.setMonto(rs.getDouble("value_movement"));
            one_record.setDetail(rs.getString("detail"));
            one_record.setFechaEmision(rs.getString("date_movement"));
            one_record.setFechaEntradaBanco(rs.getString("date_create"));

            records.add(one_record);

            one_record = null;
            one_bank = null;
            one_movement = null;
        }

        return records;
    }

    public ArrayList<Records> ListSearchToMov(Connection Con, String date_1,
            String date_2, Integer Bank, Integer Mov) throws SQLException {
        ArrayList<Records> records = new ArrayList<>();

        Statement s = Con.createStatement();
        String queary = "SELECT * FROM tb_rcd_" + Bank + " where movement = '" + Mov + "'"
                + " and date_create BETWEEN '" + date_1 + "' AND '" + date_2 + "' ORDER BY date_create ASC";

        ResultSet rs = s.executeQuery(queary);

        while (rs.next()) {
            Records one_record = new Records();
            Banks one_bank = new Banks();
            Movements one_movement = new Movements();
            one_record.setPk_id(rs.getInt("pk_id"));

            one_bank = one_bank.GetToId(Con, rs.getInt("bank"));
            one_record.setBanco(one_bank);

            one_movement = one_movement.GetToId(Con, rs.getInt("movement"));
            one_record.setMovimiento(one_movement);

            one_record.setNumero_movimiento(rs.getInt("num_movement"));
            one_record.setMonto(rs.getDouble("value_movement"));
            one_record.setDetail(rs.getString("detail"));
            one_record.setFechaEmision(rs.getString("date_movement"));
            one_record.setFechaEntradaBanco(rs.getString("date_create"));

            records.add(one_record);

            one_record = null;
            one_bank = null;
            one_movement = null;
        }
        return records;
    }

    public ArrayList<Records> ListSearchToDate(Connection Con, String date_1,
            String date_2, Integer Bank) throws SQLException {
        ArrayList<Records> records = new ArrayList<>();
        String queary = "SELECT * FROM tb_rcd_" + Bank + " WHERE date_create BETWEEN '" + date_1 + "' AND '" + date_2 + "'"
                + " ORDER BY date_create ASC";
        Statement s = Con.createStatement();
        ResultSet rs = s.executeQuery(queary);

        while (rs.next()) {
            Records one_record = new Records();
            Banks one_bank = new Banks();
            Movements one_movement = new Movements();
            one_movement = new Movements();
            one_record.setPk_id(rs.getInt("pk_id"));

            one_bank = one_bank.GetToId(Con, rs.getInt("bank"));
            one_record.setBanco(one_bank);

            one_movement = one_movement.GetToId(Con, rs.getInt("movement"));
            one_record.setMovimiento(one_movement);

            one_record.setNumero_movimiento(rs.getInt("num_movement"));
            one_record.setDetail(rs.getString("detail"));
            one_record.setMonto(rs.getDouble("value_movement"));
            one_record.setFechaEmision(rs.getString("date_movement"));
            one_record.setFechaEntradaBanco(rs.getString("date_create"));

            records.add(one_record);

            one_record = null;
            one_bank = null;
            one_movement = null;
        }

        return records;
    }

    public void CreateLogRec(Connection Con, Integer Bank) throws SQLException {
        String queary;

        queary = null;
        queary = "create table tb_rcd_" + Bank + " (\n"
                + "    pk_id INTEGER auto_increment primary key not null ,\n"
                + "    bank INTEGER,\n"
                + "    foreign key (bank) references tb_banks(pk_id),\n"
                + "    value_movement double,\n"
                + "    num_movement int, \n"
                + "    detail varchar(50),\n"
                + "    movement INTEGER,\n"
                + "    foreign key (movement) references tb_movements(pk_id),\n"
                + "    date_movement date,\n"
                + "    date_create date\n"
                + "    )";

        Statement s = Con.createStatement();
        s.executeUpdate(queary);

    }

}
