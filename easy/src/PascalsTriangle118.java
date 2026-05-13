import java.util.ArrayList;
import java.util.List;

public class PascalsTriangle118 {
  public List<List<Integer>> generate(int numRows) {
    List<List<Integer>> result = new ArrayList<List<Integer>>();
    for (int i = 0; i < numRows; i++) {
      // 存储每一行的list
      List<Integer> row = new ArrayList<>();
      for (int j = 0; j <= i; j++) {
        // 最左边最右边为1
        if (j == 0 || j == i) {
          row.add(1);
        } else {
          int value = result.get(i - 1).get(j - 1) + result.get(i - 1).get(j);
          row.add(value);
        }
      }
      result.add(row);
    }
    return result;
  }
}
