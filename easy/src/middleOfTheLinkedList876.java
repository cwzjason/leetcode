import java.util.ArrayList;

public class middleOfTheLinkedList876 {
  /*
   * Input: head = [1,2,3,4,5] Output: [3,4,5] Explanation: The middle node of the list is node 3.
   *
   *
   * Input: head = [1,2,3,4,5,6] Output: [4,5,6]
   *
   * Explanation: Since the list has two middle nodes with values 3 and 4, we return the second one.
   */
  // Definition for singly-linked list.
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

    public ListNode middleNode(ListNode head) {
      // store the list's length
      int length = 0;
      ArrayList<ListNode> list = new ArrayList<ListNode>();
      // use head != null to check if it has the next element
      while (head != null) {
        list.add(head);
        head = head.next;
        length++;
      }
      // return the middle of the list
      return list.get(length / 2);
    }
  }
}
