package hudson.plugins.checkstyle.parser;

import org.apache.commons.lang.StringUtils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import hudson.plugins.analysis.util.model.AbstractAnnotation;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.checkstyle.rules.CheckStyleRules;

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
    /** Origin of the annotation. */
    public static final String ORIGIN = "checkstyle";

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
        setOrigin(ORIGIN);
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

    @Override
    public String getToolTip() {
        return CheckStyleRules.getInstance().getDescription(getType());
    }

    /** Not used anymore. @deprecated */
    @SuppressWarnings("all")
    @SuppressFBWarnings("")
    @Deprecated
    private final transient String tooltip = StringUtils.EMPTY; // backward compatibility NOPMD
}

