package libreria;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Libreria {

    public static void main(String ar[]) throws ParseException, IOException {

        char matriz[][] = new char[5][5];

        Scanner entradaEscaner = new Scanner(System.in);

        System.out.println("matriz 5x5\n");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                if (i == j) {
                    matriz[i][j] = 'x';
                } else {
                    matriz[i][j] = '.';
                }
                System.out.print("\u001B[1m" +matriz[i][j]);
            }
            System.out.print("\n");
        }
        /* for(int i=0;i<matriz.length;i++){
            for(int j=0;j<matriz[0].length;j++){
                System.out.print(matriz[i][j]);
            }
             System.out.print("\n");
        }*/
    }
}
