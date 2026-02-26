public class NumberofStepstoReduceaNumberinBinaryRepresentationtoOne1404 {
  public int numSteps(String s) {
    // int[] intarray = new int[s.length()];
    // for (int i = 0; i < intarray.length; i++) {
    // // convert the each string element to an integer
    // intarray[i] = s.charAt(i) - '0';
    // }
    // int sum = 0;
    // int count = 0;
    // for (int i = 0; i < intarray.length; i++) {
    // sum += intarray[i] * Math.pow(2, intarray.length - i - 1);
    // }
    // while (sum > 1) {
    // if (sum % 2 == 0) {
    // sum /= 2;
    //
    // } else {
    // sum += 1;
    // }
    // count++;
    // }
    // return count;
    // overflow


    int count = 0;
    // 进位01->10
    int carry = 0;
    // 从左到右
    for (int i = s.length() - 1; i > 0; i--) {
      int bit = s.charAt(i) - '0' + carry;
      if (bit % 2 == 0) {
        count++;
      } else {
        // step1 奇数+1 step2 /2
        count += 2;
        // carry如果碰到奇数就会+1 就会出现carry 而且carry往后都是1 因为一旦进位了 1就必须要再+1 carry 然后再进位1
        // 111 最右侧1 进位 count+=2 carry=1
        // 中间1 1+carry=2 count+=1 carry=1
        // 末位直接 +carry
        carry = 1;
      }
    }
    // 最高位只受carry 影响 而且最高位还进不了位
    // 所以说i>0 并且return +carry
    return count + carry;

  }
}
