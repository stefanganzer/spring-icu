package com.transferwise.icu;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.ISpringTemplateEngine;
import org.thymeleaf.spring5.dialect.SpringStandardDialect;

import java.util.Set;

public class SpringIcuTemplateEngine extends TemplateEngine implements ISpringTemplateEngine, MessageSourceAware {

    private static final SpringStandardDialect SPRINGSTANDARD_DIALECT = new SpringStandardDialect();
    private MessageSource messageSource = null;
    private MessageSource templateEngineMessageSource = null;


    public SpringIcuTemplateEngine() {
        super();
        // This will set the SpringStandardDialect, overriding the Standard one set in the super constructor
        super.setDialect(SPRINGSTANDARD_DIALECT);
    }

    /**
     * <p>
     *   Implementation of the {@link MessageSourceAware#setMessageSource(MessageSource)}
     *   method at the {@link MessageSourceAware} interface, provided so that
     *   Spring is able to automatically set the currently configured {@link MessageSource} into
     *   this template engine.
     * </p>
     * <p>
     *   If several {@link MessageSource} implementation beans exist, Spring will inject here
     *   the one with id {@code "messageSource"}.
     * </p>
     * <p>
     *   This property <b>should not be set manually</b> in most scenarios (see
     *   {@link #setTemplateEngineMessageSource(MessageSource)} instead).
     * </p>
     *
     * @param messageSource the message source to be used by the message resolver
     */
    @Override
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * <p>
     *   Convenience method for setting the message source that will
     *   be used by this template engine, overriding the one automatically set by
     *   Spring at the {@link #setMessageSource(MessageSource)} method.
     * </p>
     *
     * @param templateEngineMessageSource the message source to be used by the message resolver
     */
    @Override
    public void setTemplateEngineMessageSource(final MessageSource templateEngineMessageSource) {
        this.templateEngineMessageSource = templateEngineMessageSource;
    }

    /**
     * <p>
     *   Returns whether the SpringEL compiler should be enabled in SpringEL expressions or not.
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#getEnableSpringELCompiler()} on the dialect instance itself. It is provided
     *   here in order to allow users to enable the SpEL compiler without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   Expression compilation can significantly improve the performance of Spring EL expressions, but
     *   might not be adequate for every environment. Read
     *   <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html#expressions-spel-compilation">the
     *   official Spring documentation</a> for more detail.
     * </p>
     * <p>
     *   Also note that although Spring includes a SpEL compiler since Spring 4.1, most expressions
     *   in Thymeleaf templates will only be able to properly benefit from this compilation step when at least
     *   Spring Framework version 4.2.4 is used.
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @return {@code true} if SpEL expressions should be compiled if possible, {@code false} if not.
     */
    public boolean getEnableSpringELCompiler() {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getEnableSpringELCompiler();
            }
        }
        return false;
    }

    /**
     * <p>
     *   Sets whether the SpringEL compiler should be enabled in SpringEL expressions or not.
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#setEnableSpringELCompiler(boolean)} on the dialect instance itself. It is provided
     *   here in order to allow users to enable the SpEL compiler without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   Expression compilation can significantly improve the performance of Spring EL expressions, but
     *   might not be adequate for every environment. Read
     *   <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html#expressions-spel-compilation">the
     *   official Spring documentation</a> for more detail.
     * </p>
     * <p>
     *   Also note that although Spring includes a SpEL compiler since Spring 4.1, most expressions
     *   in Thymeleaf templates will only be able to properly benefit from this compilation step when at least
     *   Spring Framework version 4.2.4 is used.
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @param enableSpringELCompiler {@code true} if SpEL expressions should be compiled if possible, {@code false} if not.
     */
    public void setEnableSpringELCompiler(final boolean enableSpringELCompiler) {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setEnableSpringELCompiler(enableSpringELCompiler);
            }
        }
    }

    /**
     * <p>
     *   Returns whether the {@code <input type="hidden" ...>} marker tags rendered to signal the presence
     *   of checkboxes in forms when unchecked should be rendered <em>before</em> the checkbox tag itself,
     *   or after (default).
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#getRenderHiddenMarkersBeforeCheckboxes()} on the dialect instance
     *   itself. It is provided here in order to allow users to modify this behaviour without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   A number of CSS frameworks and style guides assume that the {@code <label ...>} for a checkbox
     *   will appear in markup just after the {@code <input type="checkbox" ...>} tag itself, and so the
     *   default behaviour of rendering an {@code <input type="hidden" ...>} after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @return {@code true} if hidden markers should be rendered before the checkboxes, {@code false} if not.
     *
     * @since 3.0.10
     */
    public boolean getRenderHiddenMarkersBeforeCheckboxes() {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                return ((SpringStandardDialect) dialect).getRenderHiddenMarkersBeforeCheckboxes();
            }
        }
        return false;
    }

    /**
     * <p>
     *   Sets whether the {@code <input type="hidden" ...>} marker tags rendered to signal the presence
     *   of checkboxes in forms when unchecked should be rendered <em>before</em> the checkbox tag itself,
     *   or after (default).
     * </p>
     * <p>
     *   (This is just a convenience method, equivalent to calling
     *   {@link SpringStandardDialect#setRenderHiddenMarkersBeforeCheckboxes(boolean)} on the dialect instance
     *   itself. It is provided here in order to allow users to modify this behaviour without
     *   having to directly create instances of the {@link SpringStandardDialect})
     * </p>
     * <p>
     *   A number of CSS frameworks and style guides assume that the {@code <label ...>} for a checkbox
     *   will appear in markup just after the {@code <input type="checkbox" ...>} tag itself, and so the
     *   default behaviour of rendering an {@code <input type="hidden" ...>} after the checkbox can lead to
     *   bad application of styles. By tuning this flag, developers can modify this behaviour and make the hidden
     *   tag appear before the checkbox (and thus allow the lable to truly appear right after the checkbox).
     * </p>
     * <p>
     *   Note this hidden field is introduced in order to signal the existence of the field in the form being sent,
     *   even if the checkbox is unchecked (no URL parameter is added for unchecked check boxes).
     * </p>
     * <p>
     *   This flag is set to {@code false} by default.
     * </p>
     *
     * @param renderHiddenMarkersBeforeCheckboxes {@code true} if hidden markers should be rendered
     *                                            before the checkboxes, {@code false} if not.
     *
     * @since 3.0.10
     */
    public void setRenderHiddenMarkersBeforeCheckboxes(final boolean renderHiddenMarkersBeforeCheckboxes) {
        final Set<IDialect> dialects = getDialects();
        for (final IDialect dialect : dialects) {
            if (dialect instanceof SpringStandardDialect) {
                ((SpringStandardDialect) dialect).setRenderHiddenMarkersBeforeCheckboxes(renderHiddenMarkersBeforeCheckboxes);
            }
        }
    }
}
