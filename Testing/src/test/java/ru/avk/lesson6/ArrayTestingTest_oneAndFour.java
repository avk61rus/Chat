package ru.avk.lesson6;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArrayTestingTest_oneAndFour {

    private ArrayTesting tst;

    @Before
    public  void prepare() { tst = new ArrayTesting();}

    @Test
    public void test_oneAndFour_empty_Array(){
        Assert.assertTrue(tst.oneAndFour(new int[] {1, 4, 4, 1}));
    }

    @Test
    public void test_oneAndFour_only_1_and_others() {
        Assert.assertFalse(tst.oneAndFour(new int[] {1, 4, 3, 1}));
    }

    @Test
    public void test_oneAndFour_without_1_and_4() {
        Assert.assertFalse(tst.oneAndFour(new int[] {2, 6, 3, 5}));
    }

    @Test
    public void test_oneAndFour_only1() {
        Assert.assertFalse(tst.oneAndFour(new int[] {1, 1, 1}));
    }

    @Test
    public void test_oneAndFour_only4() {
        Assert.assertFalse(tst.oneAndFour(new int[]{4, 4, 4}));
    }
}