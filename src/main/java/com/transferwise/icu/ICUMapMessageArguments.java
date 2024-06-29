package com.transferwise.icu;

import com.ibm.icu.text.MessageFormat;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;

/** Map-based arguments implementation. Used with patterns which have named arguments */
public class ICUMapMessageArguments implements ICUMessageArguments {

	private static final ICUMapMessageArguments EMPTY = new ICUMapMessageArguments();

	private final Map<String, Object> args;

	private ICUMapMessageArguments() {
		this.args = Map.of();
	}

	public ICUMapMessageArguments(@Nullable Map<String, Object> args) {
		this.args = args == null || args.isEmpty() ? Map.of() : new LinkedHashMap<>(args);
	}

	public static ICUMapMessageArguments of() {
		return EMPTY;
	}

	public static ICUMapMessageArguments of(@Nullable Map<String, Object> args) {
		if (args == null || args.isEmpty()) {
			return EMPTY;
		}
		return new ICUMapMessageArguments(args);
	}

	@Override
	public boolean isEmpty() {
		return args.isEmpty();
	}

	@Override
	public ICUMapMessageArguments transform(Transformation transformation) {
		Map<String, Object> newArgs = new LinkedHashMap<>(args.size(), 1);
		for (Map.Entry<String, Object> item : args.entrySet())
			newArgs.put(item.getKey(), transformation.transform(item.getValue()));
		return new ICUMapMessageArguments(newArgs);
	}

	@Override
	public String formatWith(MessageFormat messageFormat) {
		return messageFormat.format(args);
	}

}
