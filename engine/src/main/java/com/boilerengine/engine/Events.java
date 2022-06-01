package com.boilerengine.engine;

import com.boilerengine.engine.event.SubscriberBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public class Events {

    public static <Type extends Event> SubscriberBuilder<Type> listen(@NotNull Class<Type> clazz, @NotNull EventPriority priority) {
        return SubscriberBuilder.newBuilder(clazz, priority);
    }

    public static <Type extends Event> SubscriberBuilder<Type> listen(@NotNull Class<Type> clazz) {
        return SubscriberBuilder.newBuilder(clazz, EventPriority.NORMAL);
    }

}
