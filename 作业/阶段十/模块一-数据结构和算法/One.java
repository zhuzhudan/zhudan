package com.study.structure;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhudan <zhudan@ebchinatech.com>
 * @since 2021/2/7 17:01
 */
public class One {
    // 堆排序：大顶堆/小顶堆都是完全二叉树
    // private static boolean isUnique(int[] arr) {
    //     int length = arr.length;
    //     // 从最后一个非叶子节点开始调整
    //     for (int i = length / 2 - 1; i >= 0; i--) {
    //         int parentIndex = i;
    //         // 父节点的值
    //         int temp = arr[parentIndex];
    //         // 左孩子
    //         int childIndex = 2 * parentIndex + 1;
    //         while (childIndex < length){
    //             // 存在右孩子，且右孩子的值>左孩子的值
    //             // childIndex 是 Max(左孩子值, 右孩子值) 的下标
    //             if (childIndex + 1 < length){
    //                 if (arr[childIndex + 1] < arr[childIndex]) {
    //                     childIndex++;
    //                 }
    //             }
    //             // 父节点的值大于左右孩子节点的值，无需交换调整
    //             if (temp < arr[childIndex]){
    //                 break;
    //             }
    //
    //             arr[parentIndex] = arr[childIndex];
    //             parentIndex = childIndex;
    //             childIndex = 2 * childIndex + 1;
    //         }
    //         arr[parentIndex] = temp;
    //     }
    //     return true;
    // }

    private static boolean isUnique(int[] arr) {
        Set<Integer> integerSet = new HashSet<>();
        for (int i : arr) {
            if (integerSet.contains(i)){
                return false;
            }
            integerSet.add(i);
        }
        return true;
    }

    public static void main(String[] args) {
        int[] array={1,2,3,4};
        if(isUnique(array)) {
            System.out.println("YES");
        } else {
            System.out.println("NO");
        }
    }
}
