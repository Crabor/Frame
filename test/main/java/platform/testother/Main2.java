package platform.testother;

import java.util.Scanner;

public class Main2 {
    public static void main(String[] args) {
        /*
        题目描述:
        小红很喜欢"red"字符串，她定义一个字符串的美丽度为：该字符串包含的"red"子序列的数量。注意子序列是可以不连续的，例如"rreed"包含了4个"red"子序列，因此美丽度为4。
        小红定义一个字符串的权值为：该字符串所有连续子串的美丽度之和。例如，"redd"的权值为3，因为它包含了一个"red"连续子串，美丽度为1，包含了一个"redd"连续子串，美丽度为2。其它连续子串的美丽度都为0。
        小红想知道，长度为n的、仅由字符'r'、'e'、'd'构成的所有字符串（共有3^n个字符串）的权值之和是多少？答案请对10^9+7取模。

        输入描述:
        一个正整数n
        1≤n≤1000

        输出描述:
        长度为n的、仅由字符'r'、'e'、'd'构成的所有字符串的权值之和。

        样例输入:
        3

        样例输出:
        1

        说明:
        长度为3的字符串，仅有"red"权值为1，其余字符串权值均为0。
        */
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[] arr = new int[n];
        int[] arr2 = new int[n];
        arr[0] = 1;
        arr2[0] = 1;
        for (int i = 1; i < n; i++) {
            arr[i] = (arr[i - 1] * 3) % 1000000007;
            arr2[i] = (arr2[i - 1] + arr[i]) % 1000000007;
        }
        System.out.println(arr2[n - 1]);

        /*
        作者：小红书


        链接：https://www.nowcoder.com/discuss/417743?type=0&order=0&pos=1&page=1
         */
    }
}
