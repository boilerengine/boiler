package com.boilerengine.engine.component;

import org.jetbrains.annotations.NotNull;

public interface Component {

    @NotNull Component enable();

    @NotNull Component disable();

    @NotNull Component bindWith(@NotNull Component component);

    boolean isEnabled();

    static @NotNull Component newComponent() {
        return new AbstractComponent();
    }
}
