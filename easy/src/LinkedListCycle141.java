public class LinkedListCycle141 {
    public boolean hasCycle(ListNode head) {
        //base case
        if (head == null || head.next == null) {
            return false;
        }
        //set two pointers and their relative speed is 1
        //if there exists a cycle, they finally meet together
        ListNode slow = head;
        ListNode fast = head;
        //why use head instead of slow
        //reason: If fast can safely jump two nodes ahead, slow is guaranteed to be able to jump one node ahead.
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (fast == slow) {
                return true;
            }
        }
        return false;
    }

    /**
     * Definition for singly-linked list.
     */
    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
            next = null;
        }
    }


}
