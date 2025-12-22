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

    /**
     * Return new array keeping only cells tha correpsond to mask values without value.
     * Like deepCopy but excluding cell when mask contains some value.
     */
    public static int[][] extractMask(int[][] mask, int[][] arr) {
        if (arr == null) {
            return null;
        }
       
        int rows = arr.length;
        int cols = arr[0].length;
        int[][] copy = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                copy[r][c] = mask[r][c] == 0 ? arr[r][c] : 0;
            }
        }
        return copy;
    }

    public static void main(String[] args) {
        int[][] mask = {{1, 2, 3}, {3, 0, 0}};
        int[][] arr = {{1, 2, 3}, {3, 30, 300}};
        System.out.println(arrayToString(extractMask(mask, arr)));
    
    }
}