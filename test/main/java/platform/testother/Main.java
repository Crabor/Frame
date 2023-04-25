package platform.testother;

import java.util.Scanner;

public class Main {
    public static int max(int[] arr) {
        int max = arr[0];
        for (int i = 0; i < arr.length; i++)
            if (arr[i] > max)
                max = arr[i];
        return max;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = scanner.nextInt();
        }
        int[] arr2 = new int[max(arr) + 1];
        arr2[0] = 1;
        for (int i = 1; i < arr2.length; i++) {
            arr2[i] = 1 + arr2[i - 1] * (i + 1);
        }
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr2[arr[i]];
        }
        System.out.println(sum % 1000000007);
    }
}
