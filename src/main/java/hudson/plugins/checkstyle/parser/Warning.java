package hudson.plugins.checkstyle.parser;

import hudson.plugins.annotations.util.model.AbstractAnnotation;
import hudson.plugins.annotations.util.model.Priority;
import hudson.plugins.checkstyle.rules.CheckStyleRules;

import org.apache.commons.lang.StringUtils;

/**
 * A serializable Java Bean class representing a warning.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * </p>
 *
 * @author Ulli Hafner
 */
public class Warning extends AbstractAnnotation {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = 5171661552905752370L;

    /** The tooltip. */
    @SuppressWarnings("all")
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("")
    private final String tooltip = StringUtils.EMPTY; // backward compatibility NOPMD

    /**
     * Creates a new instance of {@link Warning}.
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     * @param start
     *            the first line of the line range
     * @param end
     *            the last line of the line range
     */
    public Warning(final Priority priority, final String message, final String category, final String type,
            final int start, final int end) {
        super(priority, message, start, end, category, type);
    }

    /**
     * Creates a new instance of {@link Warning}.
     *
     * @param priority
     *            the priority
     * @param message
     *            the message of the warning
     * @param category
     *            the warning category
     * @param type
     *            the identifier of the warning type
     * @param lineNumber
     *            the line number of the warning in the corresponding file
     */
    public Warning(final Priority priority, final String message, final String category, final String type, final int lineNumber) {
        this(priority, message, category, type, lineNumber, lineNumber);
    }

    /** {@inheritDoc} */
    public String getToolTip() {
        return CheckStyleRules.getInstance().getDescription(getType());
    }
}

