import java.util.HashMap;
import java.util.Map;

public class MajorityElement169 {
    public int majorityElement(int[] nums) {
        //统计个数用键值对 hashmap
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            // 如果 map 里还没有这个数字，就设为 0，然后统一加 1
            //getOrDefault map method
            map.put(num, map.getOrDefault(num, 0) + 1);
            //map.get()获取指定 key 对应对 value
            if (map.get(num) > nums.length / 2) {
                return num;
            }
        }
        //return -1;  // 理论上不会执行，但满足编译器要求
        return -1;
    }
}
