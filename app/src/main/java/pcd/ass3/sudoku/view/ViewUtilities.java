package pcd.ass3.sudoku.view;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class ViewUtilities {

    public static Border makeBorder(int row, int col) {
        int top = (row % 3 == 0) ? 3 : 1;
        int left = (col % 3 == 0) ? 3 : 1;
        int bottom = (row == 8) ? 3 : 1;
        int right = (col == 8) ? 3 : 1;
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK);
    }

}