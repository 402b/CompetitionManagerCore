package com.github.b402.cmc.core.sort.impl;

import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sort.SortCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecondDescSort extends Sort<SecondDescSort.Cache> {

    public SecondDescSort() {
        super("TimeAsecSort", "降序-时间: hh:mm'ss\"SS");
    }
    private Pattern regex = Pattern.compile("((?<h>\\d{0,2}):)?((?<m>\\d{0,2})')?(?<s>\\d{1,2})\"(?<ss>\\d{0,2})");


    @Nullable
    @Override
    public Cache getScore(@NotNull String score) {
        Matcher matcher = regex.matcher(score);
        if (!matcher.matches()) {
            return null;
        }
        return new Cache(matcher);
    }

    public class Cache extends SortCache<Cache> {
        private long time = 0;

        private Cache(Matcher score) {
            String h = score.group("h");
            if (h != null) {
                time += Integer.parseInt(h) * 60 * 60 * 100;
            }
            String m = score.group("m");
            if (m != null) {
                time += Integer.parseInt(m) * 60 * 100;
            }
            String s = score.group("s");
            if (s != null) {
                time += Integer.parseInt(s) * 100;
            }
            String ss = score.group("ss");
            if (ss != null) {
                time += Integer.parseInt(ss);
            }
        }

        @Override
        public int compareTo(@NotNull SecondDescSort.Cache o) {
            return -Long.compare(time, o.time);
        }

        @Override
        public String toString() {
            return "Cache{" +
                    "time=" + time +
                    '}';
        }
    }
}
