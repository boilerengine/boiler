package com.boilerengine.engine.component;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface Component {

    @NotNull Component enable();

    @NotNull Component disable();

    @NotNull Component bindWith(@NotNull Component component);

    @NotNull Set<Component> getComponents();

    boolean isEnabled();

    static @NotNull Component newComponent() {
        return new AbstractComponent();
    }
}
