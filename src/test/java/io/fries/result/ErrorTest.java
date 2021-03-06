package io.fries.result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorTest {

    @Mock
    private Throwable error;

    private Result<Object> result;

    @BeforeEach
    void setUp() {
        this.result = new Error<>(error);
    }

    @Test
    void should_be_false_when_the_result_is_an_error_result() {
        final boolean isOk = result.isOk();

        assertThat(isOk).isFalse();
    }

    @Test
    void should_be_true_when_the_result_is_an_error_result() {
        final boolean isError = result.isError();

        assertThat(isError).isTrue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_not_call_the_ok_consumer() {
        final Consumer<Object> consumer = mock(Consumer.class);

        result.ifOk(consumer);

        verify(consumer, never()).accept(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_consume_the_value_of_an_error_result() {
        final Consumer<Throwable> consumer = mock(Consumer.class);

        result.ifError(consumer);

        verify(consumer).accept(error);
    }

    @Test
    void should_throw_when_a_null_reference_is_provided_as_the_error_consumer() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> result.ifError(null))
                .withNoCause()
                .withMessage("The error consumer cannot be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_return_the_result_of_the_fallback_method() {
        final Function<Throwable, Result<Object>> fallbackMethod = mock(Function.class);
        final Result<Object> suppliedResult = mock(Result.class);
        given(fallbackMethod.apply(error)).willReturn(suppliedResult);

        final Result<Object> fallbackResult = result.switchIfError(fallbackMethod);

        verify(fallbackMethod).apply(error);
        assertThat(fallbackResult).isEqualTo(suppliedResult);
    }

    @Test
    void should_throw_when_the_fallback_method_is_null() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> result.switchIfError(null))
                .withNoCause()
                .withMessage("The fallback method cannot be null");
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_get_the_same_result_when_trying_to_map_an_error_result() {
        final Function<Object, Object> mapper = mock(Function.class);

        final Result<Object> mappedResult = result.map(mapper);

        verify(mapper, never()).apply(any());
        assertThat(mappedResult).isEqualTo(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_get_the_same_result_when_trying_to_flat_map_an_error_result() {
        final Function<Object, Result<Object>> mapper = mock(Function.class);

        final Result<Object> mappedResult = result.flatMap(mapper);

        verify(mapper, never()).apply(any());
        assertThat(mappedResult).isEqualTo(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_map_the_wrapped_error_to_another_type() {
        final Throwable mappedError = mock(Throwable.class);
        final Function<Throwable, Throwable> mapper = mock(Function.class);
        given(mapper.apply(error)).willReturn(mappedError);

        final Result<Object> mappedResult = result.mapError(mapper);

        verify(mapper).apply(error);
        assertThat(mappedResult).isEqualTo(new Error<>(mappedError));
    }

    @Test
    void should_throw_when_the_provided_error_mapper_reference_is_null() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> result.mapError(null))
                .withNoCause()
                .withMessage("The error mapper cannot be null");
    }

    @Test
    void should_throw_the_error_when_trying_to_unwrap_the_value() {
        final Throwable throwable = catchThrowable(() -> result.get());

        assertThat(throwable).isEqualTo(error);
    }

    @Test
    @SuppressWarnings("unchecked")
    void should_get_the_supplied_fallback_value_when_trying_to_unwrap_the_result() {
        final Object fallbackValue = mock(Object.class);
        final Supplier<Object> supplier = mock(Supplier.class);
        given(supplier.get()).willReturn(fallbackValue);

        final Object unwrappedValue = result.getOrElse(supplier);

        verify(supplier).get();
        assertThat(unwrappedValue).isEqualTo(fallbackValue);
    }

    @Test
    void should_throw_when_the_supplier_fallback_is_a_null_reference() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> result.getOrElse(null));
    }

    @Test
    void should_unwrap_the_error() {
        final Throwable error = result.getError();

        assertThat(error).isEqualTo(error);
    }

    @Test
    void should_be_equal() {
        final Result<Object> otherResult = new Error<>(error);

        assertThat(result).isEqualTo(otherResult);
        assertThat(result.hashCode()).isEqualTo(otherResult.hashCode());
    }

    @Test
    void should_not_be_equal() {
        final Result<Object> otherResult = new Error<>(mock(Throwable.class));

        assertThat(result).isNotEqualTo(otherResult);
        assertThat(result.hashCode()).isNotEqualTo(otherResult.hashCode());
    }

    @Test
    void should_be_formatted_as_a_string() {
        final String errorString = "Error";
        given(error.toString()).willReturn(errorString);

        final String resultString = result.toString();

        assertThat(resultString).isEqualTo("Error{throwable=Error}");
    }
}