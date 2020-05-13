package p0nki.p0nkisassistant.utils;

import java.util.function.Supplier;

public abstract class Lazy<T> {

    private boolean calculated;
    private T value;

    public Lazy() {
        calculated = false;
    }

    public static <T> Lazy<T> fromThrowableSupplier(ExceptionableSupplier<T> supplier) {
        return new Lazy<>() {
            @Override
            protected T calculate() throws Throwable {
                return supplier.get();
            }
        };
    }

    public static <T> Lazy<T> fromSupplier(Supplier<T> supplier) {
        return new Lazy<>() {
            @Override
            protected T calculate() {
                return supplier.get();
            }
        };
    }

    public static <T> Lazy<T> fromConstant(T value) {
        return new Lazy<>() {
            @Override
            protected T calculate() {
                return value;
            }
        };
    }

    protected abstract T calculate() throws Throwable;

    public T get() {
        if (calculated) {
            return value;
        } else {
            try {
                value = calculate();
            } catch (Throwable throwable) {
                value = null;
            }
        }
        return value;
    }

}
