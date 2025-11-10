package pcd.ass3.sudoku.domain;

import de.sfuhrm.sudoku.Creator;
import de.sfuhrm.sudoku.GameMatrix;
import de.sfuhrm.sudoku.GameSchemas;
import de.sfuhrm.sudoku.Riddle;


public class SudokuGenerator {

    /**
     * Generate complete sudoku board and the riddle.
     * @return pair of int[][], respectively the complete and the riddle board.
     */
    static public SudokuBoard generate() {
        GameMatrix matrix = Creator.createFull(GameSchemas.SCHEMA_9X9);
        Riddle riddle = Creator.createRiddle(matrix);
        var riddleArray = convert(riddle.getArray());
        int[][] complete = convert(matrix.getArray());
        return new SudokuBoard(complete, riddleArray);
    }

    static private int[][] convert(byte[][] byteArray) {
        int cols = byteArray[0].length;
        int rows = byteArray.length;
        int [][] intArray = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j= 0; j < cols; j++) {
                intArray[i][j] = byteArray[i][j];
            }
        }
        return intArray;
    }
}