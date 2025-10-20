


package pcd.ass3.sudoku.mom;

import java.util.List;
import java.util.Map;


class FakeDataDistributor implements DataDistributor {

  private List<Map<String, String>> boards;

    @Override
    public void init(SharedDataListener controller) {
    }

    @Override
    public void registerBoard(JsonData boarData) {
      var boardDetails = Map.of("name", "board-1", "author", "teo");
      this.boards.add(boardDetails);
    }

    @Override
    public void joinBoard(String nickname, String boardName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void leaveBoard() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shareUpdate(JsonData edits) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateCursor(JsonData userInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JsonData> existingBoards() {
      return (List<JsonData>) this.boards.stream().map(e -> new JsonData() {
        @Override
        public String getJsonString() {
          return  e.toString();
        }
      });
    }

}