package pcd.ass3.sudoku.utils;

import java.io.Serializable;

public record Pair<X, Y>(X x, Y y) implements Serializable{};
