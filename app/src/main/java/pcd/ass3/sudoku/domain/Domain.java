package pcd.ass3.sudoku.domain;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public record CellUpdate (Pos cellPos, String cellValue){
        public String toJson() {
        return gson.toJson(this);
        }
        public static CellUpdate fromJson(String json) {
        return gson.fromJson(json, CellUpdate.class);
        }
    }

    public record BoardInfo(
        Map<Pos, Integer> board,
        String createdBy,
        String name
    ){
        public String toJson() {
        return gson.toJson(this);
        }
        public static BoardInfo fromJson(String json) {
        return gson.fromJson(json, BoardInfo.class);
        }
    }
}
