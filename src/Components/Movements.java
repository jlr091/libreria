/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Components;

import Components.Banks;
import Services.MovementsQuearys;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Jose
 */
public class Movements {

    Integer pk_id;
    String nombre;
    String abrev;

    public Movements(Integer pk_id, String nombre, String abrev) {
        this.pk_id = pk_id;
        this.nombre = nombre;
        this.abrev = abrev;
    }

    public Movements() {
        this.pk_id = null;
        this.abrev = null;
        this.nombre = null;
    }

    public String getAbrev() {
        return abrev;
    }

    public void setAbrev(String abrev) {
        this.abrev = abrev;
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
        MovementsQuearys movemente_q = new MovementsQuearys();
        String[] list;
        ArrayList<Movements> list_b = new ArrayList<>();

        list_b = movemente_q.getMovements(Con);
        list = new String[list_b.size()];

        for (int i = 0; i < list_b.size() && !list_b.isEmpty(); i++) {
            list[i] = (list_b.get(i).getNombre());
        }

        return list;
    }

    public void Create(Connection Con, String name) throws SQLException {
        MovementsQuearys movemente_q = new MovementsQuearys();
        movemente_q.setNameMovements(Con, name);
    }

    public Movements GetToId(Connection Con, Integer pk_id) throws SQLException {
        MovementsQuearys movemente_q = new MovementsQuearys();
        return movemente_q.getMovementtoId(Con, pk_id);
    }

    public Movements GetToString(Connection Con, String name) throws SQLException {
        MovementsQuearys movemente_q = new MovementsQuearys();
        return movemente_q.get(Con, name);
    }

    public Boolean isUnique(Connection Con, Integer num_movement, String Bank, String Mov) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        MovementsQuearys movemente_q = new MovementsQuearys();
        return movemente_q.UniqueMovement(Con,
                num_movement,
                bank.GetToCuil(Con, Bank).getPk_id(),
                mov.GetToString(Con, Mov).getPk_id()
        );
    }

    public Boolean isUniqueToName(Connection Con, String name) throws SQLException {
        MovementsQuearys movemente_q = new MovementsQuearys();
        return movemente_q.UniqueMovementName(Con, name);
    }
}
