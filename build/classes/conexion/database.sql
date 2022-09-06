/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Jose
 * Created: 11/07/2021
 
   static String username = "root";
    static String password = "libAlveroni*2021";
*/
create database db_libAlveroni;
use rg_registros_db;
create table tb_movimientos (
     pk_id INTEGER auto_increment primary key not null,
    nombre varchar(50)
);
create table tb_bancos (
     pk_id INTEGER auto_increment primary key not null,
    nombre varchar(50)
);

create table tb_users (
     pk_id INTEGER auto_increment primary key not null,
    user_name varchar(50) not null,
    user_passowrd varchar(50) not null
);

create table tb_records (
    pk_id INTEGER auto_increment primary key not null ,
    bank INTEGER,
    foreign key (bank) references tb_banks(pk_id),
    val_movement double,
    num_movement int, 
    movement INTEGER,
    foreign key (movement) references tb_movements(pk_id),
    date_movement date,
    date_create date
    );
