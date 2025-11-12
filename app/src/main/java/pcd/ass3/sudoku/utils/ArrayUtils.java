package pcd.ass3.sudoku.utils;


public class ArrayUtils {
    public static String arrayToString(int[][] arr) {
        String str = "";
        int rows = arr.length;
        int cols = arr[0].length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                str += arr[r][c] + " ";
            }
            str += "\n"; 
        }
        return str;
    }

    public static int[][] deepCopy(int[][] original) {
            if (original == null) {
                return null;
            }
            int[][] copy = new int[original.length][];
            for (int i = 0; i < original.length; i++) {
                copy[i] = original[i].clone(); // clones inner arrays individually
            }
            return copy;
        }
}