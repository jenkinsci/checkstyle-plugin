package hudson.plugins.checkstyle.parser;

/**
 * Java Bean class for a violation of the Checkstyle format.
 *
 * @author Ulli Hafner
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
// CHECKSTYLE:OFF
public class Error {
    /** Source of warning. */
    private String source;
    /** Priority of warning. */
    private String severity;
    /** Message of warning. */
    private String message;
    /** The first line of the warning range. */
    private int line;
    /**
     * Returns the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }
    /**
     * Sets the source to the specified value.
     *
     * @param source the value to set
     */
    public void setSource(final String source) {
        this.source = source;
    }
    /**
     * Returns the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }
    /**
     * Sets the severity to the specified value.
     *
     * @param severity the value to set
     */
    public void setSeverity(final String severity) {
        this.severity = severity;
    }
    /**
     * Returns the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * Sets the message to the specified value.
     *
     * @param message the value to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }
    /**
     * Returns the line.
     *
     * @return the line
     */
    public int getLine() {
        return line;
    }
    /**
     * Sets the line to the specified value.
     *
     * @param line the value to set
     */
    public void setLine(final int line) {
        this.line = line;
    }
}

