package ru.avk.lesson6;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArrayTestingTest {

    private  ArrayTesting tst;

    @Before
    public void prepare() { tst = new ArrayTesting();}

    @Test(expected = RuntimeException.class)
    public void test_extractedAfterFour_empty_array() {tst.extractedAfterFour(new int[0]);}

    @Test(expected = RuntimeException.class)
    public void test_extractedAfterFour_without_4() { tst.extractedAfterFour(new int[] {1, 2, 3});}

    @Test
    public void test_extractedAfterFour_4_is_not_last() {
        int[] a = {1, 2, 3, 4, 5, 6, 7};
        Assert.assertArrayEquals(new int[]{5, 6, 7}, tst.extractedAfterFour(a));
    }

    @Test
    public void test_extractedAfterFour_without_some4() {
        int[] a = {1, 4, 3, 4, 5, 6, 4};
        Assert.assertArrayEquals(new int[] {}, tst.extractedAfterFour(a));
    }
}