package hudson.plugins.checkstyle;

import hudson.plugins.analysis.util.SaxSetup;

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
        SaxSetup sax = new SaxSetup();

        try {
            CheckStyleRules.getInstance().initialize();
        }
        finally {
            sax.cleanup();
        }
    }
}
