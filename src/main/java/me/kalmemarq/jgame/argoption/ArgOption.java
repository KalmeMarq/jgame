package me.kalmemarq.jgame.argoption;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Consumer;

public class ArgOption<T> {
    protected final String name;
    protected final String alias;
    protected boolean required;
    protected final Class<T> type;
    protected T defaultValue;
    protected T value;

    protected ArgOption(Class<T> clazz, String name, String alias) {
        this.type = clazz;
        this.name = name;
        this.alias = alias;
        this.value = null;
    }

    public ArgOption<T> required() {
        this.required = true;
        return this;
    }

    public ArgOption<T> defaultsTo(T value) {
        this.defaultValue = value;
        return this;
    }

    protected T getDefaultValue() {
        return this.defaultValue;
    }

    protected boolean has() {
        return this.value != null;
    }

    protected T getValue() {
        return this.has() ? this.value : this.defaultValue;
    }

    protected void ifHas(Consumer<T> consumer) {
        if (this.has()) {
            consumer.accept(this.value);
        }
    }

    @SuppressWarnings("unchecked")
    protected void onNotFound() {
        if (this.defaultValue != null) {
            return;
        }

        if (this.type == Boolean.class) {
            this.defaultValue = (T) Boolean.FALSE;
        } else if (this.type == Integer.class) {
            this.defaultValue = (T) Integer.valueOf(0);
        }
    }

    @SuppressWarnings("unchecked")
    protected void parseValues(List<String> values) {
        if (values.isEmpty()) {
            if (this.type == Boolean.class) {
                this.value = (T) Boolean.TRUE;
            }
        } else {
            String value = values.get(0);

            if (this.type.isPrimitive()) {
                try {
                    this.value = (T) this.type.getMethod("valueOf", String.class).invoke(null, value);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                         | NoSuchMethodException | SecurityException e) {
                    System.out.println("(A) Failed to parse arg " + this.name);
                }
            } else {
                try {
                    this.value = this.type.getConstructor(String.class).newInstance(value);
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                         | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    System.out.println("(B) Failed to parse arg " + this.name);
                }
            }
        }
    }
}
