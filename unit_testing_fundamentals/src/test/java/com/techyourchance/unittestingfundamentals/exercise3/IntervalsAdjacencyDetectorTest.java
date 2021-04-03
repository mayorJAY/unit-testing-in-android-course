package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class IntervalsAdjacencyDetectorTest {

    private IntervalsAdjacencyDetector sut;

    @Before
    public void setUp() {
        sut = new IntervalsAdjacencyDetector();
    }

    // interval1 is before interval2
    @Test
    public void isAdjacent_Interval1BeforeInterval2_trueReturned() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(4, 6);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 overlaps interval2 on start
    @Test
    public void isAdjacent_Interval1OverlapsInterval2OnStart_falseReturned() {
        Interval interval1 = new Interval(1, 5);
        Interval interval2 = new Interval(4, 6);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 is contained within interval2
    @Test
    public void isAdjacent_Interval1ContainedWithinInterval2_falseReturned() {
        Interval interval1 = new Interval(2, 5);
        Interval interval2 = new Interval(1, 6);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 contains interval2
    @Test
    public void isAdjacent_Interval1ContainsInterval2_falseReturned() {
        Interval interval1 = new Interval(1, 7);
        Interval interval2 = new Interval(4, 6);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 overlaps interval2 on end
    @Test
    public void isAdjacent_Interval1OverlapsInterval2OnEnd_falseReturned() {
        Interval interval1 = new Interval(1, 3);
        Interval interval2 = new Interval(-1, 2);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 is after interval2
    @Test
    public void isAdjacent_Interval1AfterInterval2_trueReturned() {
        Interval interval1 = new Interval(7, 9);
        Interval interval2 = new Interval(4, 6);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 is the same as interval2
    @Test
    public void isAdjacent_Interval1SameAsInterval2_falseReturned() {
        Interval interval1 = new Interval(7, 9);
        Interval interval2 = new Interval(7, 9);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    // interval1 end is the same as interval2 start
    @Test
    public void isAdjacent_Interval1EndSameAsInterval2Start_trueReturned() {
        Interval interval1 = new Interval(7, 9);
        Interval interval2 = new Interval(9, 12);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }

    // interval1 start is the same as interval2 end
    @Test
    public void isAdjacent_Interval1StartSameAsInterval2End_trueReturned() {
        Interval interval1 = new Interval(7, 9);
        Interval interval2 = new Interval(1, 7);
        boolean result = sut.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }
}