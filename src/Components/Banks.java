/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Components;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import Services.BanksQuearys;

/**
 *
 * @author Jose
 */
public class Banks {

    Integer pk_id;
    String cuil;
    String nombre;
    Double initbalance;
    Double finaltbalance;

    public Banks(Integer pk_id, String cuil, String nombre, Double initbalance, Double finaltbalance) {
        this.pk_id = pk_id;
        this.cuil = cuil;
        this.nombre = nombre;
        this.initbalance = initbalance;
        this.finaltbalance = finaltbalance;
    }

    public Banks() {
        this.pk_id = null;
        this.nombre = null;
        this.cuil = null;
        this.initbalance = null;
        this.finaltbalance = null;
    }

    public Double getInitbalance() {
        return initbalance;
    }

    public void setInitbalance(Double initbalance) {
        this.initbalance = initbalance;
    }

    public Double getFinaltbalance() {
        return finaltbalance;
    }

    public void setFinaltbalance(Double finaltbalance) {
        this.finaltbalance = finaltbalance;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public Integer getPk_id() {
        return pk_id;
    }

    public void setPk_id(Integer pk_id) {
        this.pk_id = pk_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String[] GetList(Connection Con) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        String[] list;
        ArrayList<Banks> list_b = new ArrayList<>();

        list_b = bank_q.getListBanks(Con);
        list = new String[list_b.size()];

        for (int i = 0; i < list_b.size() && !list_b.isEmpty(); i++) {
            list[i] = (list_b.get(i).getNombre() + "-" + list_b.get(i).getCuil());
        }

        return list;
    }

    public String[] GetListWithCuil(Connection Con) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        String[] list;
        ArrayList<Banks> list_b = new ArrayList<>();

        list_b = bank_q.getListBanks(Con);
        list = new String[list_b.size()];

        for (int i = 0; i < list_b.size() && !list_b.isEmpty(); i++) {
            list[i] = (list_b.get(i).getNombre() + " - " + list_b.get(i).getCuil());
        }

        return list;
    }

    public void Create(Connection Con, String name, String cuil, double initbalance) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        bank_q.Create(Con, name, cuil, initbalance);
    }

    public Banks GetToId(Connection Con, Integer pk_id) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        return bank_q.getBanktoId(Con, pk_id);
    }

    public Banks GetToName(Connection Con, String name) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        return bank_q.getBanktoName(Con, name);
    }

    public Banks GetToCuil(Connection Con, String Cuil) throws SQLException {
        BanksQuearys bank_q = new BanksQuearys();
        return bank_q.getBanktoCuil(Con, Cuil);
    }

    public Boolean isUnique(Connection Con, String cuil) throws SQLException {
        BanksQuearys BanksQuearys_q = new BanksQuearys();
        return BanksQuearys_q.UniqueBank(Con, cuil);
    }

    public Boolean ExistBanks(Connection Con) throws SQLException {
        BanksQuearys BanksQuearys_q = new BanksQuearys();
        return BanksQuearys_q.ExistBanks(Con);
    }

    public void UpdateBalance(Connection Con, String Bank, double Balance) throws SQLException {
        BanksQuearys BanksQuearys_q = new BanksQuearys();
        BanksQuearys_q.UpdateBank(Con, Bank, Balance);
    }

}
