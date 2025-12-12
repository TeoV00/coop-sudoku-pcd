package pcd.ass3.sudoku.communication.rmi.client;
import java.rmi.Remote;

import pcd.ass3.sudoku.domain.Domain.CellUpdate;
import pcd.ass3.sudoku.domain.Domain.UserInfo;


public interface RmiListener extends Remote {

    public void cellUpdated(CellUpdate edits);
    public void cursorsUpdated(UserInfo userInfo);

}