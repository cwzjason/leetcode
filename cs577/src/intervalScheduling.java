import java.util.Arrays;
import java.util.Scanner;

public class intervalScheduling {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);

    int instances = input.nextInt();
    for (int i = 0; i < instances; i++) {

      int jobs = input.nextInt();
      int[][] pair = new int[jobs][2];
      for (int k = 0; k < jobs; k++) {
        // start time
        pair[k][0] = input.nextInt();
        // end time
        pair[k][1] = input.nextInt();
      }
      // earliest deadline first
      Arrays.sort(pair, (a, b) -> Integer.compare(a[1], b[1]));
      // total intervals
      int count = 0;
      // last end time
      int lastEnd = 0;

      for (int k = 0; k < jobs; k++) {
        // whether the start time >= last end time
        if (pair[k][0] >= lastEnd) {
          count++;
          // update the last end time correspond to current start time
          lastEnd = pair[k][1];
        }
      }

      System.out.println(count);
    }
  }
}
