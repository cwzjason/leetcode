import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class PageFaultFF {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int instances = input.nextInt();
    for (int i = 0; i < instances; i++) {
      int cacheSize = input.nextInt();
      int requestSize = input.nextInt();
      int[] requestsequence = new int[requestSize];
      for (int k = 0; k < requestSize; k++) {
        requestsequence[k] = input.nextInt();
      }
      int result = pageFaultFF(cacheSize, requestsequence);
      System.out.println(result);
    }
  }

  public static int pageFaultFF(int cacheSize, int[] requestSequence) {
    Set<Integer> set = new HashSet<>();
    int pageFaults = 0;
    for (int i = 0; i < requestSequence.length; i++) {
      int page = requestSequence[i];
      if (set.contains(page)) {
        continue;// hit
      }
      // pagefault
      pageFaults++;

      // cache is not full
      if (set.size() < cacheSize) {
        set.add(page);
        // cache is full
      } else {
        // find the furthest page and replace
        int furthestIndex = -1;
        int pageToRemove = -1;

        for (int p : set) {
          // this value won't appear in the future
          int nextUse = Integer.MAX_VALUE;
          // find the position where the page will appear in the future sequence
          for (int j = i + 1; j < requestSequence.length; j++) {
            if (requestSequence[j] == p) {
              nextUse = j;
              break;
            }
          }

          // remove the furthest page
          if (nextUse > furthestIndex) {
            furthestIndex = nextUse;
            pageToRemove = p;
          }
        }
        set.remove(pageToRemove);
        set.add(page);
      }
    }
    return pageFaults;
  }
}
