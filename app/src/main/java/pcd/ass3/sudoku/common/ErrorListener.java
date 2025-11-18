package pcd.ass3.sudoku.common;

import java.util.Optional;

public interface ErrorListener {
    public void notifyError(String errMsg, Optional<String> description);
}