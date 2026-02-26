import java.util.Arrays;

public class TwoCityScheduling1029 {
  // 一半人去A 一半人去B
  // [COSTa,COSTb] 差值越小去A 差值越大去B 然后根据差值排序
  public int twoCitySchedCost(int[][] costs) {
    Arrays.sort(costs, (a, b) -> ((a[0] - b[0]) - (a[1] - b[1])));
    // 一半一半
    int n = costs.length / 2;
    int total = 0;
    for (int i = 0; i < costs.length; i++) {
      if (i < n) {
        total += costs[i][0];// A
      } else {
        total += costs[i][1];// B
      }
    }
    return total;
  }


}
