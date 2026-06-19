import java.util.HashSet;
import java.util.Set;

public class LongestSubstringWithoutRepeatingCharacters3 {
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet();
        int left = 0;
        int max = 0;

        // slide window method
        for (int right = 0; right < s.length(); right++) {
            //为什么不是charAt(left)  因为我们获取值是从左往右获取 获取的右边的值
            char c = s.charAt(right);
            // we need continuously to remove the repeat element until it disappear
            // To avoid "abba" such situation
            //用while为什么不用if while循环清除掉所有的左侧元素直到不存在重复元素为止 而不是清除一次 例如 abb
            while (set.contains(c)) {
                //为什么是移除左边的元素而不是出现重复的右边元素
                //因为右边的元素还没有加入 无法移除
                //而且索引需要连续 又必须让程序进行 所以应该移除左边元素 但是曾经的长度已经保留在max中 所以删除也无所谓
                set.remove(s.charAt(left));
                left++;
            }
            //不包含c 就加 包含的话清除掉再加
            set.add(c);
            //更新历史中最长字符串的长度
            max = Math.max(max, right - left + 1);
        }
        return max;
    }
}
