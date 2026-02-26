import java.util.Arrays;

public class SortIntegersbyTheNumberof1Bits1356 {
  public int[] sortByBits(int[] arr) {
    // lambda表达式需要对象数组 需要Integer
    Integer[] temp = new Integer[arr.length];
    for (int i = 0; i < arr.length; i++) {
      temp[i] = arr[i];
    }
    // Integer.bitcount 获取二进制1的数量
    Arrays.sort(temp, (a, b) -> {
      int countA = Integer.bitCount(a);
      int countB = Integer.bitCount(b);
      if (countA == countB) {
        // 都是升序排
        return a - b;
      }
      return countA - countB;
    });
    for (int i = 0; i < arr.length; i++) {
      arr[i] = temp[i];
    }
    return arr;
  }
}
