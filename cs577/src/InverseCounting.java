import java.util.Scanner;

public class InverseCounting {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int intances = input.nextInt();
    for (int i = 0; i < intances; i++) {
      int elements = input.nextInt();
      int array[] = new int[elements];
      for (int j = 0; j < elements; j++) {
        array[j] = input.nextInt();
      }
      // avoid overflow
      long count = countSort(array, 0, array.length - 1);
      System.out.println(count);
    }
  }

  public static long countSort(int[] array, int left, int right) {
    // no inverse pairs
    if (left >= right) {
      return 0;
    }
    int mid = left + (right - left) / 2;
    long count = 0;
    // left half
    count += countSort(array, left, mid);
    // right half
    count += countSort(array, mid + 1, right);
    // merge count
    count += mergeAndCount(array, left, mid, right);
    return count;
  }

  public static long mergeAndCount(int[] array, int left, int mid, int right) {
    // left elements
    int n1 = mid - left + 1;
    // right elements
    int n2 = right - mid;

    int[] L = new int[n1];
    int[] R = new int[n2];

    for (int i = 0; i < n1; i++) {
      L[i] = array[left + i];
    }
    for (int i = 0; i < n2; i++) {
      R[i] = array[mid + 1 + i];
    }

    int i = 0;
    int j = 0;
    int l = left;
    long count = 0;
    while (i < n1 && j < n2) {
      if (L[i] <= R[j]) {
        array[l++] = L[i++];
      } else {
        array[l++] = R[j++];
        // all remaining elements in L are inversions
        count += (n1 - i);
      }
    }
    while (i < n1) {
      array[l++] = L[i++];
    }
    while (j < n2) {
      array[l++] = R[j++];
    }
    return count;
  }


}
