package hudson.plugins.checkstyle;

import hudson.plugins.checkstyle.rules.CheckStyleRuleInfoProvider;
import hudson.plugins.checkstyle.rules.CheckStyleRuleInfoPlugin;

import hudson.Plugin;
import jenkins.model.Jenkins;

/**
 * Initializes the Checkstyle messages and descriptions.
 *
 * @author Ulli Hafner
 */
public class CheckStylePlugin extends Plugin {
    @Override
    public void start() {
        final CheckStyleRuleInfoProvider csrip = CheckStyleRuleInfoProvider.getInstance();
        csrip.initialize();
        for (CheckStyleRuleInfoPlugin plugin : Jenkins.getActiveInstance().getExtensionList(CheckStyleRuleInfoPlugin.class)) {
           csrip.add(plugin);
        }
    }
}
