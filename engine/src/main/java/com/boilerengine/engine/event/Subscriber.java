package com.boilerengine.engine.event;

import com.boilerengine.engine.component.AbstractComponent;
import com.boilerengine.engine.component.Component;
import com.boilerengine.engine.plugin.ExtPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public interface Subscriber<Type extends Event> extends Component {

    static <T extends Event> @NotNull Subscriber<T> create(@NotNull Class<T> clazz, @NotNull EventPriority priority, @NotNull Consumer<T> consumer, @NotNull Set<BiPredicate<Subscriber<T>, T>> predicates) {
        return new SimpleSubscriber<>(clazz, priority, consumer, predicates);
    }

    @NotNull Class<Type> getClazz();

    @NotNull Consumer<Type> getConsumer();

    @NotNull EventPriority getPriority();

    @NotNull Set<BiPredicate<Subscriber<Type>, Type>> getPredicates();

    class SimpleSubscriber<Type extends Event> extends AbstractComponent implements Subscriber<Type>, EventExecutor, Listener {

        private static final ExtPlugin SUBSCRIBER_PLUGIN = new ExtPlugin("Boiler-Events", () -> true);

        private final Class<Type> clazz;
        private final Consumer<Type> consumer;
        private final EventPriority priority;
        private final Set<BiPredicate<Subscriber<Type>, Type>> predicates;

        public SimpleSubscriber(Class<Type> clazz, EventPriority priority, Consumer<Type> consumer, Set<BiPredicate<Subscriber<Type>, Type>> predicates) {
            this.clazz = clazz;
            this.consumer = consumer;
            this.priority = priority;
            this.predicates = predicates;

            // by default enable
            enable();
        }

        @Override public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
            if (isEnabled()) {
                final Type type = clazz.cast(event);

                for (BiPredicate<Subscriber<Type>, Type> predicate : predicates) {
                    if (!predicate.test(this, type)) {
                        return;
                    }
                }

                consumer.accept(type);
            }
        }

        @Override public @NotNull AbstractComponent enable() {
            Bukkit.getServer().getPluginManager().registerEvent(clazz, this, priority, this, SUBSCRIBER_PLUGIN);
            enabled = true;
            return this;
        }

        @Override public @NotNull AbstractComponent disable() {
            try {
                final Method method = clazz.getMethod("getHandlerList");
                final HandlerList handlerList = (HandlerList) method.invoke(null);
                handlerList.unregister(this);
            } catch (Exception ignored) {}
            enabled = false;
            return this;
        }

        @Override public @NotNull Class<Type> getClazz() {
            return clazz;
        }

        @Override public @NotNull Consumer<Type> getConsumer() {
            return consumer;
        }

        @Override public @NotNull EventPriority getPriority() {
            return priority;
        }

        @NotNull @Override public Set<BiPredicate<Subscriber<Type>, Type>> getPredicates() {
            return predicates;
        }
    }
}
