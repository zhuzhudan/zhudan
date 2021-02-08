package com.study.structure;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;

/**
 * @author zhudan <zhudan@ebchinatech.com>
 * @since 2021/2/7 17:03
 */
public class Bags {
    public static int maxValue(int[] values, int[] people, int max){
        if (values == null || values.length == 0) return 0;
        if (people == null || people.length == 0) return 0;
        if (values.length != people.length || max <= 0) return 0;

        int[][] dp = new int[values.length + 1][max + 1];
        String numStr = "";

        for (int i = 1; i <= values.length; i++){
            for (int j = 1; j <= max; j++){
                if (j < people[i - 1]){
                    dp[i][j] = dp[i - 1][j];
                } else {
                    int preValue = dp[i - 1][j];
                    int currentValue = values[i - 1] + dp[i - 1][j - people[i - 1]];
                    dp[i][j] = Math.max(preValue, currentValue);
                }
            }

        }
        int j = max;
        for (int i = values.length; i > 0; i--) {
            if (dp[i][j] > dp[i - 1][j]){
                numStr += i + " ";
                j = j - people[i - 1];
            }
            if (j == 0) break;
        }
        System.out.println("选中的金矿有：" + numStr);


        return dp[values.length][max];
    }

    public static void main(String[] args) {
        int[] values={60,30,50,40,60};
        int[] weights={2,2,6,5,4};
        int max=10;
        System.out.println("总价值:"+maxValue(values,weights,max));
    }
}
