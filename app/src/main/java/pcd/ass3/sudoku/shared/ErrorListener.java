package pcd.ass3.sudoku.shared;

import java.util.Optional;

public interface ErrorListener {
    public void notifyError(String errMsg, Optional<String> description);
}