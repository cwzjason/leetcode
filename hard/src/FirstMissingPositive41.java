import java.util.HashSet;

public class FirstMissingPositive41 {
    public int firstMissingPositive(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        //remove repetitive ints and negative nums
        for (int num : nums) {
            if (num > 0) {
                set.add(num);
            }
        }
        int integer = 1;
        //set.contain is O(1) ;list.contains is O(n)
        while (set.contains(integer)) {
            integer++;
        }
        return integer;
    }
}
