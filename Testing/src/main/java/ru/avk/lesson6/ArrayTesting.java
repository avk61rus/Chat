package ru.avk.lesson6;

import java.util.Arrays;

public class ArrayTesting {

    public int[] extractedAfterFour(int[] a) {
        for (int i = a.length - 1; i >= 0; i--) {
            if (a[i] == 4) {
                return Arrays.copyOfRange(a, i + 1, a.length);
            }
        }
        throw new RuntimeException("Не правильный массив");
    }

        public boolean oneAndFour ( int[] b){
            boolean contains1 = false;
            boolean contains4 = false;
            for (int value : b) {
                switch (value) {
                    case 1:
                        contains1 = true;
                        break;
                    case 4:
                        contains4 = true;
                        break;
                    default:
                        return false;
                }
            }
            return contains1 && contains4;
        }
    }
