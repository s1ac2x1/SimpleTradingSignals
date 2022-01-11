package com.kishlaly.ta;

import com.kishlaly.ta.utils.Numbers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;

public class Test {

    public static void main(String[] args) throws Exception {
//        List<Quote> quotes = loadQuotesFromCache("FRC");
//        BarSeries bars = Bars.build(quotes);
        List<Integer> list = new ArrayList<>();
        list.add(-1);
        list.add(-2);
        list.add(-3);
        OptionalInt min = list.stream().mapToInt(Integer::intValue).min();
        OptionalInt max = list.stream().mapToInt(Integer::intValue).max();
        System.out.println(min.getAsInt());
        System.out.println(max.getAsInt());
    }

}
