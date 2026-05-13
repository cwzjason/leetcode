import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class WIS {
  static class Job {
    int start;
    int end;
    int weight;

    Job(int start, int end, int weight) {
      this.start = start;
      this.end = end;
      this.weight = weight;
    }
  }

  // Find the index of the last (rightmost) job that does not conflict with it
  public static int binarysearch(Job[] jobs, int index) {
    int low = 0;
    int high = index - 1;
    // can't find any previous available job
    int lastjobindex = -1;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      // it's finish time<=current start time
      if (jobs[mid].end <= jobs[index].start) {
        lastjobindex = mid;
        low = mid + 1;
      } else {
        high = mid - 1;
      }
    }
    return lastjobindex;
  }

  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int instances = input.nextInt();
    for (int k = 0; k < instances; k++) {
      int n = input.nextInt();
      Job[] jobs = new Job[n];
      for (int i = 0; i < n; i++) {
        int start = input.nextInt();
        int end = input.nextInt();
        int weight = input.nextInt();
        jobs[i] = new Job(start, end, weight);
      }
      // sort by FF
      Arrays.sort(jobs, Comparator.comparingInt(e -> e.end));

      // dp[i]=The maximum weight that can be obtained in the first i jobs
      // dp[0] no job, we start with dp[1]
      long[] dp = new long[n + 1];
      for (int i = 1; i <= n; i++) {
        // choose current job
        long includejob = jobs[i - 1].weight;
        // find compatible job
        int p = binarysearch(jobs, i - 1);
        if (p != -1) {
          includejob += dp[p + 1];
        }
        // max not choose/choose
        dp[i] = Math.max(dp[i - 1], includejob);
      }

      System.out.println(dp[n]);
    }
  }
}
