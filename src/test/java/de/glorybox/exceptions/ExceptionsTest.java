package de.glorybox.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.vavr.CheckedFunction1;
import io.vavr.control.Try;

public class ExceptionsTest {

    @Test
    void foreach() {

        var input = List.of("a", "b", "c", "toolong", "e", "f");
        var result = new ArrayList<String>();
        var handler = new ErrorHandler();

        try {
            for (String string : input) {
                result.add(Examples.duplicatesShortStrings(string));
            }
        } catch (InputTooLongException e) {
            handler.accept(e);
        }

        assertEquals(List.of("aa", "bb", "cc"), result);
        assertEquals(1, handler.called);
    }

    @Test
    void stream() {
        var input = Stream.of("a", "b", "c", "toolong", "e", "f");
        var handler = new ErrorHandler();
        final var result = new ArrayList<>();

        try {
            input.map(item -> {
                try {
                    return Examples.duplicatesShortStrings(item);
                } catch (InputTooLongException e) {
                    throw new RuntimeException(e);
                }
            }).map(item -> {
                result.add(item);
                return item;
            }).forEach(a -> {});

        } catch (Exception e) {
            handler.accept(e);
        }

        assertEquals(List.of("aa", "bb", "cc"), result);
        assertEquals(1, handler.called);

    }

    @Test
    void stream2() {
        var input = Stream.of("a", "b", "c", "toolong", "e", "f");
        var handler = new ErrorHandler();

        var result =
                input
                    .map(CheckedFunction1.liftTry(Examples::duplicatesShortStrings))
                    .map(t -> t.onFailure(handler))
                    //.takeWhile(Try::isSuccess)
                    .flatMap(Try::toJavaStream)
                    .collect(Collectors.toList());

        assertEquals(List.of("aa", "bb", "cc"), result);
        assertEquals(1, handler.called);

    }

    public static class InputTooLongException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class ErrorHandler implements Consumer<Throwable> {
        int called = 0;

        @Override
        public void accept(Throwable arg0) {
            called++;
        }
    }

}