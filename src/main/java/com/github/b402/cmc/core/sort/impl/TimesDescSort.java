package com.github.b402.cmc.core.sort.impl;

import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sort.SortCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimesDescSort extends Sort<TimesDescSort.Cache> {

    public TimesDescSort() {
        super("TimesDescSort", "降序-次数: 正整数");
    }

    @Nullable
    @Override
    public Cache getScore(@NotNull String score) {
        try {
            int i = Integer.parseInt(score);
            if (i < 0) {
                return null;
            }
            return new Cache(i);
        } catch (NumberFormatException e) {
        }
        return null;
    }

    public class Cache extends SortCache<Cache> {
        private int times;

        public Cache(int times) {
            this.times = times;
        }

        @Override
        public int compareTo(@NotNull Cache o) {
            return -Integer.compare(times, o.times);
        }
    }
}
