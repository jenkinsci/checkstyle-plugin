package hudson.plugins.checkstyle.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractNewAnnotationsTokenMacro;
import hudson.plugins.checkstyle.CheckStyleMavenResultAction;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/**
 * Provides a token that evaluates to the number of new Checkstyle warnings.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class NewCheckStyleWarnningsTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewCheckStyleWarnningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewCheckStyleWarnningsTokenMacro() {
        super("CHECKSTYLE_NEW", CheckStyleResultAction.class, CheckStyleMavenResultAction.class);
    }
}

