import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeIntervals56 {
  public int[][] merge(int[][] intervals) {
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    // dynamic list instead of fixed size arraylist
    // result stores arraylists
    List<int[]> result = new ArrayList<>();
    // [1,3] [2,4] 2<3 -> [1,4]
    for (int[] interval : intervals) {
      // result.get(result.size() - 1)last arraylist, [1]second element
      // not overlap
      if (result.isEmpty() || result.get(result.size() - 1)[1] < interval[0]) {
        result.add(interval);
      } else {
        // overlap
        result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], interval[1]);

      }
    }
    return result.toArray(new int[result.size()][]);
  }
}
