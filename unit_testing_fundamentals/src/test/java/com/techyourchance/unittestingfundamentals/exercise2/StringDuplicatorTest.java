package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class StringDuplicatorTest {

    private StringDuplicator sut;

    @Before
    public void setUp() {
        sut = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        String result = sut.duplicate("");
        Assert.assertThat(result, is(""));
    }

    @Test
    public void duplicate_anyString_duplicatedStringReturned() {
        String result = sut.duplicate("string");
        Assert.assertThat(result, is("stringstring"));
    }
}