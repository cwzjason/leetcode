import java.util.HashMap;
import java.util.Map;

public class MajorityElement169 {
  public int majorityElement(int[] nums) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int num : nums) {
      // 如果 map 里还没有这个数字，就设为 0，然后统一加 1
      map.put(num, map.getOrDefault(num, 0) + 1);
      if (map.get(num) > nums.length / 2) {
        return num;
      }
    }
    return -1;
  }
}
