package pcd.ass3.sudoku.communication.rmi;

import java.util.List;

import pcd.ass3.sudoku.communication.DataDistributor;
import pcd.ass3.sudoku.communication.DataDistributorListener;
import pcd.ass3.sudoku.domain.Domain.BoardInfo;
import pcd.ass3.sudoku.domain.Domain.CellUpdate;

class RmiClientDistributor implements DataDistributor {

    @Override
    public void init(DataDistributorListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void registerBoard(BoardInfo boardInfo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void subscribe(String boardName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unsubscribe() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void shareUpdate(CellUpdate cellUpdate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateCursor(JsonData cursorData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<JsonData> existingBoards() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    

}
