import java.util.ArrayList;
import java.util.List;

public class FindAllNumbersDisappearedinanArray448 {
    public List<Integer> findDisappearedNumbers(int[] nums) {
        int n = nums.length;
        boolean[] hasseen = new boolean[n + 1];
        for (int num : nums) {
            hasseen[num] = true;
        }
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (!hasseen[i]) {
                result.add(i);
            }
        }
        return result;

    }

}
