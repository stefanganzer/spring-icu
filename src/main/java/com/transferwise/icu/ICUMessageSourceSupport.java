/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.transferwise.icu;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.ObjectUtils;

/**
 * ICU4j MessageFormat aware
 * {@link org.springframework.context.support.MessageSourceSupport} drop-in
 *
 * @see com.ibm.icu.text.MessageFormat
 */
public abstract class ICUMessageSourceSupport {

	private static final com.ibm.icu.text.MessageFormat INVALID_MESSAGE_FORMAT = new com.ibm.icu.text.MessageFormat("");

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	private boolean alwaysUseMessageFormat = false;

	/**
	 * Cache to hold already generated MessageFormats per message. Used for passed-in
	 * default messages. MessageFormats for resolved codes are cached on a specific basis
	 * in subclasses.
	 */
	private final Map<String, Map<Locale, com.ibm.icu.text.MessageFormat>> messageFormatsPerMessage = new ConcurrentHashMap<>();

	/**
	 * Set whether to always apply the {@code MessageFormat} rules, parsing even messages
	 * without arguments.
	 * <p>
	 * Default is "false": Messages without arguments are by default returned as-is,
	 * without parsing them through MessageFormat. Set this to "true" to enforce
	 * MessageFormat for all messages, expecting all message texts to be written with
	 * MessageFormat escaping.
	 * <p>
	 * For example, MessageFormat expects a single quote to be escaped as "''". If your
	 * message texts are all written with such escaping, even when not defining argument
	 * placeholders, you need to set this flag to "true". Else, only message texts with
	 * actual arguments are supposed to be written with MessageFormat escaping.
	 * @see com.ibm.icu.text.MessageFormat
	 */
	public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
		this.alwaysUseMessageFormat = alwaysUseMessageFormat;
	}

	/**
	 * Return whether to always apply the MessageFormat rules, parsing even messages
	 * without arguments.
	 */
	protected boolean isAlwaysUseMessageFormat() {
		return this.alwaysUseMessageFormat;
	}

	/**
	 * Render the given default message String. The default message is passed in as
	 * specified by the caller and can be rendered into a fully formatted default message
	 * shown to the user.
	 * <p>
	 * The default implementation passes the String to {@code formatMessage}, resolving
	 * any argument placeholders found in them. Subclasses may override this method to
	 * plug in custom processing of default messages.
	 * @param defaultMessage the passed-in default message String
	 * @param args array of arguments that will be filled in for params within the
	 * message, or {@code null} if none.
	 * @param locale the Locale used for formatting
	 * @return the rendered default message (with resolved arguments)
	 * @see #formatMessage(String, ICUMessageArguments, java.util.Locale)
	 */
	protected String renderDefaultMessage(String defaultMessage, ICUMessageArguments args, Locale locale) {
		return formatMessage(defaultMessage, args, locale);
	}

	/**
	 * Format the given message String, using cached MessageFormats. By default invoked
	 * for passed-in default messages, to resolve any argument placeholders found in them.
	 * @param msg the message to format
	 * @param args array of arguments that will be filled in for params within the
	 * message, or {@code null} if none
	 * @param locale the Locale used for formatting
	 * @return the formatted message (with resolved arguments)
	 */
	protected String formatMessage(String msg, ICUMessageArguments args, Locale locale) {
		if (!isAlwaysUseMessageFormat() && args.isEmpty()) {
			return msg;
		}
		Map<Locale, com.ibm.icu.text.MessageFormat> messageFormatsPerLocale = messageFormatsPerMessage
			.computeIfAbsent(msg, key -> new ConcurrentHashMap<>());
		com.ibm.icu.text.MessageFormat messageFormat = messageFormatsPerLocale.computeIfAbsent(locale, key -> {
			try {
				return createMessageFormat(msg, locale);
			}
			catch (IllegalArgumentException ex) {
				// Invalid message format - probably not intended for formatting,
				// rather using a message structure with no arguments involved...
				if (isAlwaysUseMessageFormat()) {
					throw ex;
				}
				// Silently proceed with raw message if format not enforced...
				return INVALID_MESSAGE_FORMAT;
			}
		});
		if (messageFormat == INVALID_MESSAGE_FORMAT) {
			return msg;
		}
		synchronized (messageFormat) {
			return args.formatWith(messageFormat);
		}
	}

	/**
	 * Create a MessageFormat for the given message and Locale.
	 * @param msg the message to create a MessageFormat for
	 * @param locale the Locale to create a MessageFormat for
	 * @return the MessageFormat instance
	 */
	protected com.ibm.icu.text.MessageFormat createMessageFormat(String msg, Locale locale) {
		return new com.ibm.icu.text.MessageFormat(msg, locale);
	}

	/**
	 * Template method for resolving argument objects.
	 * <p>
	 * The default implementation simply returns the given argument array as-is. Can be
	 * overridden in subclasses in order to resolve special argument types.
	 * @param args the original argument array
	 * @param locale the Locale to resolve against
	 * @return the resolved argument array
	 */
	protected ICUMessageArguments resolveArguments(ICUMessageArguments args, Locale locale) {
		return args;
	}

}
