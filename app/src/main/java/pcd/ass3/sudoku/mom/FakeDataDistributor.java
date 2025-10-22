


package pcd.ass3.sudoku.mom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FakeDataDistributor implements DataDistributor {

  private final List<Map<String, String>> boards;
  private SharedDataListener dataListener;

  public FakeDataDistributor() {
    this.boards = new ArrayList<>();
    var boardDetails = Map.of("name", "board-1", "author", "teo");
    this.boards.add(boardDetails);
  }

    @Override
    public void init(SharedDataListener controller) {
      this.dataListener = controller;
    }

    @Override
    public void registerBoard(JsonData boarData) {
      dataListener.notifyErrors("registerBoard", null);
    }

    @Override
    public void subscribe(String nickname, String boardName) {
      dataListener.notifyErrors("joinBoard", null);
    }

    @Override
    public void unsubscribe() {
      dataListener.notifyErrors("leaveBoard", null);
    }

    @Override
    public void shareUpdate(JsonData edits) {
      dataListener.notifyErrors("shareUpdate", null);
    }

    @Override
    public void updateCursor(JsonData userInfo) {
      dataListener.notifyErrors("updateCursor", null);

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