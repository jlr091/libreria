/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package Components;

import Components.Banks;
import libreria.DateManager;
import Components.Movements;
import java.sql.*;
import java.util.ArrayList;
import Services.RecordsQuearys;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Jose
 */
public class Records {

    Integer pk_id;
    Banks banco;
    double monto;
    String detail;
    Integer numero_movimiento;
    Movements tipo_movimiento;
    String fechaEmision;
    String fechaEntradaBanco;

    private static Double truncate2Decimal(double numero) {
        if (numero > 0) {
            return new BigDecimal(String.valueOf(numero)).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue();
        } else {
            return new BigDecimal(String.valueOf(numero)).setScale(2, BigDecimal.ROUND_CEILING).doubleValue();
        }
    }

    public Records(Integer pk_id, Banks banco, double monto, String detail, Integer numero_movimiento, Movements tipo_movimiento, String fecha_movimiento, String fecha_carga) {
        this.pk_id = pk_id;
        this.banco = banco;
        this.monto = monto;
        this.detail = detail;
        this.numero_movimiento = numero_movimiento;
        this.tipo_movimiento = tipo_movimiento;
        this.fechaEmision = fecha_movimiento;
        this.fechaEntradaBanco = fecha_carga;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Movements getTipo_movimiento() {
        return tipo_movimiento;
    }

    public void setTipo_movimiento(Movements tipo_movimiento) {
        this.tipo_movimiento = tipo_movimiento;
    }

    public Records() {
        this.pk_id = null;
        this.banco = null;
        this.monto = 0;
        this.detail = null;
        this.numero_movimiento = null;
        this.tipo_movimiento = null;
        this.fechaEmision = null;
        this.fechaEntradaBanco = null;
    }

    public String getNombre_banco() {
        return banco.getNombre();
    }

    public String getNombre_Movimiento() {
        return tipo_movimiento.getNombre();
    }

    public Integer getPk_id() {
        return pk_id;
    }

    public void setPk_id(Integer pk_id) {
        this.pk_id = pk_id;
    }

    public Integer getNumero_movimiento() {
        return numero_movimiento;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFechaEntradaBanco() {
        return fechaEntradaBanco;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setBanco(Banks bank) {
        this.banco = (bank);
    }

    public Banks getBanco() {
        return banco;
    }

    public void setMovimiento(Movements tipo_movimiento) {
        this.tipo_movimiento = tipo_movimiento;
    }

    public Movements getMovimiento() {
        return tipo_movimiento;
    }

    public void setFechaEntradaBanco(String fecha_carga) {
        DateManager date = new DateManager();
        this.fechaEntradaBanco = date.DateToDB(fecha_carga);
    }

    public ArrayList<Records> GetList(Connection Con, String Bank, String Mov) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> list = new ArrayList<>();

        list = record_q.getListRecords(Con,
                bank.GetToCuil(Con, Bank).getPk_id());

        return list;
    }

    public Records GetRecord(Connection Con, Integer num_movement, String Bank, String Mov) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        Records re = new Records();
        re = record_q.getRecord(Con, num_movement,
                bank.GetToCuil(Con, Bank).getPk_id(),
                 mov.GetToString(Con, Mov).getPk_id());
        return re;
    }

    public void Create(Connection Con, Banks nombre_banco,
            double monto, Integer numero_movimiento, String detail,
            Movements tipo_movimiento, String fecha_movimiento, String fecha_carga,
            String Bank, String Mov) throws SQLException {

        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        Records record = new Records();
        record.setBanco(nombre_banco);
        record.setMonto(monto);
        record.setDetail(detail);
        record.setFechaEmision(fecha_movimiento);
        record.setNumero_movimiento(numero_movimiento);
        record.setMovimiento(tipo_movimiento);
        record.setFechaEntradaBanco(fecha_carga);
        record_q.createRecord(Con, bank.GetToCuil(Con, Bank).getPk_id(),
                 mov.GetToString(Con, Mov).getPk_id(), record);
    }

    public Records CreateLocal(Banks nombre_banco,
            double monto, Integer numero_movimiento, String detail,
            Movements tipo_movimiento, String fecha_movimiento, String fecha_carga) {

        Records record = new Records();
        record.setBanco(nombre_banco);
        record.setMonto(monto);
        record.setDetail(detail);
        record.setFechaEmision(fecha_movimiento);
        record.setNumero_movimiento(numero_movimiento);
        record.setMovimiento(tipo_movimiento);
        record.setFechaEntradaBanco(fecha_carga);

        return record;
    }

    public void EditRecord(Connection Con, Integer num_movement,
            String Bank, String Mov, Records record) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        record_q.UpdateRecord(Con,
                num_movement,
                bank.GetToCuil(Con, Bank).getPk_id(),
                mov.GetToString(Con, Mov).getPk_id(),
                record);
    }

    public void DeleteRecord(Connection Con, Records rec) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        record_q.DeleteRecord(Con,
                rec.getNumero_movimiento(),
                rec.getBanco().getPk_id(),
                rec.getMovimiento().getPk_id());
    }

    public String CurrentDay(Connection Con) throws SQLException {
        RecordsQuearys record_q = new RecordsQuearys();
        return record_q.Current_Date(Con);
    }

    public void setNumero_movimiento(Integer numero_movimiento) {
        this.numero_movimiento = numero_movimiento;
    }

    public Integer GenerateNumMovement(Connection Con, String Bank, String Mov) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        return (1 + record_q.Last_id(Con,
                bank.GetToCuil(Con, Bank).getPk_id(),
                mov.GetToString(Con, Mov).getPk_id()));
    }

    public ArrayList<String[]> ListTeenLastBanks(Connection Con, String Bank) throws SQLException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();
        ArrayList<String[]> list = new ArrayList<>();

        String[] nameBanks = null;
        String[] typeMovements = null;
        String[] numMovements = null;
        String[] values = null;
        String[] dateMovements = null;
        String[] dateCreate = null;
        String[] getdetail = null;

        listRecord = record_q.GetListLastTeeN(Con,
                bank.GetToCuil(Con, Bank).getPk_id());

        if (!listRecord.isEmpty()) {
            nameBanks = new String[listRecord.size()];
            typeMovements = new String[listRecord.size()];
            numMovements = new String[listRecord.size()];
            values = new String[listRecord.size()];
            dateMovements = new String[listRecord.size()];
            dateCreate = new String[listRecord.size()];
            getdetail = new String[listRecord.size()];

            for (int i = 0; i < listRecord.size(); i++) {
                nameBanks[i] = listRecord.get(i).getNombre_banco();
                values[i] = Double.toString(listRecord.get(i).getMonto());
                if (values[i].substring(values[i].length() - 2, values[i].length()).equals(".0")) {
                    values[i] = values[i].concat("0");
                }
                dateMovements[i] = listRecord.get(i).getFechaEmision();
                dateCreate[i] = listRecord.get(i).getFechaEntradaBanco();
                typeMovements[i] = listRecord.get(i).getMovimiento().getAbrev();
                numMovements[i] = listRecord.get(i).getNumero_movimiento().toString();
                getdetail[i] = listRecord.get(i).getDetail();
            }
        }

        list.add(dateMovements);
        list.add(dateCreate);        
        list.add(typeMovements);
        list.add(numMovements);
        list.add(getdetail);
        list.add(values);

        return list;
    }

    public ArrayList<String[]> ListSearchToMov(Connection Con,
            String date_1, String date_2, String Bank, String Mov) throws SQLException, ParseException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        Records order = new Records();
        DateManager dateManager = new DateManager();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();
        ArrayList<String[]> list = new ArrayList<>();

        String[] nameBanks = null;
        String[] typeMovements = null;
        String[] numMovements = null;
        String[] values = null;
        String[] dateMovements = null;
        String[] dateCreate = null;
        String[] getdetail = null;

        listRecord = record_q.ListSearchToMov(Con,
                date_1, date_2,
                bank.GetToCuil(Con, Bank).getPk_id(),
                mov.GetToString(Con, Mov).getPk_id());

        if (!listRecord.isEmpty()) {
            listRecord = order.OrderList(listRecord);
            nameBanks = new String[listRecord.size()];
            typeMovements = new String[listRecord.size()];
            numMovements = new String[listRecord.size()];
            values = new String[listRecord.size()];
            dateMovements = new String[listRecord.size()];
            dateCreate = new String[listRecord.size()];
            getdetail = new String[listRecord.size()];

            for (int i = 0; i < listRecord.size(); i++) {
                nameBanks[i] = listRecord.get(i).getNombre_banco();
                values[i] = Double.toString(listRecord.get(i).getMonto());
                if (values[i].substring(values[i].length() - 2, values[i].length()).equals(".0")) {
                    values[i] = values[i].concat("0");
                }
                dateMovements[i] = dateManager.DateToFront(listRecord.get(i).getFechaEmision());
                dateCreate[i] = dateManager.DateToFront(listRecord.get(i).getFechaEntradaBanco());
                typeMovements[i] = listRecord.get(i).getMovimiento().getAbrev();
                numMovements[i] = listRecord.get(i).getNumero_movimiento().toString();
                getdetail[i] = listRecord.get(i).getDetail();
            }
        }
        
        list.add(dateMovements);
        list.add(dateCreate);        
        list.add(typeMovements);
        list.add(numMovements);
        list.add(getdetail);
        list.add(values);

        return list;
    }

    public ArrayList<Records> OrderList(ArrayList<Records> list) throws ParseException {
        String compareDate[] = {"DEPOSITO", "CHEQUE", "CREDITO", "DEBITO"};
        ArrayList<String> datesUniq = new ArrayList<>();
        ArrayList<Records> result = new ArrayList<>();
        ArrayList<Records> forDate;
        Boolean esUnico = false;
        Records order;

        int i, j;
        if (!list.isEmpty()) {
            int length = list.size();
            datesUniq.add(list.get(0).fechaEntradaBanco);

            for (i = 0; i < length; i++) {
                esUnico = false;
                for (j = 0; j < datesUniq.size(); j++) {
                    if (datesUniq.get(j).equals(list.get(i).fechaEntradaBanco)) {
                        esUnico = false;
                        break;
                    } else {
                        esUnico = true;
                    }
                }
                if (esUnico) {
                    datesUniq.add(list.get(i).fechaEntradaBanco);
                }
            }

            for (i = 0; i < datesUniq.size(); i++) {
                order = new Records();
                forDate = new ArrayList<>();
                for (j = 0; j < length; j++) {
                    if (datesUniq.get(i).equals(list.get(j).fechaEntradaBanco)) {
                        forDate.add(list.get(j));
                    }

                }
                if (!forDate.isEmpty()) {
                    int n, m;
                    for (n = 0; n < forDate.size(); n++) {
                        for (m = 0; m < forDate.size(); m++) {
                            if (forDate.get(n).numero_movimiento <= forDate.get(m).numero_movimiento) {
                                order = forDate.get(m);
                                forDate.set(m, forDate.get(n));
                                forDate.set(n, order);
                            }
                            order = null;
                        }
                    }
                    int contador = 0;
                    for (n = 0; n < compareDate.length; n++) {
                        //System.out.println(compareDate[n]);
                        for (m = 0; m < forDate.size(); m++) {

                            if (compareDate[n].equals(forDate.get(m).getNombre_Movimiento())) {
                                result.add(forDate.get(m));
                            }
                            order = null;
                        }
                    }
                }
                forDate = null;
                order = null;
            }
        }
        return result;
    }

    public ArrayList<String[]> ListSearchToDate(Connection Con, String date_1, String date_2,
            String Bank) throws SQLException, ParseException {
        Banks bank = new Banks();
        Movements mov = new Movements();
        DateManager dateManager = new DateManager();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();
        ArrayList<String[]> list = new ArrayList<>();

        String[] nameBanks = {};
        String[] typeMovements = {};
        String[] numMovements = {};
        String[] dateMovements = {};
        String[] dateCreate = {};
        String[] getdetail = {};
        String[] debe = {};
        String[] haber = {};
        String[] saldo = {};
        double cuenta_saldo = 0;

        listRecord = record_q.ListSearchToDate(Con, dateManager.DateToDB(date_1), dateManager.DateToDB(date_2),
                bank.GetToCuil(Con, Bank).getPk_id());
        if (!listRecord.isEmpty()) {

       //     listRecord = listRecord.get(0).OrderList(listRecord);

            int length = listRecord.size();
            cuenta_saldo = bank.GetToCuil(Con, Bank).getInitbalance();

            nameBanks = new String[length];
            typeMovements = new String[length];
            numMovements = new String[length];
            dateMovements = new String[length];
            dateCreate = new String[length];
            getdetail = new String[length];
            debe = new String[length];
            haber = new String[length];
            saldo = new String[length];

            for (int i = 0; i < length; i++) {
                nameBanks[i] = listRecord.get(i).getNombre_banco();
                dateMovements[i] = dateManager.DateToFront(listRecord.get(i).getFechaEmision());
                dateCreate[i] = dateManager.DateToFront(listRecord.get(i).getFechaEntradaBanco());
                typeMovements[i] = listRecord.get(i).getMovimiento().getAbrev();
                numMovements[i] = listRecord.get(i).getNumero_movimiento().toString();
                getdetail[i] = listRecord.get(i).getDetail();
                if (listRecord.get(i).getMovimiento().getPk_id() == 1 || listRecord.get(i).getMovimiento().getPk_id() == 3) {

                    debe[i] = Double.toString(listRecord.get(i).getMonto());
                    cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
                    if (debe[i].substring(debe[i].length() - 2, debe[i].length()).equals(".0")) {
                        debe[i] = debe[i].concat("0");
                    }
                    haber[i] = "0.00";
                } else {
                    haber[i] = Double.toString(listRecord.get(i).getMonto());
                    cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
                    debe[i] = "0.00";
                    if (haber[i].substring(haber[i].length() - 2, haber[i].length()).equals(".0")) {
                        haber[i] = haber[i].concat("0");
                    }
                }
                saldo[i] = Double.toString(truncate2Decimal(cuenta_saldo));
                if (saldo[i].substring(saldo[i].length() - 2, saldo[i].length()).equals(".0")) {
                    saldo[i] = saldo[i].concat("0");
                }
            }
        }
        /*
        ('1', 'DEPOSITO', 'DP') suma;
        ('2', 'CHEQUE', 'CH') resta;
        ('3', 'CREDITO', 'CR') suma;
        ('4', 'DEBITO', 'DB') resta;*/
        list.add(dateMovements);
        list.add(dateCreate);
        list.add(typeMovements);
        list.add(numMovements);
        list.add(getdetail);
        list.add(debe);
        list.add(haber);
        list.add(saldo);

        return list;
    }

    public String BalanceSearchToDate(Connection Con, String date_1, String date_2,
            String Bank) throws SQLException {
        Banks bank = new Banks();
        DateManager dateManager = new DateManager();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();

        double cuenta_saldo = 0;

        listRecord = record_q.ListSearchToDate(Con, dateManager.DateToDB(date_1), dateManager.DateToDB(date_2),
                bank.GetToCuil(Con, Bank).getPk_id());

        if (!listRecord.isEmpty()) {
            int length = listRecord.size();
            for (int i = 0; i < length; i++) {
                if (listRecord.get(i).getMovimiento().getPk_id() == 1 || listRecord.get(i).getMovimiento().getPk_id() == 3) {
                    cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
                } else {
                    cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
                }
            }
        }

        return Double.toString(truncate2Decimal(cuenta_saldo));
    }

    public String InitBalanceSearchToDate(Connection Con, String date_1,
            String Bank, Boolean Quincena) throws SQLException, ParseException {
        Banks bank = new Banks();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();

        DateManager dateManager = new DateManager();
       // String fecha_al = dateManager.lastDayMonth(date_1, Quincena);
        String fecha_al = date_1;
        listRecord = record_q.ListSearchToDate(Con, dateManager.DateToDB("1800-01-01"), dateManager.DateToDB(fecha_al),
                bank.GetToCuil(Con, Bank).getPk_id());

        double cuenta_saldo = bank.GetToCuil(Con, Bank).getInitbalance();

        if (!listRecord.isEmpty()) {
            int length = listRecord.size();
            for (int i = 0; i < length; i++) {
                if (listRecord.get(i).getMovimiento().getPk_id() == 1 || listRecord.get(i).getMovimiento().getPk_id() == 3) {
                    cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
                } else {
                    cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
                }
            }
        }
        return Double.toString(truncate2Decimal(cuenta_saldo));
    }

    public void CreateLogRec(Connection Con, Integer Bank) throws SQLException {
        RecordsQuearys record_q = new RecordsQuearys();
        record_q.CreateLogRec(Con, Bank);
    }

    public double CalculateInitBalance(Connection Con, String Bank) throws SQLException {

        RecordsQuearys record_q = new RecordsQuearys();
        Banks bank = new Banks();
        ArrayList<Records> listRecord = new ArrayList<>();
        listRecord = record_q.getListRecords(Con, bank.GetToCuil(Con, Bank).getPk_id());

        double cuenta_saldo = bank.GetToCuil(Con, Bank).getInitbalance();
        int length = listRecord.size();
        for (int i = 0; i < length; i++) {
            if (listRecord.get(i).getMovimiento().getPk_id() == 1
                    || listRecord.get(i).getMovimiento().getPk_id() == 3) {
                cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
            } else {
                cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
            }
        }
        return truncate2Decimal(cuenta_saldo);
    }

    public double CalculateFinaltBalance(Connection Con, String Bank) throws SQLException {

        RecordsQuearys record_q = new RecordsQuearys();
        Banks bank = new Banks();
        ArrayList<Records> listRecord = new ArrayList<>();
        listRecord = record_q.getListRecords(Con, bank.GetToCuil(Con, Bank).getPk_id());

        double cuenta_saldo = 0.0;
        int length = listRecord.size();
        for (int i = 0; i < length; i++) {
            if (listRecord.get(i).getMovimiento().getPk_id() == 1
                    || listRecord.get(i).getMovimiento().getPk_id() == 3) {

                cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
            } else {
                cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
            }
        }

        return cuenta_saldo;
    }

    public double CalculateFinaltBalanceToMov(Connection Con,
            String date_1, String date_2, String Bank, String Mov) throws SQLException {

        RecordsQuearys record_q = new RecordsQuearys();
        Banks bank = new Banks();
        ArrayList<Records> listRecord = new ArrayList<>();

        Movements mov = new Movements();

        listRecord = record_q.ListSearchToMov(Con,
                date_1, date_2,
                bank.GetToCuil(Con, Bank).getPk_id(),
                mov.GetToString(Con, Mov).getPk_id());

        double cuenta_saldo = 0.0;
        int length = listRecord.size();
        for (int i = 0; i < length; i++) {

            cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
        }
        return truncate2Decimal(cuenta_saldo);
    }

    public double getInitBalance(Connection Con, String Bank) throws SQLException {

        Banks bank = new Banks();
        return bank.GetToCuil(Con, Bank).getInitbalance();
    }

    public double getBalanceWithDate(Connection Con, String Bank, String date1, String date2) throws SQLException {

        Banks bank = new Banks();
        RecordsQuearys record_q = new RecordsQuearys();
        ArrayList<Records> listRecord = new ArrayList<>();
        listRecord = record_q.ListSearchToDate(Con, date1, date2,
                bank.GetToCuil(Con, Bank).getPk_id());

        double cuenta_saldo = 0;
        int length = listRecord.size();
        for (int i = 0; i < length; i++) {
            if (listRecord.get(i).getMovimiento().getPk_id() == 1
                    || listRecord.get(i).getMovimiento().getPk_id() == 3) {

                cuenta_saldo = cuenta_saldo + listRecord.get(i).getMonto();
            } else {
                cuenta_saldo = cuenta_saldo - listRecord.get(i).getMonto();
            }
        }

        return truncate2Decimal(cuenta_saldo);
    }

    public double NewRecordCalculateBalance(Connection Con, Records record, double Balance) throws SQLException {

        double cuenta_saldo = Balance;
        if (record.getMovimiento().getPk_id() == 1
                || record.getMovimiento().getPk_id() == 3) {

            cuenta_saldo = cuenta_saldo + record.getMonto();
        } else {
            cuenta_saldo = cuenta_saldo - record.getMonto();
        }

        return truncate2Decimal(cuenta_saldo);
    }
}
