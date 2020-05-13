package p0nki.p0nkisassistant.utils;

public interface ExceptionableSupplier<T> {

    T get() throws Throwable;

}
