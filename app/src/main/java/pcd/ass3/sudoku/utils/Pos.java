package pcd.ass3.sudoku.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PosDeserializer.StdPairDeserializer.class)
public record Pos(int x, int y){};
