import java.util.HashMap;
import java.util.Map;

public class SingleNumber136 {
    public int singleNumber(int[] nums) {
        //key value
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            //map.getOrDefault if num doesn't exist in map then return 0
            map.put(num, map.getOrDefault(num, 0) + 1);
        }
        //返回一个 Set<Map.Entry<Integer, Integer>>
        //entry 就是 Map 中的每一个 key-value 对
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }
        return -1;
    }


}
