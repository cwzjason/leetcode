import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeIntervals56 {
    public int[][] merge(int[][] intervals) {
      //边界情况
        if (intervals.length <= 1 || intervals == null) {
            return intervals;
        }
        //先按照第一个值排序
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        List<int[]> result = new ArrayList<>();
        int current[] = intervals[0];
        //把第一个值左侧区间值加入
        result.add(current);
        for (int i = 1; i < intervals.length; i++) {
            int next[] = intervals[i];
            //重叠
            if (current[1] >= next[0]) {
                current[1] = Math.max(current[1], next[1]);

            } else {
              //无重叠
                current = next;
                //加右侧区间值
                result.add(current);
            }
        }
        //转数组
        return result.toArray(new int[result.size()][]);
    }
}
