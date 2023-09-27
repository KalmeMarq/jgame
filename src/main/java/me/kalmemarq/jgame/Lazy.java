package me.kalmemarq.jgame;

import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> valueSupplier;
    private T value;

    private Lazy(Supplier<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    public static <T> Lazy<T> of(Supplier<T> valueSupplier) {
        return new Lazy<>(valueSupplier);
    }

    public boolean isInitialized() {
        return this.value != null;
    }

    public T get() {
        if (this.value == null) {
            this.value = this.valueSupplier.get();
        }
        return this.value;
    }
}
