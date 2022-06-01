package com.boilerengine.engine.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface SubscriberBuilder<Type extends Event> {

    static <T extends Event> @NotNull SubscriberBuilder<T> newBuilder(@NotNull Class<T> clazz, @NotNull EventPriority priority) {
        return new SubscriberBuilderImpl<>(clazz, priority);
    }

    @NotNull SubscriberBuilder<Type> filter(@NotNull BiPredicate<Subscriber<Type>, Type> predicate);

    @NotNull SubscriberBuilder<Type> expireAfter(@NotNull Number number, @NotNull TimeUnit unit);

    @NotNull Subscriber<Type> handle(@NotNull Consumer<Type> consumer);

    class SubscriberBuilderImpl<Type extends Event> implements SubscriberBuilder<Type> {

        private final Class<Type> clazz;
        private final EventPriority priority;
        private final Set<BiPredicate<Subscriber<Type>, Type>> predicates;

        public SubscriberBuilderImpl(@NotNull Class<Type> clazz, @NotNull EventPriority priority) {
            this.clazz = clazz;
            this.priority = priority;
            this.predicates = new HashSet<>();
        }

        @Override public @NotNull SubscriberBuilder<Type> filter(@NotNull BiPredicate<Subscriber<Type>, Type> predicate) {
            predicates.add(predicate);
            return this;
        }

        @Override public @NotNull SubscriberBuilder<Type> expireAfter(@NotNull Number number, @NotNull TimeUnit unit) {
            return this;
        }

        @Override public @NotNull Subscriber<Type> handle(@NotNull Consumer<Type> consumer) {
            return Subscriber.create(clazz, priority, consumer, predicates);
        }
    }
}
