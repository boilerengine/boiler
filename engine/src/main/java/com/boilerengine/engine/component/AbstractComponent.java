package com.boilerengine.engine.component;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class AbstractComponent implements Component {

    private final Set<Component> components = new CopyOnWriteArraySet<>();
    private boolean enabled = false;

    @Override public @NotNull AbstractComponent enable() {
        components.forEach(Component::enable);
        enabled = true;
        return this;
    }

    @Override public @NotNull AbstractComponent disable() {
        components.forEach(Component::disable);
        enabled = false;
        return this;
    }

    @Override public @NotNull AbstractComponent bindWith(@NotNull Component component) {
        components.add(component);
        return this;
    }

    @Override public boolean isEnabled() {
        return enabled;
    }

}
