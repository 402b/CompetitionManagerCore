package com.github.b402.cmc.core.sort.impl;

import com.github.b402.cmc.core.sort.Sort;
import com.github.b402.cmc.core.sort.SortCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class LengthAsecSort extends Sort<LengthAsecSort.Cache> {

    public LengthAsecSort() {
        super("LengthAsecSort", "升序-长度: 小数");
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

    public class Cache extends SortCache<LengthAsecSort.Cache> {
        private BigDecimal decimal;

        public Cache(BigDecimal decimal) {
            this.decimal = decimal;
        }

        @Override
        public int compareTo(@NotNull Cache o) {
            return decimal.compareTo(o.decimal);
        }
    }
}
