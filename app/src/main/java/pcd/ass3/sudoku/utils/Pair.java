package pcd.ass3.sudoku.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PairDeserializer.StdPairDeserializer.class)
public record Pair(int x, int y){};
