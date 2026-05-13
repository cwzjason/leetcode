public class ScoreAfterFlippingMatrix861 {
  public int matrixScore(int[][] grid) {
    // step1: for each first num of row, to maximize it-> we need to flip the row if it's 0
    // step2: for each column, if the number of 0 is more than 1. flip this row
    // flip: 1-current element

    int row = grid.length;
    int col = grid[0].length;

    for (int i = 0; i < row; i++) {

      if (grid[i][0] == 0) {
        for (int j = 0; j < col; j++) {
          grid[i][j] = 1 - grid[i][j];
        }
      }
    }

    for (int j = 0; j < col; j++) {
      int count = 0;
      for (int i = 0; i < row; i++) {
        if (grid[i][j] == 1) {
          count++;
        }
      }
      // 1's<0's
      if (count < row - count) {
        for (int k = 0; k < row; k++) {
          grid[k][j] = 1 - grid[k][j];
        }
      }
    }
    int total = 0;

    for (int i = 0; i < row; i++) {
      int rowvalue = 0;
      for (int j = 0; j < col; j++) {
        rowvalue = 2 * rowvalue + grid[i][j];
      }
      total += rowvalue;
    }
    return total;
  }
}
