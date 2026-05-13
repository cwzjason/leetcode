import java.util.Arrays;

public class NonoverlappingIntervals435 {
  public int eraseOverlapIntervals(int[][] intervals) {
    // earliest deadline first
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
    int count = 1;
    // last end time
    // It maybe negative number so set lastend as the first element's value
    int lastEnd = intervals[0][1];

    for (int k = 0; k < intervals.length; k++) {
      // whether the start time >= last end time
      if (intervals[k][0] >= lastEnd) {
        count++;
        // update the last end time correspond to current start time
        lastEnd = intervals[k][1];
      }
    }
    return intervals.length - count;
  }
}
