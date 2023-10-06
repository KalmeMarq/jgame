package me.kalmemarq.jgame;

import java.util.function.Supplier;

public class Lazy<T> {
    private final Supplier<T> valueSupplier;
    private T value;

    private Lazy(Supplier<T> valueSupplier) {
        this.valueSupplier = valueSupplier;
    }

    /**
     * Created an instance of the Lazy class with the specified value supplier.
     * @param valueSupplier the supplier that creates the lazily computed value
     */
    public static <T> Lazy<T> of(Supplier<T> valueSupplier) {
        return new Lazy<>(valueSupplier);
    }

    /**
     * If the value has already been computed, return true, otherwise false.
     * @return true if the value has already been computed, otherwise false
     */
    public boolean isInitialized() {
        return this.value != null;
    }

    /**
     * Returns the lazily computed value.
     * <p>
     * Computes the value if not already.
     * </p>
     * @return the lazily computed value
     */
    public T get() {
        if (this.value == null) {
            this.value = this.valueSupplier.get();
        }
        return this.value;
    }
}
