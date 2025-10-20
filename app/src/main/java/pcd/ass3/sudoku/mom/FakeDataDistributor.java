


package pcd.ass3.sudoku.mom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FakeDataDistributor implements DataDistributor {

  private List<Map<String, String>> boards;

  public FakeDataDistributor() {
    this.boards = new ArrayList<>();
    var boardDetails = Map.of("name", "board-1", "author", "teo");
      this.boards.add(boardDetails);
  }

    @Override
    public void init(SharedDataListener controller) {
    }

    @Override
    public void registerBoard(JsonData boarData) {
    }

    @Override
    public void joinBoard(String nickname, String boardName) {
    }

    @Override
    public void leaveBoard() {
    }

    @Override
    public void shareUpdate(JsonData edits) {
    }

    @Override
    public void updateCursor(JsonData userInfo) {
    }

    @Override
    public List<JsonData> existingBoards() {
      return this.boards.stream().map(e -> 
      (JsonData) new JsonData() {
        @Override
        public String getJsonString() {
          return  e.toString();
        }
      }).toList();
    }

}