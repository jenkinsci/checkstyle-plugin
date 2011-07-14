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
public class NewCheckStyleWarningsTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewCheckStyleWarningsTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewCheckStyleWarningsTokenMacro() {
        super("CHECKSTYLE_NEW", CheckStyleResultAction.class, CheckStyleMavenResultAction.class);
    }
}

