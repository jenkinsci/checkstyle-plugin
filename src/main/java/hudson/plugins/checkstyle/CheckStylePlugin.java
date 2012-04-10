package hudson.plugins.checkstyle;

import hudson.Plugin;
import hudson.plugins.checkstyle.rules.CheckStyleRules;

/**
 * Initializes the Checkstyle messages and descriptions.
 *
 * @author Ulli Hafner
 */
public class CheckStylePlugin extends Plugin {
    @Override
    public void start() {
        CheckStyleRules.getInstance().initialize();
    }
}
