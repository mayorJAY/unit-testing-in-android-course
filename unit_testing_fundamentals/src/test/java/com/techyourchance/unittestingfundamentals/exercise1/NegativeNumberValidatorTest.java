package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;


public class NegativeNumberValidatorTest {

    private NegativeNumberValidator sut;

    @Before
    public void setUp() {
        sut = new NegativeNumberValidator();
    }

    @Test
    public void test1() {
        boolean result = sut.isNegative(12);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test2() {
        boolean result = sut.isNegative(0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test3() {
        boolean result = sut.isNegative(-5);
        Assert.assertThat(result, is(true));
    }
}