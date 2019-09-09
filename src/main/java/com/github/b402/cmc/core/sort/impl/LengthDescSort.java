package com.github.b402.cmc.core.sort.impl;

import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sort.SortCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class LengthDescSort extends Sort<LengthDescSort.Cache> {

    public LengthDescSort() {
        super("LengthDescSort", "降序-长度: 小数");
    }

    @Nullable
    @Override
    public Cache getScore(@NotNull String score) {
        try {
            BigDecimal bd = new BigDecimal(score);
            return new Cache(bd);
        } catch (NumberFormatException e) {
        }

        return null;
    }

    public class Cache extends SortCache<LengthDescSort.Cache> {
        private BigDecimal decimal;

        public Cache(BigDecimal decimal) {
            this.decimal = decimal;
        }

        @Override
        public int compareTo(@NotNull Cache o) {
            return -decimal.compareTo(o.decimal);
        }
    }
}
