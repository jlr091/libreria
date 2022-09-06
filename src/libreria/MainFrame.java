/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package libreria;

import Components.Banks;
import Components.Movements;
import Components.Records;
import Components.Users;
import conexion.conexion;
import libreria.MyTablePrintable;
import libreria.DateManager;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import static java.lang.Boolean.FALSE;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Calendar;

/**
 *
 * @author Jose
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form NewJFrame
     */
    //estados
    // public Boolean user_state = FALSE;
    public Boolean record_state = FALSE;
    public Boolean banks_state = FALSE;
    public Boolean SESION_INIT = FALSE;
    public int origin_edit = 0;
    public int origin_print = 0;
    String CuilSelect = "";
    String MovSelection = "";
    String MovSelectionToEdit = "";
    String MovSelectionToSearch = "";

    //conexion
    public conexion _con = null;
    public Connection Con = null;
    //datos
    public int count = 0;

    protected JCheckBox headerBox;
    protected JCheckBox footerBox;
    protected JTextField headerField;
    protected JTextField footerField;

    String[] list_banks;
    String[] list_movements;

    Integer idBankToSearch = null;
    String idMovToSearch = null;
    Integer idBankToSearchToMov = null;
    String idMovToSearchToMov = null;
    Integer numMovementeToSearchToMov = null;
    Integer numMovementeToSearch = null;

    String currentDay = "";

    public Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public void InitVisibleFalse() {
        alert_bank_void.setVisible(false);
        alert_mov_void.setVisible(false);
        alert_value_void.setVisible(false);
        alert_numMov_void.setVisible(false);
        alert_date_void.setVisible(false);
        alertNewBank.setVisible(false);
        alertExistBank.setVisible(false);
        alert_detail_void.setVisible(false);
        //  alertNewMovement.setVisible(false);
        //  alertExistMovement.setVisible(false);
        //  dateFormatAlert.setVisible(false);
        dateToSearchAlert.setVisible(false);
        rangeDatetoMov.setVisible(false);

        alert_sesion.setVisible(false);
        user_input.setVisible(false);
        pass_input.setVisible(false);

        alertEdit.setVisible(false);
        alertUserCreate.setVisible(false);
    }

    public void printTableSearch(String date_1, String date_2,
            String bank) throws SQLException {
        Records new_record = new Records();
        ArrayList<String[]> ListRecords = new ArrayList<>();
        DateManager dateForm = new DateManager();
        date_1 = _dateFirst.getText();
        date_2 = _dateEnd.getText();
        Boolean esQuincena = false;
        DefaultTableModel dm = (DefaultTableModel) TableSearch.getModel();
        int rowCount = dm.getRowCount();
        //Remove rows one by one from the end of the table

        for (int i = rowCount - 1; i >= 0; i--) {
            dm.removeRow(i);
        }

        if (dateForm.isDateValid(date_1) && dateForm.isDateValid(date_2)) {
            //        dateFormatAlert.setVisible(false);
            date_1 = dateForm.DateToDB(_dateFirst.getText());
            date_2 = dateForm.DateToDB(_dateEnd.getText());

            try {
                if (dateForm.isValidRange(date_1, date_2)) {
                    dateToSearchAlert.setVisible(false);

                    ListRecords = new_record.ListSearchToDate(Con, date_1, date_2, bank);
                    if (ListRecords.get(0) != null) {
                        int length = ListRecords.get(0).length;
                        if (length > 0) {
                            DefaultTableModel model = new DefaultTableModel();
                            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                            rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

                            for (int i = 0; i < length; i++) {
                                model = (DefaultTableModel) TableSearch.getModel();
                                Object[] newRecord = {"", "", "", "", "", ""};
                                model.addRow(newRecord);
                                TableSearch.setValueAt(ListRecords.get(0)[i], i, 0);
                                TableSearch.setValueAt(ListRecords.get(1)[i], i, 1);
                                TableSearch.setValueAt(ListRecords.get(2)[i], i, 2);
                                TableSearch.setValueAt(ListRecords.get(3)[i], i, 3);
                                TableSearch.setValueAt(ListRecords.get(4)[i], i, 4);
                                TableSearch.setValueAt(ListRecords.get(5)[i], i, 5);
                                TableSearch.setValueAt(ListRecords.get(6)[i], i, 6);
                                TableSearch.setValueAt(ListRecords.get(7)[i], i, 7);
                            }
                            TableSearch.getColumn("DEBE").setCellRenderer(rightRenderer);
                            TableSearch.getColumn("HABER").setCellRenderer(rightRenderer);
                            TableSearch.getColumn("SALDO").setCellRenderer(rightRenderer);
                            esQuincena = esQuincenal.isSelected();
                            _datePass.setText(dateForm.lastDayMonth(_dateFirst.getText(), esQuincenal.isSelected()));
                            printBalance(new_record.InitBalanceSearchToDate(Con, _datePass.getText(), bank, esQuincena));
                            finalBalanceText.setText((ListRecords.get(7))[length - 1]);
                        }
                    }
                } else {
                    dateToSearchAlert.setVisible(true);
                }

            } catch (ParseException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            dateToSearchAlert.setVisible(true);
        }
    }

    public void printTableSearchToMov(String date_1, String date_2,
            String bank) throws SQLException {
        Records new_record = new Records();
        ArrayList<String[]> ListRecords = new ArrayList<>();
        DateManager datecast = new DateManager();
        date_1 = _dateFirsttoMov.getText();
        date_2 = _dateEndtoMov.getText();
        double balanceToMov = 0.0;
        DefaultTableModel dm = (DefaultTableModel) TableSearchToMov.getModel();
        int rowCount = dm.getRowCount();
        //Remove rows one by one from the end of the table

        for (int i = rowCount - 1; i >= 0; i--) {
            dm.removeRow(i);
        }

        if (datecast.isDateValid(date_1) && datecast.isDateValid(date_2)) {
            //        dateFormatAlert.setVisible(false);
            date_1 = datecast.DateToDB(_dateFirsttoMov.getText());
            date_2 = datecast.DateToDB(_dateEndtoMov.getText());

            try {
                if (datecast.isValidRange(date_1, date_2)) {
                    dateToSearchAlert.setVisible(false);

                    if (MovSelectionToSearch != null) {
                        ListRecords = new_record.ListSearchToMov(Con,
                                date_1, date_2,
                                CuilSelect,
                                MovSelectionToSearch);
                        if (ListRecords.get(0) != null) {
                            int length = ListRecords.get(0).length;
                            if (length > 0) {
                                DefaultTableModel model = new DefaultTableModel();
                                DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
                                rightRenderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);

                                for (int i = 0; i < length; i++) {
                                    model = (DefaultTableModel) TableSearchToMov.getModel();
                                    Object[] newRecord = {"", "", "", "", "", ""};
                                    model.addRow(newRecord);
                                    TableSearchToMov.setValueAt(ListRecords.get(0)[i], i, 0);
                                    TableSearchToMov.setValueAt(ListRecords.get(1)[i], i, 1);
                                    TableSearchToMov.setValueAt(ListRecords.get(2)[i], i, 2);
                                    TableSearchToMov.setValueAt(ListRecords.get(3)[i], i, 3);
                                    TableSearchToMov.setValueAt(ListRecords.get(4)[i], i, 4);
                                    TableSearchToMov.setValueAt(ListRecords.get(5)[i], i, 5);
                                    balanceToMov = balanceToMov + Double.parseDouble(ListRecords.get(5)[i]);
                                }

                                //TableSearchToMov.getColumn("DEBE").setCellRenderer(rightRenderer);
                                //TableSearchToMov.getColumn("HABER").setCellRenderer(rightRenderer);
                                TableSearchToMov.getColumn("MONTO").setCellRenderer(rightRenderer);

                                if (balanceToMov > 0) {
                                    balanceToMov = (new BigDecimal(String.valueOf(balanceToMov)).setScale(2, BigDecimal.ROUND_FLOOR).doubleValue());
                                } else {
                                    balanceToMov = (new BigDecimal(String.valueOf(balanceToMov)).setScale(2, BigDecimal.ROUND_CEILING).doubleValue());
                                }
                                finalBalanceTextToMov.setText(String.valueOf(balanceToMov));
                            }
                        }
                    }
                } else {
                    rangeDatetoMov.setVisible(true);
                }
            } catch (ParseException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            rangeDatetoMov.setVisible(true);
        }
    }

    public void SaveEditRecord(Integer numMovT, String bank, String tipeMov) throws SQLException {
        Records new_record = new Records();
        Banks Rbank = new Banks();
        Movements movement = new Movements();
        DateManager datecast = new DateManager();
        Banks nombre_banco = null;
        double monto = -1;

        String detalle = null;
        Integer numero_movimiento = null;
        Movements tipo_movimiento = null;
        String fechaEmsion = null;
        String fecha_carga = null;
        if (!EditValueText.getText().isEmpty()) {
            if (pattern.matcher(EditValueText.getText()).matches()) {
                monto = Double.parseDouble(EditValueText.getText());
            }
        }
        if (!EditNumMovementText.getText().isEmpty()) {
            numero_movimiento = Integer.parseInt(EditNumMovementText.getText());
        }
        fechaEmsion = EditDateMovementeText.getText();
        if (datecast.isDateValid(fechaEmsion)) {
            //  dateFormatAlert.setVisible(false);
            fechaEmsion = datecast.DateToDB(EditDateMovementeText.getText());
        } else {
            fechaEmsion = "";
        }

        fecha_carga = EditDateEnterText.getText();
        if (datecast.isDateValid(fecha_carga)) {
            //  dateFormatAlert.setVisible(false);
            fecha_carga = datecast.DateToDB(EditDateEnterText.getText());
        } else {
            fecha_carga = "";
        }

        if (!EditDetailText.getText().isEmpty()) {
            detalle = EditDetailText.getText();
        }
        try {

            nombre_banco = Rbank.GetToCuil(Con, CuilSelect);
            tipo_movimiento = movement.GetToString(Con, MovSelectionToEdit);

            if (nombre_banco == null) {
                alert_bank_void.setVisible(true);
            } else {
                alert_bank_void.setVisible(false);
            }
            if (tipo_movimiento == null) {
                alert_mov_void.setVisible(true);
            } else {
                alert_mov_void.setVisible(false);
            }
            if (monto == -1) {
                alert_value_void.setVisible(true);
            } else {
                alert_value_void.setVisible(false);
            }

            if ("".equals(fechaEmsion)) {
                alert_date_void.setVisible(true);
            } else {
                alert_date_void.setVisible(false);
            }

            if ("".equals(fecha_carga)) {
                alert_date_void.setVisible(true);
            } else {
                alert_date_void.setVisible(false);
            }

            if (numero_movimiento == null) {
                alert_numMov_void.setVisible(true);
            } else if (tipo_movimiento != null) {
                boolean equialMov = tipo_movimiento.getAbrev().equals(tipeMov)
                        && EditNumMovementText.getText().equals(numMovCurrent.getText());
                if (tipo_movimiento.isUnique(Con, numero_movimiento, CuilSelect,
                        MovSelectionToEdit) || equialMov) {

                    alert_numMov_void.setVisible(false);
                } else {
                    numero_movimiento = null;
                    alert_numMov_void.setVisible(true);
                }
            } else {
                alert_numMov_void.setVisible(false);
            }

            if (nombre_banco != null && monto != -1 && numero_movimiento != null && detalle != null
                    && tipo_movimiento != null && !"".equals(fechaEmsion) && !"".equals(fecha_carga)) {

                new_record = new_record.CreateLocal(nombre_banco, monto,
                        numero_movimiento, detalle, tipo_movimiento, fechaEmsion, fecha_carga);
                new_record.EditRecord(Con,
                        numMovT, bank,
                        tipo_movimiento.GetToString(Con, tipeMov).getNombre(),
                        new_record);
                EditValueText.setText("");
                EditDetailText.setText("");
                EditNumMovementText.setText("");
                EditDateMovementeText.setText("");
                EditDateEnterText.setText("");
                editRecordFrame.setVisible(false);
                alertEdit.setVisible(false);
            } else {

                alertEdit.setVisible(true);
            }
            switch (origin_edit) {
                case 1:
                    try {
                        printTableSearch(_dateFirst.getText(), _dateEnd.getText(), CuilSelect);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 2:
                    PrintTableInsertRecord();
                    break;
                case 3:
                    try {
                        printTableSearchToMov(_dateFirsttoMov.getText(), _dateEndtoMov.getText(), CuilSelect);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                default:
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        origin_edit = 0;
    }

    public void PrintTables(JTable TableToPrint) {
        try {
            String fBalance = "";
            String dateFirst = "";
            String dateEnd = "";
            String saldo_al = "";
            String fecha_al = "";
            Boolean esQuincena = false;
            Banks bank = new Banks();
            Records new_record = new Records();
            bank = bank.GetToCuil(Con, CuilSelect);
            DateManager dateManager = new DateManager();

            if (origin_print == 1) {
                dateFirst = _dateFirst.getText();
                dateEnd = _dateEnd.getText();
                fBalance = finalBalanceText.getText();

                esQuincena = esQuincenal.isSelected();
                fecha_al = dateManager.lastDayMonth(dateFirst, esQuincena);
                saldo_al = new_record.InitBalanceSearchToDate(Con, fecha_al, CuilSelect, esQuincena);
            } else if (origin_print == 2) {
                dateFirst = _dateFirsttoMov.getText();
                dateEnd = _dateEndtoMov.getText();
                fBalance = finalBalanceTextToMov.getText();

                saldo_al = Double.toString(new_record.getBalanceWithDate(Con, CuilSelect, dateFirst, dateEnd));
            }

            fecha_al = dateManager.lastDayMonth(dateFirst, esQuincena);

            /* calendar.setTime(dateform.DateFormatDate(currentDay));
            calendar.add(Calendar.DATE, -1);
            Date currentDate = calendar.getTime();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
             fecha_al = dateFormat. format(currentDate);*/
            String HeaderLinea0 = "  ";
            String HeaderLinea1 = "              Alveroni";
            String HeaderLinea2 = "           Libreria Juridica                         " + bank.getNombre();
            String HeaderLinea3 = "                                    "
                    + "Resumen de Cuenta: " + bank.getCuil() + ""
                    + "                                            Fecha al " + (currentDay);
            String HeaderLinea4 = "                                              "
                    + "                  Desde: " + dateFirst + "   Hasta: " + dateEnd;
            String HeaderLinea5 = "                                                     "
                    + "                                              Saldo al: " + fecha_al + "           " + saldo_al;

            MessageFormat[] header = new MessageFormat[6];

            header[0] = new MessageFormat(HeaderLinea0);
            header[1] = new MessageFormat(HeaderLinea1);
            header[2] = new MessageFormat(HeaderLinea2);
            header[3] = new MessageFormat(HeaderLinea3);
            header[4] = new MessageFormat(HeaderLinea4);
            header[5] = new MessageFormat(HeaderLinea5);

            MessageFormat[] footer = new MessageFormat[2];

            String FooterLinea0 = "  ";
            String FooterLinea1 = "                                                               "
                    + "                                              TOTAL: " + fBalance;

            footer[0] = new MessageFormat(FooterLinea0);
            footer[1] = new MessageFormat(FooterLinea1);

            JTable.PrintMode mode = JTable.PrintMode.FIT_WIDTH;

            TableToPrint.setShowVerticalLines(true);
            DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
            rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
            TableToPrint.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
            if (origin_print == 1) {
                TableToPrint.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
                TableToPrint.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
                //   fBalance = new_record.CalculateFinaltBalance(Con, bank_select.getText());
            }

            origin_print = 0;

            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pf = job.defaultPage();
            Paper paper = pf.getPaper();
            double margin = 20.;
            paper.setImageableArea(margin,
                    paper.getImageableY(),
                    paper.getWidth() - 2 * margin, paper.getImageableHeight());
            pf.setPaper(paper);
            paper.setImageableArea(margin,
                    paper.getImageableX(),
                    paper.getWidth() - 2 * margin, paper.getImageableHeight());
            pf.setPaper(paper);

            job.setPrintable(new MyTablePrintable(TableToPrint, mode, header, footer), job.validatePage(pf));

            job.print();
        } catch (PrinterException | SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void EditRecords(Integer NumMov, String NameBank, String NameMov) throws SQLException {
        Movements b = new Movements();
        try {
            //  EditbanksList.setListData(a.GetList(Con));
            EditMovementList.setListData(b.GetList(Con));
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        editRecordFrame.setSize(863, 468);
        editRecordFrame.setVisible(true);
        editRecordFrame.setLocationRelativeTo(null);
        editRecordFrame.toFront();
        editRecordFrame.invalidate();
        editRecordFrame.validate();
        editRecordFrame.repaint();

        Records old_record = new Records();
        Banks bn = new Banks();
        try {

            list_banks = bn.GetList(Con);
            Movements mv = new Movements();
            list_movements = mv.GetList(Con);
            Records rcd = new Records();

            old_record = old_record.GetRecord(Con, NumMov, NameBank, NameMov);

            EditNumMovementText.setText(rcd.GenerateNumMovement(Con, old_record.getBanco().getCuil(),
                    old_record.getMovimiento().getNombre()).toString());

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        numMovCurrent.setVisible(true);
        dateCargeCurrent.setVisible(true);
        detailBankCurrent.setVisible(true);
        typeMovCurrent.setVisible(true);
        valCurrent.setVisible(true);
        dateMovCurrent.setVisible(true);
        EditBankSelect.setText(bn.GetToCuil(Con, CuilSelect).getNombre());

        EditDateEnterText.setText(currentDay);
        numMovCurrent.setText(old_record.getNumero_movimiento().toString());
        dateCargeCurrent.setText(old_record.getFechaEntradaBanco());
        detailBankCurrent.setText(old_record.getDetail());
        typeMovCurrent.setText(old_record.getNombre_Movimiento());
        valCurrent.setText(Double.toString(old_record.getMonto()));
        dateMovCurrent.setText(old_record.getFechaEmision());
    }

    public void PrintTableInsertRecord() {
        try {
            if (CuilSelect != "") {
                Records rcd = new Records();
                ArrayList<String[]> ListRecordsInit;
                ListRecordsInit = rcd.ListTeenLastBanks(Con, CuilSelect);

                for (int i = 0; i < 10; i++) {
                    TableInsertRecord.setValueAt("", i, 0);
                    TableInsertRecord.setValueAt("", i, 1);
                    TableInsertRecord.setValueAt("", i, 2);
                    TableInsertRecord.setValueAt("", i, 3);
                    TableInsertRecord.setValueAt("", i, 4);
                    TableInsertRecord.setValueAt("", i, 5);

                }

                //       enter_bank_text.setText(currentDay);
                //     num_movement_text.setText(Record.GenerateNumMovement(Con,
                //           Record.getBanco().getNombre(), Record.getMovimiento().getNombre()).toString());
                if (ListRecordsInit.get(0) != null) {
                    for (int i = 0; i < ListRecordsInit.get(0).length; i++) {
                        TableInsertRecord.setValueAt(ListRecordsInit.get(0)[i], i, 0);
                        TableInsertRecord.setValueAt(ListRecordsInit.get(1)[i], i, 1);
                        TableInsertRecord.setValueAt(ListRecordsInit.get(2)[i], i, 2);
                        TableInsertRecord.setValueAt(ListRecordsInit.get(3)[i], i, 3);
                        TableInsertRecord.setValueAt(ListRecordsInit.get(4)[i], i, 4);
                        TableInsertRecord.setValueAt(ListRecordsInit.get(5)[i], i, 5);
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void printBalance(String balance) {
        String StrBalance = balance;
        if (StrBalance.indexOf(".") + 2 < StrBalance.length()) {
            StrBalance = StrBalance.substring(0, StrBalance.indexOf(".") + 3);
        }
        initBalanceText1.setText(StrBalance);
    }

    public void validCharacter(char value[], KeyEvent evt) {
        char c = evt.getKeyChar();
        boolean esNumero = false;
        //char value[] = {'0','1','2','3','4','5','6','7','8','9','.'};
        for (char x : value) {
            if (x == c) {
                esNumero = true;
                break;
            }
        }
        if (!esNumero) {
            evt.consume();
        }
    }

    public MainFrame() throws SQLException {

        initComponents();
        //datos de la ventana actual
        setLocationRelativeTo(null);
        setTitle("REGISTRO DE DATOS");

        Users user = new Users();
        _con = new conexion();
        try {
            Con = _con.obtener();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //inicio de alertas en falso
        alert_pass_new.setVisible(false);
        alert_user_new.setVisible(false);
        alert_sesion.setVisible(false);
        close_sesion.setVisible(false);

        if (!user.UserExist(Con)) {
            createUserFrame.setVisible(true);
            createUserFrame.setTitle("CREAR USUARIO");
            createUserFrame.setSize(644, 498);
            createUserFrame.toFront();
            createUserFrame.setLocationRelativeTo(null);
            createUserFrame.toFront();
            alertUserCreate.setVisible(false);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        recordFrame = new javax.swing.JFrame();
        primaryPanel = new javax.swing.JTabbedPane();
        recordPanel = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        ListRecordPanel = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        TableInsertRecord = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        save_record = new javax.swing.JButton();
        jScrollMovement1 = new javax.swing.JScrollPane();
        movementList1 = new javax.swing.JList<>();
        movement_select = new javax.swing.JLabel();
        bank_select = new javax.swing.JLabel();
        jScrollBanks1 = new javax.swing.JScrollPane();
        banksList1 = new javax.swing.JList<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        alert_mov_void = new javax.swing.JLabel();
        alert_bank_void = new javax.swing.JLabel();
        alert_value_void = new javax.swing.JLabel();
        alert_numMov_void = new javax.swing.JLabel();
        alert_detail_void = new javax.swing.JLabel();
        alert_date_void = new javax.swing.JLabel();
        edit_last_record = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        detail_text = new javax.swing.JTextField();
        value_text = new javax.swing.JTextField();
        fecha_emision_text = new javax.swing.JTextField();
        num_movement_text = new javax.swing.JTextField();
        fecha_Entrada_Text = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        bankMovementPanel = new javax.swing.JPanel();
        PanelAddBank = new javax.swing.JPanel();
        jScrollBanks = new javax.swing.JScrollPane();
        bankListCreate = new javax.swing.JList<>();
        createBank = new javax.swing.JButton();
        NewBank = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        titleAddBank = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        newCount = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        newBalance = new javax.swing.JTextField();
        alertNewBank = new javax.swing.JLabel();
        alertExistBank = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        searchPanel = new javax.swing.JPanel();
        _dateFirst = new javax.swing.JTextField();
        search_to_date = new javax.swing.JButton();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        _dateEnd = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        ListRecordPanel2 = new javax.swing.JPanel();
        jScrollPane15 = new javax.swing.JScrollPane();
        TableSearch = new javax.swing.JTable();
        dateToSearchAlert = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        finalBalanceText = new javax.swing.JLabel();
        printerToSearch = new javax.swing.JButton();
        jLabel50 = new javax.swing.JLabel();
        initBalanceText1 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        editRecordToSearch = new javax.swing.JButton();
        deleteRecord = new javax.swing.JButton();
        confirmDelete = new javax.swing.JCheckBox();
        jLabel57 = new javax.swing.JLabel();
        _datePass = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        esQuincenal = new javax.swing.JCheckBox();
        searchPanelToMov = new javax.swing.JPanel();
        jScrollMovement3 = new javax.swing.JScrollPane();
        movemenSearchtList = new javax.swing.JList<>();
        jLabel51 = new javax.swing.JLabel();
        movementSearchSelect = new javax.swing.JLabel();
        _dateFirsttoMov = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        _dateEndtoMov = new javax.swing.JTextField();
        rangeDatetoMov = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        ListRecordPanel3 = new javax.swing.JPanel();
        jScrollPane16 = new javax.swing.JScrollPane();
        TableSearchToMov = new javax.swing.JTable();
        searchToMov = new javax.swing.JButton();
        confirmDeleteToMov = new javax.swing.JCheckBox();
        deleteRecordtoMov = new javax.swing.JButton();
        editRecordtoMov = new javax.swing.JButton();
        sumValues1 = new javax.swing.JButton();
        jLabel56 = new javax.swing.JLabel();
        finalBalanceTextToMov = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        close_sesion1 = new javax.swing.JButton();
        MainTitle = new javax.swing.JLabel();
        bankAnounce = new javax.swing.JLabel();
        createUserFrame = new javax.swing.JFrame();
        jLabel5 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        new_pass_input1 = new javax.swing.JPasswordField();
        new_pass_input2 = new javax.swing.JPasswordField();
        new_user_input = new javax.swing.JTextField();
        create_user = new javax.swing.JButton();
        cancel_create_user = new javax.swing.JButton();
        alert_pass_new = new javax.swing.JLabel();
        alert_user_new = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        alertUserCreate = new javax.swing.JLabel();
        changePasswordFrame = new javax.swing.JFrame();
        jLabel10 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        cancel_reset_pass = new javax.swing.JButton();
        change_pass = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        user_exists = new javax.swing.JTextField();
        new_pass_2 = new javax.swing.JPasswordField();
        new_pass_1 = new javax.swing.JPasswordField();
        alert_pass_new_1 = new javax.swing.JLabel();
        alert_user_exists = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        editRecordFrame = new javax.swing.JFrame();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        EditRecord = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        EditDateMovementeText = new javax.swing.JTextField();
        EditValueText = new javax.swing.JTextField();
        EditNumMovementText = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        EditDateCreate = new javax.swing.JLabel();
        jScrollMovement2 = new javax.swing.JScrollPane();
        EditMovementList = new javax.swing.JList<>();
        EditMovementSelect = new javax.swing.JLabel();
        EditBankSelect = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        alertEdit = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        EditDetailText = new javax.swing.JTextField();
        EditDateEnterText = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        dateCargeCurrent = new javax.swing.JLabel();
        numMovCurrent = new javax.swing.JLabel();
        detailBankCurrent = new javax.swing.JLabel();
        typeMovCurrent = new javax.swing.JLabel();
        valCurrent = new javax.swing.JLabel();
        dateMovCurrent = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        user_input = new javax.swing.JTextField();
        pass_input = new javax.swing.JPasswordField();
        init_sesion = new javax.swing.JButton();
        changePassword = new javax.swing.JButton();
        createUser = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        alert_sesion = new javax.swing.JLabel();
        close_sesion = new javax.swing.JButton();

        recordFrame.getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        primaryPanel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        primaryPanel.setMaximumSize(new java.awt.Dimension(930, 800));
        primaryPanel.setPreferredSize(new java.awt.Dimension(930, 800));

        recordPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("ULTIMOS REGISTROS CARGADOS");

        ListRecordPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        TableInsertRecord.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableInsertRecord.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "", "", "", "", ""},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "EMISION", "Ent.BANCO", "MOV", "COMPR", "DETALLE", "MONTO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane14.setViewportView(TableInsertRecord);
        if (TableInsertRecord.getColumnModel().getColumnCount() > 0) {
            TableInsertRecord.getColumnModel().getColumn(0).setMinWidth(90);
            TableInsertRecord.getColumnModel().getColumn(0).setMaxWidth(90);
            TableInsertRecord.getColumnModel().getColumn(1).setMinWidth(90);
            TableInsertRecord.getColumnModel().getColumn(1).setMaxWidth(90);
            TableInsertRecord.getColumnModel().getColumn(2).setMinWidth(40);
            TableInsertRecord.getColumnModel().getColumn(2).setMaxWidth(40);
            TableInsertRecord.getColumnModel().getColumn(3).setMinWidth(60);
            TableInsertRecord.getColumnModel().getColumn(3).setMaxWidth(60);
            TableInsertRecord.getColumnModel().getColumn(4).setMinWidth(200);
            TableInsertRecord.getColumnModel().getColumn(4).setMaxWidth(200);
            TableInsertRecord.getColumnModel().getColumn(5).setMinWidth(100);
            TableInsertRecord.getColumnModel().getColumn(5).setMaxWidth(100);
        }

        javax.swing.GroupLayout ListRecordPanelLayout = new javax.swing.GroupLayout(ListRecordPanel);
        ListRecordPanel.setLayout(ListRecordPanelLayout);
        ListRecordPanelLayout.setHorizontalGroup(
            ListRecordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ListRecordPanelLayout.createSequentialGroup()
                .addContainerGap(155, Short.MAX_VALUE)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(175, 175, 175))
        );
        ListRecordPanelLayout.setVerticalGroup(
            ListRecordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListRecordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane14, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        save_record.setText("CARGAR REGISTROS");
        save_record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_recordActionPerformed(evt);
            }
        });
        save_record.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                save_recordKeyPressed(evt);
            }
        });

        movementList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollMovement1.setViewportView(movementList1);

        banksList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollBanks1.setViewportView(banksList1);

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("BANCO");

        jLabel16.setText("TIPO DE MOVIMIENTO");

        alert_mov_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_mov_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_mov_void.setText("<html><p>SELECIONE TIPO  <br> DE MOVIMIENTO</p></html>");
        alert_mov_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        alert_mov_void.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        alert_bank_void.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alert_bank_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_bank_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_bank_void.setText("SELECCIONE BANCO");
        alert_bank_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        alert_value_void.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alert_value_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_value_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_value_void.setText("<html><p>INGRESE MONTO  <br>   CORRECTO</p></html>");
        alert_value_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        alert_numMov_void.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alert_numMov_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_numMov_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_numMov_void.setText("<html><p>INGRESE NUMERO<br> DE MOVIMIENTO</p></html>");
        alert_numMov_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        alert_detail_void.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alert_detail_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_detail_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_detail_void.setText("INGRESE DETALLE");
        alert_detail_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        alert_date_void.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alert_date_void.setForeground(new java.awt.Color(255, 51, 0));
        alert_date_void.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_date_void.setText("<html><p>INGRESE FECHA <br> DEL MOVIMIENTO</p></html>");
        alert_date_void.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(alert_bank_void, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                    .addComponent(alert_mov_void, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(alert_numMov_void)
                    .addComponent(alert_value_void, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(alert_detail_void, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(alert_date_void))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(alert_bank_void)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alert_mov_void, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alert_numMov_void, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alert_value_void, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alert_date_void, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alert_detail_void)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        edit_last_record.setText("EDITAR ULTIMO REGISTRO");
        edit_last_record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_last_recordActionPerformed(evt);
            }
        });

        detail_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                detail_textKeyPressed(evt);
            }
        });

        value_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                value_textActionPerformed(evt);
            }
        });
        value_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                value_textKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                value_textKeyTyped(evt);
            }
        });

        fecha_emision_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fecha_emision_textActionPerformed(evt);
            }
        });
        fecha_emision_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fecha_emision_textKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fecha_emision_textKeyTyped(evt);
            }
        });

        num_movement_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                num_movement_textKeyPressed(evt);
            }
        });

        fecha_Entrada_Text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fecha_Entrada_TextKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fecha_Entrada_TextKeyTyped(evt);
            }
        });

        jLabel18.setText("NUMERO DE COMPR.");

        jLabel20.setText("FECHA EMISION");

        jLabel19.setText("MONTO");

        jLabel45.setText("DETALLE");

        jLabel21.setText("ENT. BANCO");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                            .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(num_movement_text, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(value_text, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(detail_text, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fecha_Entrada_Text, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fecha_emision_text, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(21, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(210, 210, 210))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(num_movement_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel20))
                    .addComponent(fecha_emision_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jLabel19))
                    .addComponent(value_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel45))
                    .addComponent(detail_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fecha_Entrada_Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 558, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(94, 94, 94))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bank_select, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollBanks1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(163, 163, 163)
                                .addComponent(save_record, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(edit_last_record))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollMovement1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(movement_select, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edit_last_record)
                            .addComponent(save_record, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollBanks1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollMovement1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(movement_select, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bank_select, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout recordPanelLayout = new javax.swing.GroupLayout(recordPanel);
        recordPanel.setLayout(recordPanelLayout);
        recordPanelLayout.setHorizontalGroup(
            recordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordPanelLayout.createSequentialGroup()
                .addComponent(ListRecordPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(recordPanelLayout.createSequentialGroup()
                .addGroup(recordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(recordPanelLayout.createSequentialGroup()
                        .addGap(194, 194, 194)
                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(recordPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        recordPanelLayout.setVerticalGroup(
            recordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ListRecordPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        primaryPanel.addTab("REGISTROS", recordPanel);

        bankMovementPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        PanelAddBank.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        bankListCreate.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollBanks.setViewportView(bankListCreate);

        createBank.setText("AGREGAR BANCO");
        createBank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBankActionPerformed(evt);
            }
        });

        jLabel23.setText("NOMBRE");

        titleAddBank.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        titleAddBank.setText("AGREGAR BANCO");

        jLabel46.setText("NUMERO DE CUENTA");

        jLabel48.setText("SALDO INICIAL");

        newBalance.setText("0.00");

        javax.swing.GroupLayout PanelAddBankLayout = new javax.swing.GroupLayout(PanelAddBank);
        PanelAddBank.setLayout(PanelAddBankLayout);
        PanelAddBankLayout.setHorizontalGroup(
            PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelAddBankLayout.createSequentialGroup()
                .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelAddBankLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(createBank, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(titleAddBank, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelAddBankLayout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(NewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelAddBankLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelAddBankLayout.createSequentialGroup()
                                .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(newBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelAddBankLayout.createSequentialGroup()
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(newCount, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jScrollBanks, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelAddBankLayout.setVerticalGroup(
            PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelAddBankLayout.createSequentialGroup()
                .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelAddBankLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(titleAddBank, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newCount, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PanelAddBankLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(newBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addComponent(createBank, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelAddBankLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jScrollBanks, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        alertNewBank.setForeground(new java.awt.Color(255, 51, 0));
        alertNewBank.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertNewBank.setText("INGRESE BANCO");
        alertNewBank.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        alertExistBank.setForeground(new java.awt.Color(255, 51, 0));
        alertExistBank.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertExistBank.setText("EL BANCO YA EXISTE");
        alertExistBank.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout bankMovementPanelLayout = new javax.swing.GroupLayout(bankMovementPanel);
        bankMovementPanel.setLayout(bankMovementPanelLayout);
        bankMovementPanelLayout.setHorizontalGroup(
            bankMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bankMovementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bankMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(alertNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alertExistBank, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelAddBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(199, Short.MAX_VALUE))
        );
        bankMovementPanelLayout.setVerticalGroup(
            bankMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bankMovementPanelLayout.createSequentialGroup()
                .addGroup(bankMovementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bankMovementPanelLayout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(alertNewBank, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(alertExistBank, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bankMovementPanelLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(PanelAddBank, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addContainerGap(227, Short.MAX_VALUE))
        );

        primaryPanel.addTab("BANCOS", bankMovementPanel);

        searchPanel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        searchPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        _dateFirst.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _dateFirst.setToolTipText("YYYY-MM-DD");
        _dateFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _dateFirstActionPerformed(evt);
            }
        });
        _dateFirst.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                _dateFirstKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _dateFirstKeyTyped(evt);
            }
        });
        searchPanel.add(_dateFirst, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 99, -1));

        search_to_date.setText("BUSCAR");
        search_to_date.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search_to_dateActionPerformed(evt);
            }
        });
        search_to_date.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                search_to_dateKeyPressed(evt);
            }
        });
        searchPanel.add(search_to_date, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 70, -1, -1));

        jLabel27.setText("DESDE");
        searchPanel.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 49, 99, -1));

        jLabel28.setText("HASTA");
        searchPanel.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 49, 99, -1));

        _dateEnd.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _dateEnd.setToolTipText("YYYY-MM-DD");
        _dateEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _dateEndActionPerformed(evt);
            }
        });
        _dateEnd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                _dateEndKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _dateEndKeyTyped(evt);
            }
        });
        searchPanel.add(_dateEnd, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 70, 99, -1));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel29.setText("INGRESE RENGO DE FECHAS");
        searchPanel.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(26, 11, 173, 11));

        ListRecordPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        TableSearch.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableSearch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "EMISION", "Ent.BANCO", "MOV", "COMPR", "DETALLE", "DEBE", "HABER", "SALDO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane15.setViewportView(TableSearch);
        if (TableSearch.getColumnModel().getColumnCount() > 0) {
            TableSearch.getColumnModel().getColumn(0).setMinWidth(90);
            TableSearch.getColumnModel().getColumn(0).setPreferredWidth(90);
            TableSearch.getColumnModel().getColumn(0).setMaxWidth(90);
            TableSearch.getColumnModel().getColumn(1).setMinWidth(90);
            TableSearch.getColumnModel().getColumn(1).setPreferredWidth(90);
            TableSearch.getColumnModel().getColumn(1).setMaxWidth(90);
            TableSearch.getColumnModel().getColumn(2).setMinWidth(40);
            TableSearch.getColumnModel().getColumn(2).setPreferredWidth(40);
            TableSearch.getColumnModel().getColumn(2).setMaxWidth(40);
            TableSearch.getColumnModel().getColumn(3).setMinWidth(60);
            TableSearch.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableSearch.getColumnModel().getColumn(3).setMaxWidth(60);
            TableSearch.getColumnModel().getColumn(4).setMinWidth(150);
            TableSearch.getColumnModel().getColumn(4).setPreferredWidth(150);
            TableSearch.getColumnModel().getColumn(4).setMaxWidth(150);
            TableSearch.getColumnModel().getColumn(5).setMinWidth(100);
            TableSearch.getColumnModel().getColumn(5).setPreferredWidth(100);
            TableSearch.getColumnModel().getColumn(5).setMaxWidth(100);
            TableSearch.getColumnModel().getColumn(6).setMinWidth(100);
            TableSearch.getColumnModel().getColumn(6).setPreferredWidth(100);
            TableSearch.getColumnModel().getColumn(6).setMaxWidth(100);
            TableSearch.getColumnModel().getColumn(7).setMinWidth(90);
            TableSearch.getColumnModel().getColumn(7).setMaxWidth(90);
        }

        javax.swing.GroupLayout ListRecordPanel2Layout = new javax.swing.GroupLayout(ListRecordPanel2);
        ListRecordPanel2.setLayout(ListRecordPanel2Layout);
        ListRecordPanel2Layout.setHorizontalGroup(
            ListRecordPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListRecordPanel2Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.PREFERRED_SIZE, 721, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        ListRecordPanel2Layout.setVerticalGroup(
            ListRecordPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListRecordPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE)
                .addContainerGap())
        );

        searchPanel.add(ListRecordPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, -1, -1));

        dateToSearchAlert.setForeground(new java.awt.Color(255, 0, 51));
        dateToSearchAlert.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateToSearchAlert.setText("RANGO O FORMANO NO VALIDO");
        dateToSearchAlert.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchPanel.add(dateToSearchAlert, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 33, 180, -1));

        jLabel49.setText("SALDO FINAL");
        jLabel49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchPanel.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 40, 88, -1));

        finalBalanceText.setText("0.0");
        finalBalanceText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchPanel.add(finalBalanceText, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 40, 163, -1));

        printerToSearch.setText("IMPRIMIR");
        printerToSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printerToSearchActionPerformed(evt);
            }
        });
        searchPanel.add(printerToSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 70, -1, -1));

        jLabel50.setText("SALDO INICIAL");
        jLabel50.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchPanel.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 88, -1));

        initBalanceText1.setText("0.0");
        initBalanceText1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        searchPanel.add(initBalanceText1, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 20, 163, -1));

        editRecordToSearch.setText("EDITAR REGISTRO");
        editRecordToSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRecordToSearchActionPerformed(evt);
            }
        });

        deleteRecord.setText("BORRAR REGISTRO");
        deleteRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRecordActionPerformed(evt);
            }
        });

        confirmDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(deleteRecord)
                    .addComponent(editRecordToSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(confirmDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(editRecordToSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteRecord))
                    .addComponent(confirmDelete, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        searchPanel.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(649, 11, -1, 87));

        jLabel57.setText("F. CIERRE ANT.");
        searchPanel.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 99, -1));

        _datePass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _datePass.setToolTipText("YYYY-MM-DD");
        _datePass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _datePassActionPerformed(evt);
            }
        });
        _datePass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                _datePassKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _datePassKeyTyped(evt);
            }
        });
        searchPanel.add(_datePass, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 70, 99, -1));

        jLabel55.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("ES QUINCENAL");
        searchPanel.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 100, 11));

        esQuincenal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                esQuincenalMouseClicked(evt);
            }
        });
        esQuincenal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                esQuincenalActionPerformed(evt);
            }
        });
        searchPanel.add(esQuincenal, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, -1, 30));

        primaryPanel.addTab("BUSQUEDA", searchPanel);

        movemenSearchtList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollMovement3.setViewportView(movemenSearchtList);

        jLabel51.setText("TIPO DE MOVIMIENTO");

        _dateFirsttoMov.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _dateFirsttoMov.setToolTipText("YYYY-MM-DD");
        _dateFirsttoMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _dateFirsttoMovActionPerformed(evt);
            }
        });
        _dateFirsttoMov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                _dateFirsttoMovKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _dateFirsttoMovKeyTyped(evt);
            }
        });

        jLabel52.setText("DESDE");

        jLabel53.setText("HASTA");

        _dateEndtoMov.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        _dateEndtoMov.setToolTipText("YYYY-MM-DD");
        _dateEndtoMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _dateEndtoMovActionPerformed(evt);
            }
        });
        _dateEndtoMov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                _dateEndtoMovKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _dateEndtoMovKeyTyped(evt);
            }
        });

        rangeDatetoMov.setForeground(new java.awt.Color(255, 0, 51));
        rangeDatetoMov.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rangeDatetoMov.setText("RANGO DE FECHAS NO VALIDOS");
        rangeDatetoMov.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel54.setText("INGRESE RENGO DE FECHAS");

        ListRecordPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        TableSearchToMov.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        TableSearchToMov.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "EMISION", "Ent.BANCO", "MOV", "COMPR", "DETALLE", "MONTO"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane16.setViewportView(TableSearchToMov);
        if (TableSearchToMov.getColumnModel().getColumnCount() > 0) {
            TableSearchToMov.getColumnModel().getColumn(0).setMinWidth(90);
            TableSearchToMov.getColumnModel().getColumn(0).setPreferredWidth(90);
            TableSearchToMov.getColumnModel().getColumn(0).setMaxWidth(90);
            TableSearchToMov.getColumnModel().getColumn(1).setMinWidth(90);
            TableSearchToMov.getColumnModel().getColumn(1).setPreferredWidth(90);
            TableSearchToMov.getColumnModel().getColumn(1).setMaxWidth(90);
            TableSearchToMov.getColumnModel().getColumn(2).setMinWidth(40);
            TableSearchToMov.getColumnModel().getColumn(2).setPreferredWidth(40);
            TableSearchToMov.getColumnModel().getColumn(2).setMaxWidth(40);
            TableSearchToMov.getColumnModel().getColumn(3).setMinWidth(60);
            TableSearchToMov.getColumnModel().getColumn(3).setPreferredWidth(60);
            TableSearchToMov.getColumnModel().getColumn(3).setMaxWidth(60);
            TableSearchToMov.getColumnModel().getColumn(4).setMinWidth(200);
            TableSearchToMov.getColumnModel().getColumn(4).setPreferredWidth(200);
            TableSearchToMov.getColumnModel().getColumn(4).setMaxWidth(200);
            TableSearchToMov.getColumnModel().getColumn(5).setMinWidth(100);
            TableSearchToMov.getColumnModel().getColumn(5).setPreferredWidth(100);
            TableSearchToMov.getColumnModel().getColumn(5).setMaxWidth(100);
        }

        javax.swing.GroupLayout ListRecordPanel3Layout = new javax.swing.GroupLayout(ListRecordPanel3);
        ListRecordPanel3.setLayout(ListRecordPanel3Layout);
        ListRecordPanel3Layout.setHorizontalGroup(
            ListRecordPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ListRecordPanel3Layout.createSequentialGroup()
                .addGap(128, 128, 128)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.PREFERRED_SIZE, 581, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );
        ListRecordPanel3Layout.setVerticalGroup(
            ListRecordPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
        );

        searchToMov.setText("BUSCAR");
        searchToMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchToMovActionPerformed(evt);
            }
        });
        searchToMov.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                searchToMovKeyPressed(evt);
            }
        });

        confirmDeleteToMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmDeleteToMovActionPerformed(evt);
            }
        });

        deleteRecordtoMov.setText("BORRAR REGISTRO");
        deleteRecordtoMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRecordtoMovActionPerformed(evt);
            }
        });

        editRecordtoMov.setText("EDITAR REGISTRO");
        editRecordtoMov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editRecordtoMovActionPerformed(evt);
            }
        });

        sumValues1.setText("IMPRIMIR");
        sumValues1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sumValues1ActionPerformed(evt);
            }
        });

        jLabel56.setText("SALDO FINAL");
        jLabel56.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        finalBalanceTextToMov.setText("0.0");
        finalBalanceTextToMov.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout searchPanelToMovLayout = new javax.swing.GroupLayout(searchPanelToMov);
        searchPanelToMov.setLayout(searchPanelToMovLayout);
        searchPanelToMovLayout.setHorizontalGroup(
            searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                        .addComponent(jScrollMovement3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(movementSearchSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(115, 115, 115)
                                .addComponent(searchToMov)
                                .addGap(18, 18, 18)
                                .addComponent(sumValues1))
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(_dateFirsttoMov, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(_dateEndtoMov, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(rangeDatetoMov, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(finalBalanceTextToMov, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editRecordtoMov, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(deleteRecordtoMov)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(confirmDeleteToMov))
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ListRecordPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        searchPanelToMovLayout.setVerticalGroup(
            searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(finalBalanceTextToMov))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(searchToMov)
                            .addComponent(sumValues1))
                        .addGap(71, 71, 71))
                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(editRecordtoMov)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(confirmDeleteToMov, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(deleteRecordtoMov)))
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollMovement3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rangeDatetoMov)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(searchPanelToMovLayout.createSequentialGroup()
                                        .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(searchPanelToMovLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(_dateEndtoMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(_dateFirsttoMov, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addComponent(movementSearchSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(ListRecordPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(179, 179, 179))
        );

        primaryPanel.addTab("BUSQUEDA POR MOVIMIENTOS", searchPanelToMov);

        recordFrame.getContentPane().add(primaryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 87, -1, 634));

        close_sesion1.setText("CERRAR SEISON");
        close_sesion1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_sesion1ActionPerformed(evt);
            }
        });

        MainTitle.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        MainTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        MainTitle.setText("REGISTROS LIBRERIA ALVERONI");
        MainTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        bankAnounce.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        bankAnounce.setText("BANCO:");
        bankAnounce.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(bankAnounce, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(211, 211, 211)
                .addComponent(close_sesion1, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MainTitle)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(close_sesion1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(MainTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bankAnounce, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        recordFrame.getContentPane().add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 900, 80));

        createUserFrame.setResizable(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("REGISTROS LIBRERIA ALVERONI");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("USUARIO");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("CONTRASEÑA");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("CONFIRMAR CONTRASEÑA");

        new_pass_input1.setText("123435");

        new_pass_input2.setText("53451");

        new_user_input.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        new_user_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_user_inputActionPerformed(evt);
            }
        });

        create_user.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        create_user.setText("CREAR USUARIO");
        create_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                create_userActionPerformed(evt);
            }
        });

        cancel_create_user.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cancel_create_user.setText("CANCELAR");
        cancel_create_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_create_userActionPerformed(evt);
            }
        });

        alert_pass_new.setForeground(new java.awt.Color(255, 0, 0));
        alert_pass_new.setText("<html><p>CONTRASEÑAS NO <br> COINCIDEN </p></html>");
        alert_pass_new.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        alert_user_new.setForeground(new java.awt.Color(255, 0, 0));
        alert_user_new.setText("EL USUARIO YA EXISTE");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("CREAR USUARIO");

        alertUserCreate.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        alertUserCreate.setForeground(new java.awt.Color(0, 153, 51));
        alertUserCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertUserCreate.setText("USUARIO CREADO");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(new_pass_input1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(alert_pass_new, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(new_pass_input2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel6Layout.createSequentialGroup()
                                        .addComponent(new_user_input, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(alert_user_new, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(75, 75, 75))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(alertUserCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addGap(163, 163, 163)
                                    .addComponent(create_user))
                                .addGroup(jPanel6Layout.createSequentialGroup()
                                    .addGap(181, 181, 181)
                                    .addComponent(cancel_create_user))))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(234, 234, 234)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new_user_input, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alert_user_new, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(new_pass_input2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(new_pass_input1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(alert_pass_new)))
                .addGap(18, 18, 18)
                .addComponent(create_user, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancel_create_user, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(alertUserCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout createUserFrameLayout = new javax.swing.GroupLayout(createUserFrame.getContentPane());
        createUserFrame.getContentPane().setLayout(createUserFrameLayout);
        createUserFrameLayout.setHorizontalGroup(
            createUserFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createUserFrameLayout.createSequentialGroup()
                .addGroup(createUserFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(createUserFrameLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(createUserFrameLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        createUserFrameLayout.setVerticalGroup(
            createUserFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(createUserFrameLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        changePasswordFrame.setResizable(false);

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("REGISTROS LIBRERIA ALVERONI");
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        cancel_reset_pass.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cancel_reset_pass.setText("CANCELAR");
        cancel_reset_pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_reset_passActionPerformed(evt);
            }
        });

        change_pass.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        change_pass.setText("CAMBIAR CONTRASEÑA");
        change_pass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                change_passActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("CONFIRMAR CONTRASEÑA");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("CONTRASEÑA NUEVA");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("USUARIO");

        user_exists.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        user_exists.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        user_exists.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_existsActionPerformed(evt);
            }
        });

        new_pass_2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        new_pass_2.setText("1234");
        new_pass_2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                new_pass_2ActionPerformed(evt);
            }
        });

        new_pass_1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        new_pass_1.setText("1234");

        alert_pass_new_1.setForeground(new java.awt.Color(255, 0, 0));
        alert_pass_new_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_pass_new_1.setText("<html><p>CONTRASEÑAS NO <br> COINCIDEN </p></html>");
        alert_pass_new_1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        alert_user_exists.setForeground(new java.awt.Color(255, 0, 0));
        alert_user_exists.setText("EL USUARIO NO EXISTE");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("RECUPERAR CONTRASEÑA");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(user_exists, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(alert_user_exists, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(new_pass_1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(alert_pass_new_1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(new_pass_2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(205, 205, 205)
                        .addComponent(change_pass, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(248, 248, 248)
                        .addComponent(cancel_reset_pass))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(193, 193, 193)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(user_exists, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(alert_user_exists, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(new_pass_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(new_pass_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(alert_pass_new_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(change_pass)
                .addGap(18, 18, 18)
                .addComponent(cancel_reset_pass)
                .addGap(0, 65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout changePasswordFrameLayout = new javax.swing.GroupLayout(changePasswordFrame.getContentPane());
        changePasswordFrame.getContentPane().setLayout(changePasswordFrameLayout);
        changePasswordFrameLayout.setHorizontalGroup(
            changePasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePasswordFrameLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, changePasswordFrameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );
        changePasswordFrameLayout.setVerticalGroup(
            changePasswordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePasswordFrameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        editRecordFrame.setFocusTraversalPolicyProvider(true);
        editRecordFrame.setMinimumSize(new java.awt.Dimension(863, 468));
        editRecordFrame.setResizable(false);

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("REGISTROS LIBRERIA ALVERONI");
        jLabel25.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("EDITAR REGISTRO");

        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        EditRecord.setText("EDITAR REGISTRO");
        EditRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditRecordActionPerformed(evt);
            }
        });
        EditRecord.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditRecordKeyPressed(evt);
            }
        });
        jPanel4.add(EditRecord, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 233, 219, 48));

        jLabel30.setText("ENT.BANCO");
        jPanel4.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 181, 138, -1));

        EditDateMovementeText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditDateMovementeTextActionPerformed(evt);
            }
        });
        EditDateMovementeText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditDateMovementeTextKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                EditDateMovementeTextKeyTyped(evt);
            }
        });
        jPanel4.add(EditDateMovementeText, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 132, 162, -1));

        EditValueText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditValueTextKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                EditValueTextKeyTyped(evt);
            }
        });
        jPanel4.add(EditValueText, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 56, 162, -1));

        EditNumMovementText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditNumMovementTextKeyPressed(evt);
            }
        });
        jPanel4.add(EditNumMovementText, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 11, 162, -1));

        jLabel31.setText("NUMERO DE MOVIMIENTO");
        jPanel4.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 14, 138, -1));

        jLabel32.setText("MONTO");
        jPanel4.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 59, 138, -1));

        jLabel33.setText("FECHA DE EMISION");
        jPanel4.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 135, 138, -1));

        EditDateCreate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel4.add(EditDateCreate, new org.netbeans.lib.awtextra.AbsoluteConstraints(206, 288, 152, 25));

        EditMovementList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollMovement2.setViewportView(EditMovementList);

        jPanel4.add(jScrollMovement2, new org.netbeans.lib.awtextra.AbsoluteConstraints(159, 76, 121, 93));
        jPanel4.add(EditMovementSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(159, 175, 121, 23));

        EditBankSelect.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EditBankSelect.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel4.add(EditBankSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 84, 121, 23));

        jLabel34.setText("BANCO");
        jPanel4.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(48, 56, 56, -1));

        jLabel35.setText("TIPO DE MOVIMIENTO");
        jPanel4.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(159, 48, 121, 22));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel36.setText("INFORMACION NUEVA ");
        jPanel4.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 11, 166, 37));

        alertEdit.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        alertEdit.setForeground(new java.awt.Color(255, 0, 51));
        alertEdit.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alertEdit.setText("FALTAN DATOS, CORROBORAR");
        jPanel4.add(alertEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 209, 235, 18));

        jLabel47.setText("DETALLE");
        jPanel4.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 94, 138, -1));

        EditDetailText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditDetailTextActionPerformed(evt);
            }
        });
        EditDetailText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditDetailTextKeyPressed(evt);
            }
        });
        jPanel4.add(EditDetailText, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 94, 162, -1));

        EditDateEnterText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EditDateEnterTextActionPerformed(evt);
            }
        });
        EditDateEnterText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EditDateEnterTextKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                EditDateEnterTextKeyTyped(evt);
            }
        });
        jPanel4.add(EditDateEnterText, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 178, 162, -1));

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        dateCargeCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateCargeCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        numMovCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        numMovCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        detailBankCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        detailBankCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        typeMovCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        typeMovCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        valCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        valCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        dateMovCurrent.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dateMovCurrent.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("INFORMACION ACTUAL");

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("NUMERO DE MOVMIENTO");

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("DETALLE");

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("TIPO DE MOVIMIENTO");

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("MONTO");

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("FECHA DE EMISION");

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("ENT.BANCO");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateMovCurrent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(149, 149, 149))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel42, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(detailBankCurrent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel40, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numMovCurrent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                            .addComponent(typeMovCurrent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel41, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateCargeCurrent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(valCurrent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel37)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateMovCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel44)
                            .addComponent(jLabel39))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dateCargeCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeMovCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel38)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(numMovCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel40)
                .addGap(12, 12, 12)
                .addComponent(detailBankCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valCurrent, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );

        javax.swing.GroupLayout editRecordFrameLayout = new javax.swing.GroupLayout(editRecordFrame.getContentPane());
        editRecordFrame.getContentPane().setLayout(editRecordFrameLayout);
        editRecordFrameLayout.setHorizontalGroup(
            editRecordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editRecordFrameLayout.createSequentialGroup()
                .addGap(127, 127, 127)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(editRecordFrameLayout.createSequentialGroup()
                .addGroup(editRecordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(editRecordFrameLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(editRecordFrameLayout.createSequentialGroup()
                        .addGap(224, 224, 224)
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
        );
        editRecordFrameLayout.setVerticalGroup(
            editRecordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, editRecordFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(editRecordFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("REGISTRO LIBRERIA ALVERONI");
        setMaximumSize(new java.awt.Dimension(632, 363));
        setMinimumSize(new java.awt.Dimension(632, 363));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        user_input.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        user_input.setText("usuario");
        user_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                user_inputActionPerformed(evt);
            }
        });
        user_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                user_inputKeyPressed(evt);
            }
        });
        getContentPane().add(user_input, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 132, 168, 26));

        pass_input.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        pass_input.setText("jPas");
        pass_input.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pass_inputActionPerformed(evt);
            }
        });
        pass_input.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pass_inputKeyPressed(evt);
            }
        });
        getContentPane().add(pass_input, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 164, 168, 27));

        init_sesion.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        init_sesion.setText("INGRESAR");
        init_sesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                init_sesionActionPerformed(evt);
            }
        });
        init_sesion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                init_sesionKeyPressed(evt);
            }
        });
        getContentPane().add(init_sesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(247, 202, 143, 34));

        changePassword.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        changePassword.setText("RECUPERAR CONTRASEÑA");
        changePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordActionPerformed(evt);
            }
        });
        getContentPane().add(changePassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(216, 242, -1, -1));

        createUser.setText("CREAR USUARIO");
        createUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createUserActionPerformed(evt);
            }
        });
        getContentPane().add(createUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(216, 273, 199, 38));

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("CONTRASEÑA");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 164, 95, 27));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("USUARIO");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 132, 95, 26));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("REGISTROS LIBRERIA ALVERONI");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(131, 6, 376, 50));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("INICIAR SESION");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 62, 249, 30));

        alert_sesion.setForeground(new java.awt.Color(255, 51, 0));
        alert_sesion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alert_sesion.setText("USUARIO O CONTRASEÑA INCORRECTA");
        alert_sesion.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(alert_sesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 103, 323, 23));

        close_sesion.setText("CERRAR SEISON");
        close_sesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_sesionActionPerformed(evt);
            }
        });
        getContentPane().add(close_sesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(216, 317, 199, 35));

        getAccessibleContext().setAccessibleName("mainFrame");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void init_sesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_init_sesionActionPerformed
        // TODO add your handling code here:
        Boolean user_state = FALSE;
        Users user = new Users();

        try {
            SESION_INIT = user.InitSesion(Con, user_input.getText(), pass_input.getText());
            Date expire = new SimpleDateFormat("yyyy-MM-dd").parse("2021-08-14");
            Date acutal = new Date();

            if (SESION_INIT) {

                Banks banks = new Banks();
                Movements movements = new Movements();
                list_banks = banks.GetList(Con);
                list_movements = movements.GetList(Con);

                banksList1.setListData(banks.GetList(Con));
                movementList1.setListData(list_movements);
                movemenSearchtList.setListData(list_movements);
                bankListCreate.setListData(banks.GetListWithCuil(Con));
                // movementListCreate.setListData(list_movements);
                //lista de bancos en crear registro
                banksList1.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 1) {

                            JList target = (JList) me.getSource();
                            int index = target.locationToIndex(me.getPoint());
                            if (index >= 0) {
                                Object item = target.getModel().getElementAt(index);
                                //JOptionPane.showMessageDialog(null, item.toString());

                                CuilSelect = item.toString().substring(item.toString().indexOf("-") + 1, item.toString().length());
                                bank_select.setText(item.toString().substring(0, item.toString().indexOf("-")));

                                try {

                                    Banks bankanounce = new Banks();
                                    bankanounce = bankanounce.GetToCuil(Con, CuilSelect);
                                    String anounce = "BANCO: " + bankanounce.getNombre() + " - "
                                            + bankanounce.getCuil();
                                    bankAnounce.setText(anounce);

                                    Records getRcd = new Records();

                                    bankanounce = null;

                                    PrintTableInsertRecord();

                                    if (CuilSelect != "" && MovSelection != "") {

                                        num_movement_text.setText(
                                                getRcd.GenerateNumMovement(Con,
                                                        // banks.GetToCuil(Con, bank_select.getText()).getCuil(),
                                                        CuilSelect,
                                                        movements.GetToString(Con, MovSelection).getNombre()
                                                ).toString());
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }

                            }
                        }
                    }
                });
                //lista de movimientos en crear registro
                movementList1.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 1) {

                            JList target = (JList) me.getSource();
                            int index = target.locationToIndex(me.getPoint());
                            if (index >= 0) {
                                Object item = target.getModel().getElementAt(index);
                                // JOptionPane.showMessageDialog(null, item.toString());
                                movement_select.setText(item.toString());
                                MovSelection = item.toString();
                                if (CuilSelect != "" && MovSelection != "") {
                                    Records getRcd = new Records();
                                    try {
                                        num_movement_text.setText(
                                                getRcd.GenerateNumMovement(Con,
                                                        banks.GetToCuil(Con, CuilSelect).getCuil(),
                                                        movements.GetToString(Con, MovSelection).getNombre()
                                                ).toString());
                                    } catch (SQLException ex) {
                                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    }
                });

                EditMovementList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 1) {

                            JList target = (JList) me.getSource();
                            int index = target.locationToIndex(me.getPoint());
                            if (index >= 0) {
                                Object item = target.getModel().getElementAt(index);
                                //JOptionPane.showMessageDialog(null, item.toString());
                                EditMovementSelect.setText(item.toString());
                                MovSelectionToEdit = item.toString();
                                Records getRcd = new Records();
                                try {
                                    EditNumMovementText.setText(
                                            getRcd.GenerateNumMovement(Con,
                                                    CuilSelect,
                                                    movements.GetToString(Con, MovSelectionToEdit).getNombre()
                                            ).toString());
                                } catch (SQLException ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                });

                //lista movimiento bosqueda por movimiento
                movemenSearchtList.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent me) {
                        if (me.getClickCount() == 1) {

                            JList target = (JList) me.getSource();
                            int index = target.locationToIndex(me.getPoint());
                            if (index >= 0) {
                                Object item = target.getModel().getElementAt(index);
                                // JOptionPane.showMessageDialog(null, item.toString());
                                movementSearchSelect.setText(item.toString());
                                MovSelectionToSearch = item.toString();

                            }
                        }
                    }
                });
                // selecti record creados
                TableSearch.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent event) {
                        // do some actions here, for example
                        // print first column value from selected row
                        if (TableSearch.getSelectedRow() != -1) {
                            //System.out.println(TableSearch.getValueAt(TableSearch.getSelectedRow(), 2).toString());
                            numMovementeToSearch = Integer.parseInt((String) TableSearch.getValueAt(TableSearch.getSelectedRow(), 3));
                            idMovToSearch = ((TableSearch.getValueAt(TableSearch.getSelectedRow(), 2)).toString());
                        }
                    }
                });
                TableSearchToMov.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        // do some actions here, for example
                        // print first column value from selected row
                        if (TableSearchToMov.getSelectedRow() != -1) {
                            //System.out.println(TableSearchToMov.getValueAt(TableSearchToMov.getSelectedRow(), 2).toString());
                            numMovementeToSearchToMov = Integer.parseInt((String) TableSearchToMov.getValueAt(TableSearchToMov.getSelectedRow(), 3));
                            idMovToSearchToMov = ((TableSearchToMov.getValueAt(TableSearchToMov.getSelectedRow(), 2)).toString());
                        }
                    }
                });
                //mostrar registros creados
                Records new_record = new Records();
                currentDay = new_record.CurrentDay(Con);
                fecha_Entrada_Text.setText(currentDay);
                fecha_Entrada_Text.setVisible(true);
                try {
                    if (banks.ExistBanks(Con) && CuilSelect != "") {
                        fecha_Entrada_Text.setText(currentDay);
                        fecha_Entrada_Text.setVisible(true);
                        num_movement_text.setText(
                                new_record.GenerateNumMovement(Con,
                                        // banks.GetToCuil(Con, bank_select.getText()).getCuil(),
                                        CuilSelect,
                                        movements.GetToString(Con, MovSelection).getNombre()
                                ).toString()
                        );
                        bankListCreate.setListData(banks.GetListWithCuil(Con));
                        //  movementListCreate.setListData(movements.GetList(Con));
                        PrintTableInsertRecord();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                //ListRecordsInit = null;
                recordFrame.setVisible(true);
                recordFrame.setTitle("REGISTRO");
                recordFrame.setSize(950, 800);
                recordFrame.setLocationRelativeTo(null);
                recordFrame.toFront();
                init_sesion.setVisible(false);
                close_sesion.setVisible(true);

                InitVisibleFalse();

            } else {
                alert_sesion.setVisible(true);
            }
        } catch (SQLException ex) {
        } catch (ParseException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_init_sesionActionPerformed

    private void changePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePasswordActionPerformed
        // TODO add your handling code here:
        Users user = new Users();
        try {
            if (user.UserExist(Con)) {
                alert_pass_new_1.setVisible(false);
                alert_user_exists.setVisible(false);
                changePasswordFrame.setVisible(true);
                changePasswordFrame.setTitle("CAMBIAR CONTRASEÑA");
                changePasswordFrame.setSize(651, 355);
                changePasswordFrame.setLocationRelativeTo(null);
                changePasswordFrame.toFront();

                if ((new_pass_2.getText().equals(new_pass_1.getText()) && new_pass_2.getText().equals(""))) {
                    alert_pass_new_1.setVisible(false);
                }

                if (new_user_input.getText().equals("")) {
                    alert_user_exists.setVisible(false);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_changePasswordActionPerformed

    private void createUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createUserActionPerformed
        // TODO add your handling code here:
        Users user = new Users();
        createUserFrame.setTitle("CREAR USUARIO");
        createUserFrame.setSize(644, 498);
        createUserFrame.setLocationRelativeTo(null);
        createUserFrame.toFront();
        createUserFrame.invalidate();
        createUserFrame.validate();
        createUserFrame.repaint();
        if ((new_pass_input2.getText().equals(new_pass_input1.getText()) && new_pass_input2.getText().equals(""))) {
            alert_pass_new.setVisible(false);
        }

        if (new_user_input.getText().equals("")) {
            alert_user_new.setVisible(false);
        }

        try {
            if (user.UserExist(Con)) {
                if (SESION_INIT) {
                    createUserFrame.setVisible(true);
                }
            } else {
                createUserFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_createUserActionPerformed

    private void user_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_inputActionPerformed

    private void new_user_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_user_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_new_user_inputActionPerformed

    private void create_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_create_userActionPerformed
        // TODO add your handling code here:
        Users user = new Users();
        Boolean user_state = FALSE;
        Boolean pass_state = FALSE;
        pass_state = new_pass_input2.getText().equals(new_pass_input1.getText());

        if (!new_user_input.getText().isEmpty()) {
            try {
                user_state = user.OneUserExist(Con, new_user_input.getText());
                alertUserCreate.setVisible(false);
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            alert_user_new.setVisible(true);
        }

        if (user_state) {
            alert_user_new.setVisible(true);
        }

        if ((new_pass_input2.getText().equals("") && pass_state)) {
            alert_pass_new.setVisible(true);
        }

        if (!new_pass_input2.getText().equals("") && pass_state && !user_state) {
            alert_pass_new.setVisible(false);
            alert_user_new.setVisible(false);
            try {
                user.Create(Con, new_user_input.getText(), new_pass_input1.getText());
                createUserFrame.setVisible(false);

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createUserFrame.repaint();
    }//GEN-LAST:event_create_userActionPerformed

    private void cancel_create_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_create_userActionPerformed
        // TODO add your handling code here:

        createUserFrame.setVisible(false);
        createUserFrame.dispose();

    }//GEN-LAST:event_cancel_create_userActionPerformed

    private void pass_inputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pass_inputActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pass_inputActionPerformed

    private void close_sesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_sesionActionPerformed
        try {
            // TODO add your handling code here:
            _con.cerrar(Con);

            alert_sesion.setVisible(false);
            recordFrame.setVisible(false);
            createUserFrame.setVisible(false);
            changePasswordFrame.setVisible(false);

            recordFrame.dispose();
            createUserFrame.dispose();
            changePasswordFrame.dispose();

            //System.exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_close_sesionActionPerformed

    private void cancel_reset_passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_reset_passActionPerformed
        // TODO add your handling code here:
        changePasswordFrame.setVisible(false);
        changePasswordFrame.dispose();
    }//GEN-LAST:event_cancel_reset_passActionPerformed

    private void new_pass_2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_new_pass_2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_new_pass_2ActionPerformed

    private void user_existsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_user_existsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_user_existsActionPerformed

    private void change_passActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_change_passActionPerformed
        // TODO add your handling code here: CAMBIAR CONTRASEÑA
        Users user = new Users();
        Boolean user_state = FALSE;
        Boolean pass_state = FALSE;
        pass_state = new_pass_1.getText().equals(new_pass_2.getText());

        if (!user_exists.getText().equals("")) {
            try {
                user_state = user.OneUserExist(Con, user_exists.getText());
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            alert_user_exists.setVisible(true);
        }

        if (user_state) {
            alert_user_exists.setVisible(true);
        }

        if ((new_pass_input2.getText().equals("") && pass_state)) {
            alert_pass_new.setVisible(true);
        }

        if (!new_pass_2.getText().equals("") && pass_state && user_state) {
            alert_pass_new.setVisible(false);
            alert_user_new.setVisible(false);
            try {
                user.ResetPassword(Con, user_exists.getText(), new_pass_1.getText());

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        createUserFrame.repaint();
    }//GEN-LAST:event_change_passActionPerformed

    private void save_recordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_recordActionPerformed
        // TODO add your handling code here:.
        //  Date expire = ;
        Records new_record = new Records();
        Banks banks = new Banks();
        Movements movements = new Movements();
        DateManager datecast = new DateManager();
        ArrayList<String[]> ListRecords = new ArrayList<>();
        Banks nombre_banco = null;
        double monto = -1;
        String detalle = null;
        Integer numero_movimiento = null;
        Movements tipo_movimiento = null;
        String fechaEmsion = null;
        String fecha_carga = null;
        if (!value_text.getText().isEmpty()) {
            if (pattern.matcher(value_text.getText()).matches()) {
                monto = Double.parseDouble(value_text.getText());
            }
        }
        if ((MovSelection != "" && num_movement_text.getText() != "")) {
            numero_movimiento = Integer.parseInt(num_movement_text.getText());
        }
        fechaEmsion = fecha_emision_text.getText();
        if (datecast.isDateValid(fechaEmsion)) {

            fechaEmsion = datecast.DateToDB(fecha_emision_text.getText());
        } else {
            fechaEmsion = "";
        }
        fecha_carga = fecha_Entrada_Text.getText();
        if (datecast.isDateValid(fecha_carga)) {

            fecha_carga = datecast.DateToDB(fecha_Entrada_Text.getText());
        } else {
            fecha_carga = "";
        }
        if (!detail_text.getText().isEmpty()) {
            detalle = detail_text.getText();
        }
        try {

            nombre_banco = banks.GetToCuil(Con, CuilSelect);
            tipo_movimiento = movements.GetToString(Con, MovSelection);

            if (nombre_banco == null) {
                alert_bank_void.setVisible(true);
            } else {
                alert_bank_void.setVisible(false);
            }

            if (detalle == null) {
                alert_detail_void.setVisible(true);
            } else {
                alert_detail_void.setVisible(false);
            }

            if (tipo_movimiento == null) {
                alert_mov_void.setVisible(true);
            } else {
                alert_mov_void.setVisible(false);
            }
            if (monto == -1) {
                alert_value_void.setVisible(true);
            } else {
                alert_value_void.setVisible(false);
            }

            if ("".equals(fecha_carga)) {
                alert_date_void.setVisible(true);
            } else {
                alert_date_void.setVisible(false);
            }

            if ("".equals(fechaEmsion)) {
                alert_date_void.setVisible(true);
            } else {
                alert_date_void.setVisible(false);
            }

            if (numero_movimiento == null) {
                alert_numMov_void.setVisible(true);
            } else if (tipo_movimiento != null) {
                if (tipo_movimiento.isUnique(Con, numero_movimiento,
                        banks.GetToCuil(Con, CuilSelect).getCuil(),
                        movements.GetToString(Con, MovSelection).getNombre())) {

                    alert_numMov_void.setVisible(false);
                } else {
                    numero_movimiento = null;
                    alert_numMov_void.setVisible(true);
                }
            } else {
                alert_numMov_void.setVisible(false);
            }

            if (nombre_banco != null && monto != -1 && numero_movimiento != null && detalle != null
                    && tipo_movimiento != null && !"".equals(fechaEmsion) && !"".equals(fecha_carga)) {

                new_record.Create(Con, nombre_banco, monto, numero_movimiento, detalle,
                        tipo_movimiento, fechaEmsion, fecha_carga, nombre_banco.getCuil(), tipo_movimiento.getNombre());

                new_record = new_record.GetRecord(Con, numero_movimiento, nombre_banco.getCuil(), tipo_movimiento.getNombre());

                ListRecords = new_record.ListTeenLastBanks(Con, nombre_banco.getCuil());

                if (nombre_banco != null) {
                    fecha_Entrada_Text.setText(currentDay);
                    num_movement_text.setText(new_record.GenerateNumMovement(Con, nombre_banco.getCuil(), tipo_movimiento.getNombre()).toString());
                }

                value_text.setText("");
                detail_text.setText("");
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        PrintTableInsertRecord();
        ListRecords.clear();
        ListRecords = null;

    }//GEN-LAST:event_save_recordActionPerformed

    private void createBankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBankActionPerformed
        Banks bank = new Banks();
        Records record_q = new Records();
        String new_cuil, new_bank = "";
        Double new_balance = 0.00;
        new_bank = NewBank.getText();
        new_cuil = newCount.getText();
        new_balance = Double.parseDouble(newBalance.getText());

        if (!new_cuil.isEmpty()) {
            try {
                if (bank.isUnique(Con, new_cuil)) {
                    bank.Create(Con, new_bank, new_cuil, new_balance);

                    record_q.CreateLogRec(Con, bank.GetToCuil(Con, new_cuil).getPk_id());

                    NewBank.setText("");
                    newBalance.setText("0.00");
                    newCount.setText("");
                    alertExistBank.setVisible(false);
                    alertNewBank.setVisible(false);

                } else {
                    alertExistBank.setVisible(true);
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            alertExistBank.setVisible(false);
            alertNewBank.setVisible(true);
        }
        try {

            bankListCreate.setListData(bank.GetListWithCuil(Con));
            banksList1.setListData(bank.GetList(Con));

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_createBankActionPerformed

    private void _dateEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__dateEndActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__dateEndActionPerformed

    private void printerToSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printerToSearchActionPerformed
        if (TableSearch.getRowCount() > 0) {
            origin_print = 1;
            PrintTables(TableSearch);
        }
    }//GEN-LAST:event_printerToSearchActionPerformed

    private void search_to_dateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_search_to_dateActionPerformed
        // TODO add your handling code here:
        if (CuilSelect != "") {
            String date_1 = _dateFirst.getText();
            String date_2 = _dateEnd.getText();
            String bank = CuilSelect;
            try {
                printTableSearch(date_1, date_2, bank);
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        numMovementeToSearch = null;
    }//GEN-LAST:event_search_to_dateActionPerformed

    private void _dateFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__dateFirstActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__dateFirstActionPerformed

    private void close_sesion1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_sesion1ActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            _con.cerrar(Con);

            alert_sesion.setVisible(false);
            recordFrame.setVisible(false);
            createUserFrame.setVisible(false);
            changePasswordFrame.setVisible(false);

            recordFrame.dispose();
            createUserFrame.dispose();
            changePasswordFrame.dispose();

            //System.exit(0);
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_close_sesion1ActionPerformed

    private void deleteRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRecordActionPerformed
        // TODO add your handling code here:
        Records new_record = new Records();

        boolean confirm = confirmDelete.isSelected();
        if (confirm && numMovementeToSearch != null) {
            String date_1 = _dateFirst.getText();
            String date_2 = _dateEnd.getText();
            try {
                new_record = new_record.GetRecord(Con, numMovementeToSearch, CuilSelect,
                        idMovToSearch);
                new_record.DeleteRecord(Con, new_record);
                printTableSearch(date_1, date_2,
                        CuilSelect);

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {

                num_movement_text.setText((new_record.GenerateNumMovement(Con, CuilSelect, idMovToSearch).toString()));
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            confirmDelete.setSelected(false);
            numMovementeToSearch = null;
        }

    }//GEN-LAST:event_deleteRecordActionPerformed

    private void editRecordToSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRecordToSearchActionPerformed
        // TODO add your handling code here:
        //   TableInsertRecord.getModel().getValueAt(0, 1);
        if (numMovementeToSearch != null) {
            origin_edit = 1;

            try {
                Banks bn = new Banks();
                list_banks = bn.GetList(Con);
                Movements mv = new Movements();
                list_movements = mv.GetList(Con);
                list_banks = bn.GetList(Con);
                //    Movements mv = new Movements();
                list_movements = mv.GetList(Con);
                Records rcd = new Records();
                rcd = rcd.GetRecord(
                        Con,
                        numMovementeToSearch,
                        // bn.GetToCuil(Con, bank_select.getText()).getCuil(),
                        CuilSelect,
                        //mv.GetToString(Con, idMovToSearch).getNombre()
                        idMovToSearch
                );

                //EditRecords (Integer NumMov, String NameBank, String  NameMov)
                EditRecords(rcd.getNumero_movimiento(),
                        CuilSelect,
                        rcd.getMovimiento().getNombre());

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_editRecordToSearchActionPerformed

    private void confirmDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmDeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirmDeleteActionPerformed

    private void EditRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditRecordActionPerformed
        Banks bn = new Banks();
        Records rcd = new Records();
        Movements mv = new Movements();
        try {
            list_banks = bn.GetList(Con);

            list_movements = mv.GetList(Con);

        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        String numMovToSearch, name_bankc, name_mov;

        switch (origin_edit) {
            case 1:
                try {
                    //search record

                    SaveEditRecord(numMovementeToSearch,
                            CuilSelect,
                            idMovToSearch);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 2:
                try {
                    // save record
                    SaveEditRecord(
                            Integer.parseInt(TableInsertRecord.getModel().getValueAt(0, 3).toString()),
                            CuilSelect,
                            TableInsertRecord.getModel().getValueAt(0, 2).toString()
                    );
                    //      PrintTableInsertRecord();

                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case 3:
                try {
                    //serach to mov
                    SaveEditRecord(
                            Integer.parseInt(TableSearchToMov.getModel().getValueAt(0, 3).toString()),
                            CuilSelect,
                            TableSearchToMov.getModel().getValueAt(0, 2).toString()
                    );
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            default:
                break;
        }

        origin_edit = 0;
        // TODO add your handling code here:
    }//GEN-LAST:event_EditRecordActionPerformed

    private void EditDateMovementeTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditDateMovementeTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditDateMovementeTextActionPerformed

    private void fecha_emision_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fecha_emision_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fecha_emision_textActionPerformed

    private void edit_last_recordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_last_recordActionPerformed
        // TODO add your handling code here:
        TableInsertRecord.getModel().getValueAt(0, 1);
        if (TableInsertRecord.getModel().getValueAt(0, 2).toString() != "") {
            origin_edit = 2;

            try {
                Banks bn = new Banks();
                list_banks = bn.GetList(Con);
                Movements mv = new Movements();
                list_movements = mv.GetList(Con);

                EditRecords(Integer.parseInt(TableInsertRecord.getModel().getValueAt(0, 3).toString()),
                        CuilSelect,
                        mv.GetToString(Con, (TableInsertRecord.getModel().getValueAt(0, 2).toString())).getNombre());

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_edit_last_recordActionPerformed

    private void EditDetailTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditDetailTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditDetailTextActionPerformed

    private void _dateFirsttoMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__dateFirsttoMovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__dateFirsttoMovActionPerformed

    private void _dateEndtoMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__dateEndtoMovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event__dateEndtoMovActionPerformed

    private void sumValues1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sumValues1ActionPerformed
        if (TableSearchToMov.getRowCount() != 0) {
            origin_print = 2;
            PrintTables(TableSearchToMov);
        }
    }//GEN-LAST:event_sumValues1ActionPerformed

    private void searchToMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchToMovActionPerformed
        // TODO add your handling code here:
        if (CuilSelect != "") {
            String date_1 = _dateFirsttoMov.getText();
            String date_2 = _dateEndtoMov.getText();
            String bank = CuilSelect;
            try {
                printTableSearchToMov(date_1, date_2, bank);
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        numMovementeToSearch = null;

    }//GEN-LAST:event_searchToMovActionPerformed

    private void confirmDeleteToMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmDeleteToMovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_confirmDeleteToMovActionPerformed

    private void deleteRecordtoMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRecordtoMovActionPerformed
        // TODO add your handling code here:
        Records new_record = new Records();
        Banks bank = new Banks();
        Integer mov = -1;
        boolean confirm = confirmDeleteToMov.isSelected();
        if (confirm && numMovementeToSearchToMov != null) {
            String date_1 = _dateFirsttoMov.getText();
            String date_2 = _dateEndtoMov.getText();
            try {
                new_record = new_record.GetRecord(Con, numMovementeToSearchToMov, CuilSelect,
                        idMovToSearchToMov);
                new_record.DeleteRecord(Con, new_record);
                printTableSearchToMov(date_1, date_2,
                        CuilSelect);

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {

                num_movement_text.setText((new_record.GenerateNumMovement(Con, CuilSelect, idMovToSearchToMov).toString()));
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            confirmDeleteToMov.setSelected(false);
            numMovementeToSearchToMov = null;
        }
    }//GEN-LAST:event_deleteRecordtoMovActionPerformed

    private void editRecordtoMovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editRecordtoMovActionPerformed
        // TODO add your handling code here:
        if (numMovementeToSearchToMov != null) {
            origin_edit = 3;
            Banks bn = new Banks();
            try {

                list_banks = bn.GetList(Con);
                Movements mv = new Movements();
                list_movements = mv.GetList(Con);
                //EditRecords (Integer NumMov, String NameBank, String  NameMov)
                //numMovementeToSearchToMov = Integer. parseInt((String) TableSearchToMov.getValueAt(TableSearchToMov.getSelectedRow(), 3));
                //idMovToSearchToMov = ((TableSearchToMov.getValueAt(TableSearchToMov.getSelectedRow(), 2)).toString());
                EditRecords(numMovementeToSearchToMov,
                        CuilSelect,
                        idMovToSearchToMov);

            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_editRecordtoMovActionPerformed

    private void EditDateEnterTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditDateEnterTextActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EditDateEnterTextActionPerformed

    private void value_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_value_textActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_value_textActionPerformed

    private void value_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_value_textKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            detail_text.requestFocus();
        }
    }//GEN-LAST:event_value_textKeyPressed

    private void value_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_value_textKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
        validCharacter(value, evt);
    }//GEN-LAST:event_value_textKeyTyped

    private void fecha_emision_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fecha_emision_textKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (fecha_emision_text.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }

    }//GEN-LAST:event_fecha_emision_textKeyTyped

    private void fecha_Entrada_TextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fecha_Entrada_TextKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (fecha_Entrada_Text.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_fecha_Entrada_TextKeyTyped

    private void _dateFirstKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateFirstKeyTyped
        // TODO add your handling code here:
        DateManager dateForm = new DateManager();
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (_dateFirst.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            try {
                _datePass.setText(dateForm.lastDayMonth(_dateFirst.getText(), esQuincenal.isSelected()));
            } catch (ParseException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            evt.consume();
        }
        dateForm = null;
    }//GEN-LAST:event__dateFirstKeyTyped

    private void _dateEndKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateEndKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (_dateEnd.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event__dateEndKeyTyped

    private void _dateFirsttoMovKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateFirsttoMovKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (_dateFirsttoMov.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event__dateFirsttoMovKeyTyped

    private void _dateEndtoMovKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateEndtoMovKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (_dateEndtoMov.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event__dateEndtoMovKeyTyped

    private void EditDateEnterTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditDateEnterTextKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (EditDateEnterText.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_EditDateEnterTextKeyTyped

    private void EditDateMovementeTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditDateMovementeTextKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (EditDateMovementeText.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event_EditDateMovementeTextKeyTyped

    private void EditValueTextKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditValueTextKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
        validCharacter(value, evt);
    }//GEN-LAST:event_EditValueTextKeyTyped

    private void detail_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_detail_textKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            fecha_Entrada_Text.requestFocus();
        }
    }//GEN-LAST:event_detail_textKeyPressed

    private void fecha_Entrada_TextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fecha_Entrada_TextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            save_record.requestFocus();
        }
    }//GEN-LAST:event_fecha_Entrada_TextKeyPressed

    private void save_recordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_save_recordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                    // for enter key
            save_record.doClick();
            num_movement_text.requestFocus();
        }
    }//GEN-LAST:event_save_recordKeyPressed

    private void num_movement_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_num_movement_textKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            fecha_emision_text.requestFocus();
        }
    }//GEN-LAST:event_num_movement_textKeyPressed

    private void fecha_emision_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fecha_emision_textKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            value_text.requestFocus();
        }
    }//GEN-LAST:event_fecha_emision_textKeyPressed

    private void EditNumMovementTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditNumMovementTextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditValueText.requestFocus();
        }
    }//GEN-LAST:event_EditNumMovementTextKeyPressed

    private void EditValueTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditValueTextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditDetailText.requestFocus();
        }
    }//GEN-LAST:event_EditValueTextKeyPressed

    private void EditDetailTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditDetailTextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditDateMovementeText.requestFocus();
        }
    }//GEN-LAST:event_EditDetailTextKeyPressed

    private void EditDateMovementeTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditDateMovementeTextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditDateEnterText.requestFocus();
        }
    }//GEN-LAST:event_EditDateMovementeTextKeyPressed

    private void EditDateEnterTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditDateEnterTextKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditRecord.requestFocus();
        }
    }//GEN-LAST:event_EditDateEnterTextKeyPressed

    private void EditRecordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EditRecordKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {                     // for enter key
            EditRecord.doClick();
            EditNumMovementText.requestFocus();
        }
    }//GEN-LAST:event_EditRecordKeyPressed

    private void esQuincenalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_esQuincenalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_esQuincenalActionPerformed

    private void _dateFirstKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateFirstKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            _dateEnd.requestFocus();
        }
    }//GEN-LAST:event__dateFirstKeyPressed

    private void _dateEndKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateEndKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            _datePass.requestFocus();
        }
    }//GEN-LAST:event__dateEndKeyPressed

    private void search_to_dateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_search_to_dateKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            search_to_date.doClick();
        }
    }//GEN-LAST:event_search_to_dateKeyPressed

    private void _dateFirsttoMovKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateFirsttoMovKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            _dateEndtoMov.requestFocus();
        }
    }//GEN-LAST:event__dateFirsttoMovKeyPressed

    private void _dateEndtoMovKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__dateEndtoMovKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            searchToMov.requestFocus();
        }
    }//GEN-LAST:event__dateEndtoMovKeyPressed

    private void searchToMovKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchToMovKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            searchToMov.doClick();
        }
    }//GEN-LAST:event_searchToMovKeyPressed

    private void user_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_user_inputKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            pass_input.requestFocus();
        }
    }//GEN-LAST:event_user_inputKeyPressed

    private void pass_inputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pass_inputKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            init_sesion.requestFocus();
        }
    }//GEN-LAST:event_pass_inputKeyPressed

    private void init_sesionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_init_sesionKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            init_sesion.doClick();
        }
    }//GEN-LAST:event_init_sesionKeyPressed

    private void _datePassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__datePassActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event__datePassActionPerformed

    private void _datePassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__datePassKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == 10) {
            search_to_date.requestFocus();
        }
    }//GEN-LAST:event__datePassKeyPressed

    private void _datePassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__datePassKeyTyped
        // TODO add your handling code here:
        char value[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-'};
        if (_datePass.getText().length() < 10) {
            validCharacter(value, evt);
        } else {
            evt.consume();
        }
    }//GEN-LAST:event__datePassKeyTyped

    private void esQuincenalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_esQuincenalMouseClicked
        // TODO add your handling code here:
        DateManager dateForm = new DateManager();
        if (_dateFirst.getText().length() == 10) {
            try {
                _datePass.setText(dateForm.lastDayMonth(_dateFirst.getText(), esQuincenal.isSelected()));
            } catch (ParseException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        dateForm = null;
    }//GEN-LAST:event_esQuincenalMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainFrame().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EditBankSelect;
    private javax.swing.JLabel EditDateCreate;
    private javax.swing.JTextField EditDateEnterText;
    private javax.swing.JTextField EditDateMovementeText;
    private javax.swing.JTextField EditDetailText;
    private javax.swing.JList<String> EditMovementList;
    private javax.swing.JLabel EditMovementSelect;
    private javax.swing.JTextField EditNumMovementText;
    private javax.swing.JButton EditRecord;
    private javax.swing.JTextField EditValueText;
    private javax.swing.JPanel ListRecordPanel;
    private javax.swing.JPanel ListRecordPanel2;
    private javax.swing.JPanel ListRecordPanel3;
    private javax.swing.JLabel MainTitle;
    private javax.swing.JTextField NewBank;
    private javax.swing.JPanel PanelAddBank;
    private javax.swing.JTable TableInsertRecord;
    private javax.swing.JTable TableSearch;
    private javax.swing.JTable TableSearchToMov;
    private javax.swing.JTextField _dateEnd;
    private javax.swing.JTextField _dateEndtoMov;
    private javax.swing.JTextField _dateFirst;
    private javax.swing.JTextField _dateFirsttoMov;
    private javax.swing.JTextField _datePass;
    private javax.swing.JLabel alertEdit;
    private javax.swing.JLabel alertExistBank;
    private javax.swing.JLabel alertNewBank;
    private javax.swing.JLabel alertUserCreate;
    private javax.swing.JLabel alert_bank_void;
    private javax.swing.JLabel alert_date_void;
    private javax.swing.JLabel alert_detail_void;
    private javax.swing.JLabel alert_mov_void;
    private javax.swing.JLabel alert_numMov_void;
    private javax.swing.JLabel alert_pass_new;
    private javax.swing.JLabel alert_pass_new_1;
    private javax.swing.JLabel alert_sesion;
    private javax.swing.JLabel alert_user_exists;
    private javax.swing.JLabel alert_user_new;
    private javax.swing.JLabel alert_value_void;
    private javax.swing.JLabel bankAnounce;
    private javax.swing.JList<String> bankListCreate;
    private javax.swing.JPanel bankMovementPanel;
    private javax.swing.JLabel bank_select;
    private javax.swing.JList<String> banksList1;
    private javax.swing.JButton cancel_create_user;
    private javax.swing.JButton cancel_reset_pass;
    private javax.swing.JButton changePassword;
    private javax.swing.JFrame changePasswordFrame;
    private javax.swing.JButton change_pass;
    private javax.swing.JButton close_sesion;
    private javax.swing.JButton close_sesion1;
    private javax.swing.JCheckBox confirmDelete;
    private javax.swing.JCheckBox confirmDeleteToMov;
    private javax.swing.JButton createBank;
    private javax.swing.JButton createUser;
    private javax.swing.JFrame createUserFrame;
    private javax.swing.JButton create_user;
    private javax.swing.JLabel dateCargeCurrent;
    private javax.swing.JLabel dateMovCurrent;
    private javax.swing.JLabel dateToSearchAlert;
    private javax.swing.JButton deleteRecord;
    private javax.swing.JButton deleteRecordtoMov;
    private javax.swing.JLabel detailBankCurrent;
    private javax.swing.JTextField detail_text;
    private javax.swing.JFrame editRecordFrame;
    private javax.swing.JButton editRecordToSearch;
    private javax.swing.JButton editRecordtoMov;
    private javax.swing.JButton edit_last_record;
    private javax.swing.JCheckBox esQuincenal;
    private javax.swing.JTextField fecha_Entrada_Text;
    private javax.swing.JTextField fecha_emision_text;
    private javax.swing.JLabel finalBalanceText;
    private javax.swing.JLabel finalBalanceTextToMov;
    private javax.swing.JLabel initBalanceText1;
    private javax.swing.JButton init_sesion;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollBanks;
    private javax.swing.JScrollPane jScrollBanks1;
    private javax.swing.JScrollPane jScrollMovement1;
    private javax.swing.JScrollPane jScrollMovement2;
    private javax.swing.JScrollPane jScrollMovement3;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JList<String> movemenSearchtList;
    private javax.swing.JList<String> movementList1;
    private javax.swing.JLabel movementSearchSelect;
    private javax.swing.JLabel movement_select;
    private javax.swing.JTextField newBalance;
    private javax.swing.JTextField newCount;
    private javax.swing.JPasswordField new_pass_1;
    private javax.swing.JPasswordField new_pass_2;
    private javax.swing.JPasswordField new_pass_input1;
    private javax.swing.JPasswordField new_pass_input2;
    private javax.swing.JTextField new_user_input;
    private javax.swing.JLabel numMovCurrent;
    private javax.swing.JTextField num_movement_text;
    private javax.swing.JPasswordField pass_input;
    private javax.swing.JTabbedPane primaryPanel;
    private javax.swing.JButton printerToSearch;
    private javax.swing.JLabel rangeDatetoMov;
    private javax.swing.JFrame recordFrame;
    private javax.swing.JPanel recordPanel;
    private javax.swing.JButton save_record;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JPanel searchPanelToMov;
    private javax.swing.JButton searchToMov;
    private javax.swing.JButton search_to_date;
    private javax.swing.JButton sumValues1;
    private javax.swing.JLabel titleAddBank;
    private javax.swing.JLabel typeMovCurrent;
    private javax.swing.JTextField user_exists;
    private javax.swing.JTextField user_input;
    private javax.swing.JLabel valCurrent;
    private javax.swing.JTextField value_text;
    // End of variables declaration//GEN-END:variables
}
