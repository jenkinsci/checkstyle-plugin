package hudson.plugins.checkstyle.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/**
 * Provides a token that evaluates to the Checkstyle build result.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class CheckStyleResultTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link CheckStyleResultTokenMacro}.
     */
    public CheckStyleResultTokenMacro() {
        super(CheckStyleResultAction.class, "CHECKSTYLE_RESULT");
    }
}

