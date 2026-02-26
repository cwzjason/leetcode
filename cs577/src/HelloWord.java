import java.util.Scanner;

public class HelloWord {
  public static void main(String[] args) {
    Scanner input = new Scanner(System.in);
    int n = input.nextInt();
    input.nextLine();
    for (int i = 0; i < n; i++) {
      String s = input.nextLine();
      System.out.println("Hello, " + s + "!");
    }
  }
}
