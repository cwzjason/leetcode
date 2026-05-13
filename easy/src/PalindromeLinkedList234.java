import java.util.ArrayList;
import java.util.List;

public class PalindromeLinkedList234 {
    public boolean isPalindrome(ListNode head) {
        //use arraylist to store values and compare
        List<Integer> list = new ArrayList<>();
        //use node to represent head
        ListNode node = head;
        while (node != null) {
            list.add(node.val);
            node = node.next;
        }
        int start = 0;
        int end = list.size() - 1;
        while (start < end) {
            if (list.get(start) != list.get(end)) {
                return false;
            }
            start++;
            end--;
        }
        return true;

    }

    public class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

}
