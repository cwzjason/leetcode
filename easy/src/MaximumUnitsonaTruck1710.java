import java.util.Arrays;

public class MaximumUnitsonaTruck1710 {
  public int maximumUnits(int[][] boxTypes, int truckSize) {
    int total = 0;
    // b-a descending order
    // goal: load max unitsPerBox's box first to achieve the most units
    Arrays.sort(boxTypes, (a, b) -> b[1] - a[1]);
    // b[1] - a[1] represents second column - first column
    // [1,2] [2,3]
    // 2-3<0 -> reorder: [2,3],[1,2]

    for (int i = 0; i < boxTypes.length; i++) {
      int numberOfBoxes = boxTypes[i][0];
      int unitsPerBox = boxTypes[i][1];

      // if trucksize is able to load current boxes[2,3], go to the next level[1,2]
      if (truckSize >= numberOfBoxes) {
        total += numberOfBoxes * unitsPerBox;
        truckSize -= numberOfBoxes;
      } else {
        // if it can't load all of these boxes, just load the rest of the truckSize's boxes
        total += truckSize * unitsPerBox;
        break;
      }
    }
    return total;

  }

}
