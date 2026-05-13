import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class LineIntersections {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int instances = input.nextInt();
    for (int i = 0; i < instances; i++) {
      int n = input.nextInt();
      long[] top = new long[n];
      long[] bottom = new long[n];
      for (int k = 0; k < n; k++) {
        top[k] = input.nextInt();
      }
      for (int k = 0; k < n; k++) {
        bottom[k] = input.nextInt();
      }
      // match top and bottom
      long[][] pair = new long[n][2];
      for (int k = 0; k < n; k++) {
        pair[k][0] = top[k];
        pair[k][1] = bottom[k];
      }
      // sorted according to top
      Arrays.sort(pair, Comparator.comparing(a -> a[0]));
      // get bottom
      long[] reordered = new long[n];
      for (int k = 0; k < n; k++) {
        reordered[k] = pair[k][1];
      }
      long result = mergesort(reordered, 0, n - 1);
      System.out.println(result);

    }

  }

  public static long mergesort(long[] arr, int left, int right) {
    if (left >= right) {
      return 0;
    }
    int mid = (left + right) / 2;
    long inversions = 0;
    // left
    inversions += mergesort(arr, left, mid);
    // right
    inversions += mergesort(arr, mid + 1, right);
    // merge
    inversions += merge(arr, left, mid, right);
    return inversions;

  }

  public static long merge(long[] arr, int left, int mid, int right) {
    long inversions = 0;
    int n1 = mid - left + 1;
    int n2 = right - mid;
    long[] L = new long[n1];
    long[] R = new long[n2];

    for (int i = 0; i < n1; i++) {
      L[i] = arr[left + i];
    }
    for (int i = 0; i < n2; i++) {
      R[i] = arr[mid + 1 + i];
    }
    int i = 0;
    int j = 0;
    int k = left;
    while (i < n1 && j < n2) {
      if (L[i] <= R[j]) {
        arr[k++] = L[i++];
      } else {
        arr[k++] = R[j++];
        inversions += (n1 - i);
      }
    }
    while (i < n1) {
      arr[k++] = L[i++];
    }
    while (j < n2) {
      arr[k++] = R[j++];
    }
    return inversions;
  }
}
