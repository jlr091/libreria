/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package conexion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 *
 * @author Jose
 */
public class conexion {

    Connection connection = null;
    Statement statement = null;

    static String driver = "com.mysql.cj.jdbc.Driver";
    static String url = "jdbc:mysql://localhost:3400/db_libAlveroni";
   // static String username = "admin";
   // static String password = "libAlveroni*2021";
    static String username = "root";
    static String password = "admin";

    public Connection obtener() throws FileNotFoundException, IOException {
        try {
            Class.forName(driver).newInstance();
            connection = DriverManager.getConnection(url, username, password);
            if (connection != null) {
                System.out.println("Se conecto a la base de datos");
                return connection;
            } else {
                System.out.println("No se conecto a la base de datos");
            }
        } catch (ClassNotFoundException | SQLException | IllegalAccessException | InstantiationException cnfex) {
        }
        return connection;
    }

    public void cerrar(Connection con) throws SQLException {
        con.close();
    }
}

/*
drop database db_libalveroni;
create database db_libalveroni;
use db_libalveroni;
create table tb_movements (
pk_id INTEGER auto_increment primary key not null,
name varchar(50),
abrev varchar(5)
);
INSERT INTO `db_libalveroni`.`tb_movements` (`pk_id`, `name`, `abrev`) VALUES ('1', 'DEPOSITO', 'DP');
INSERT INTO `db_libalveroni`.`tb_movements` (`pk_id`, `name`, `abrev`) VALUES ('2', 'CHEQUE', 'CH');
INSERT INTO `db_libalveroni`.`tb_movements` (`pk_id`, `name`, `abrev`) VALUES ('3', 'CREDITO', 'CR');
INSERT INTO `db_libalveroni`.`tb_movements` (`pk_id`, `name`, `abrev`) VALUES ('4', 'DEBITO', 'DB');


create table tb_banks (
pk_id INTEGER auto_increment primary key not null,
name varchar(50),
cuil integer,
initbalance double,
finalbalance double
);

create table tb_users (
pk_id INTEGER auto_increment primary key not null,
user_name varchar(50) not null,
user_password varchar(50) not null
);
iNSERT INTO tb_users (`user_name`, `user_password`) VALUES ("admin","admin");

/*create table tb_rcd_1_ch (
pk_id INTEGER auto_increment primary key not null ,
bank INTEGER,
foreign key (bank) references tb_banks(pk_id),
value_movement double,
num_movement int,
detall varchar(50),
movement INTEGER,
foreign key (movement) references tb_movements(pk_id),
date_movement date,
date_create date
);
 */
