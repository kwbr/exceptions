package de.glorybox.exceptions;

public class Examples {

    public static String duplicatesShortStrings(String input) throws ExceptionsTest.InputTooLongException {
        if (input.length() > 1) throw new ExceptionsTest.InputTooLongException();
        return input + input;
    }
}
