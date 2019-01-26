package io.fries.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class Ok<T> implements Result<T> {

    private final T value;

    Ok(final T value) {
        this.value = value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public void ifOk(final Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        consumer.accept(value);
    }

    @Override
    public boolean isError() {
        return false;
    }

    @Override
    public void ifError(final Consumer<Throwable> consumer) {
        // Do nothing when trying to consume the error of an Ok result.
    }

    @Override
    public <U> Result<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return new Ok<>(mapper.apply(value));
    }

    @Override
    public <U> Result<U> flatMap(final Function<? super T, Result<U>> mapper) {
        Objects.requireNonNull(mapper);
        return mapper.apply(value);
    }

    @Override
    public Result<T> mapError(final Function<Throwable, ? extends Throwable> mapper) {
        return new Ok<>(value);
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public T getOrElse(final Supplier<T> supplier) {
        return value;
    }

    @Override
    public Throwable getError() {
        throw new NoSuchElementException("Result is ok");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Ok<?> ok = (Ok<?>) o;
        return Objects.equals(value, ok.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Ok{" +
                "value=" + value +
                '}';
    }
}
