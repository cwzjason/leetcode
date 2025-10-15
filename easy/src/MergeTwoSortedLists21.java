public class MergeTwoSortedLists21 {
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
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
      ListNode dummy = new ListNode();
      ListNode temp = dummy;
      while (list1 != null && list2 != null) {
        if (list1.val < list2.val) {
          temp.next = list1;
          // move the list pointer to the next
          list1 = list1.next;
        } else {
          temp.next = list2;
          // move the list pointer to the next
          list2 = list2.next;
        }
        // let the pointer move to the next node
        temp = temp.next;
      }
      // can't use while because it is a dead loop
      // it is not list1=list1.next, so it can't end the loop
      // while (list1 != null) {
      // temp.next = list1;
      // }
      if (list1 != null) {
        temp.next = list1;
      }
      if (list2 != null) {
        temp.next = list2;
      }
      return dummy.next;
    }
  }
}
