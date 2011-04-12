package hudson.plugins.checkstyle.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/**
 * Provides a token that evaluates to the number of Checkstyle warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class CheckStyleWarningCountTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link CheckStyleWarningCountTokenMacro}.
     */
    public CheckStyleWarningCountTokenMacro() {
        super(CheckStyleResultAction.class, "CHECKSTYLE_COUNT");
    }
}

