package de.glorybox.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

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
		var input = List.of("a", "b", "c", "toolong", "e", "f");
		var handler = new ErrorHandler();

		List<String> result = new ArrayList<>();
		
		try {
			result = input
					.stream()
					.map(arg0 -> {
						try {
							return Examples.duplicatesShortStrings(arg0);
						} catch (InputTooLongException e) {
							throw new RuntimeException(e);
						}
					}).collect(Collectors.toList());
				
		} catch (Exception e) {
			handler.accept(e);
		}
		
		
		assertEquals(List.of("aa", "bb", "cc"), result);
		assertEquals(1, handler.called);

	}
	

	public static class InputTooLongException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	public static class ErrorHandler implements Consumer<Throwable>{
		int called = 0; 

		@Override
		public void accept(Throwable arg0) {
			called++;
		}
	}

}