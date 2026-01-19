import java.util.HashSet;

public class DeleteNodesFromLinkedListPresentinArray3217 {
  public class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
      this.val = val;
    }

    ListNode(int val, ListNode next) {
      this.val = val;
      this.next = next;
    }
  }


  class Solution {
    public ListNode modifiedList(int[] nums, ListNode head) {
      // hashset search is O(1) faster
      HashSet<Integer> set = new HashSet<>();
      for (int num : nums) {
        set.add(num);
      }
      ListNode dummy = new ListNode(0);
      dummy.next = head;
      ListNode cur = dummy;

      while (cur.next != null) {
        if (set.contains(cur.next.val)) {
          cur.next = cur.next.next;
        } else {
          cur = cur.next;
        }
      }
      return dummy.next;
    }
  }
}
// time limit exceed but answer is right
// public ListNode modifiedList(int[] nums, ListNode head) {
// ListNode dummy = new ListNode(0);
// dummy.next = head;
// ListNode cur = dummy;
//
// boolean isdelete = false;
// while (cur.next != null) {
// isdelete = false;
// for (int i = 0; i < nums.length; i++) {
// if (cur.next.val == nums[i]) {
// cur.next = cur.next.next;
// isdelete = true;
// break;
// }
// }
//
// if (isdelete == false) {
// cur = cur.next;
// }
//
// }
// return dummy.next;
// }
// }
