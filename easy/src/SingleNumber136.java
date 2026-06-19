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
    // a ^ a = 0（相同数字异或得 0）

    //a ^ 0 = a（任何数与 0 异或得自身）
    //题目中是多个出现两次的数和一个只出现一次的数 两次的数异或得0 0和一次的数得到自己


}
