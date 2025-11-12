package pcd.ass3.sudoku.domain;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static pcd.ass3.sudoku.utils.ArrayUtils.arrayToString;
import static pcd.ass3.sudoku.utils.ArrayUtils.deepCopy;

public interface Domain {
    Gson gson = new GsonBuilder().create();

    public record UserInfo(String nickname, String hexColor, Pos cursorPos){
        public String toJson() {
        return gson.toJson(this);
        }
        public static UserInfo fromJson(String json) {
        return gson.fromJson(json, UserInfo.class);
        }
    }

    public record UserEdit(String nickname, CellUpdate edits){
        public String toJson() {
        return gson.toJson(this);
        }
        public static UserEdit fromJson(String json) {
        return gson.fromJson(json, UserEdit.class);
        }
    }

    public record CellUpdate (Pos cellPos, int cellValue){
        public String toJson() {
        return gson.toJson(this);
        }
        public static CellUpdate fromJson(String json) {
        return gson.fromJson(json, CellUpdate.class);
        }
    }

    public record BoardInfo(
        int[][] riddle,
        int[][] solution,
        String createdBy,
        String name
    ){
        public String toJson() {
        return gson.toJson(this);
        }
        public static BoardInfo fromJson(String json) {
        return gson.fromJson(json, BoardInfo.class);
        }

        @Override
        public String toString() {
            return "createdBy: " + createdBy() + "\n" +
            "name: " + name() + "\n"+
            "riddle: " + "\n" +
            arrayToString(riddle()) + "\n" +
            "solution: " + "\n" +
            arrayToString(solution());
        }

        @Override
        public int[][] solution() {
            // return copy
            return deepCopy(this.solution);
        }

        @Override
        public int[][] riddle() {
            return deepCopy(this.riddle);
        }
    }
}
