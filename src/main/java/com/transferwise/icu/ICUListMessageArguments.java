package com.transferwise.icu;

import com.ibm.icu.text.MessageFormat;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Default, List-based arguments implementation. Used with patterns which have numbered
 * arguments
 */
public class ICUListMessageArguments implements ICUMessageArguments {

	private static final ICUListMessageArguments EMPTY = new ICUListMessageArguments();

	private final List<Object> args;

	private ICUListMessageArguments() {
		this.args = List.of();
	}

	public ICUListMessageArguments(@Nullable List<Object> args) {
		this.args = args == null || args.isEmpty() ? List.of() : new ArrayList<>(args);
	}

	public ICUListMessageArguments(@Nullable Object[] args) {
		this(args == null ? null : Arrays.asList(args));
	}

	public static ICUMessageArguments of(@Nullable Object[] args) {
		if (args == null || args.length == 0) {
			return EMPTY;
		}
		return new ICUListMessageArguments(args);
	}

	public static ICUMessageArguments of(@Nullable List<Object> args) {
		if (args == null || args.isEmpty()) {
			return EMPTY;
		}
		return new ICUListMessageArguments(args);
	}

	@Override
	public boolean isEmpty() {
		return args.isEmpty();
	}

	@Override
	public ICUListMessageArguments transform(Transformation transformation) {
		List<Object> newArgs = new ArrayList<>(args.size());
		for (Object item : args)
			newArgs.add(transformation.transform(item));
		return new ICUListMessageArguments(newArgs);
	}

	@Override
	public String formatWith(MessageFormat messageFormat) {
		return messageFormat.format(toArray());
	}

	public Object[] toArray() {
		return args.toArray(new Object[0]);
	}

}
