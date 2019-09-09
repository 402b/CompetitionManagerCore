package com.github.b402.cmc.core.sort;

import com.github.b402.cmc.core.sort.impl.LengthAsecSort;
import com.github.b402.cmc.core.sort.impl.LengthDescSort;
import com.github.b402.cmc.core.sort.impl.SecondAsecSort;
import com.github.b402.cmc.core.sort.impl.SecondDescSort;
import com.github.b402.cmc.core.sort.impl.TimesAsecSort;
import com.github.b402.cmc.core.sort.impl.TimesDescSort;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Sort<T extends SortCache<T>> {
    private static final Map<String, Sort> SORT_FACTORY_MAP = new ConcurrentHashMap<>();

    @NotNull
    private final String name;
    @NotNull
    private final String displayName;

    public Sort(@NotNull String name, @NotNull String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Nullable
    public abstract T getScore(@NotNull String score);


    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public static void registerSort(@NotNull Sort sortFactory) {
        SORT_FACTORY_MAP.put(sortFactory.getName(), sortFactory);
    }

    @NotNull
    public static Collection<String> getAllSortName() {
        return SORT_FACTORY_MAP.keySet();
    }

    @Nullable
    public static Sort getSort(@NotNull String name) {
        return SORT_FACTORY_MAP.get(name);
    }

    public static void init() {
        registerSort(new LengthAsecSort());
        registerSort(new LengthDescSort());
        registerSort(new SecondAsecSort());
        registerSort(new SecondDescSort());
        registerSort(new TimesAsecSort());
        registerSort(new TimesDescSort());
    }
}
