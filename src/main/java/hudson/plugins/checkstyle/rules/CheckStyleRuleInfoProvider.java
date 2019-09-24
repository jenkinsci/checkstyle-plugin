package hudson.plugins.checkstyle.rules;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton class, which provides information
 * about checkstyle rules. Basically, this is a
 * controller class for the {@link CheckStyleRuleInfoPlugin}
 * instances.
 */
public class CheckStyleRuleInfoProvider {
    private static final CheckStyleRuleInfoProvider INSTANCE = new CheckStyleRuleInfoProvider();

    /**
     * Returns the singleton instance.
     * @return The singleton instance of {@link CheckStyleRuleInfoProvider}.
     */
    public static CheckStyleRuleInfoProvider getInstance() { return INSTANCE; }

    private final List<CheckStyleRuleInfoPlugin> plugins = new ArrayList<>();

    /**
     * Private constructor, to ensure singleton pattern.
     */
    private CheckStyleRuleInfoProvider() {
        // Does nothing.
    }

    /** Called to initialize this instance.
     */
    public void initialize() {
        plugins.clear();
        CheckStyleRules.getInstance().initialize();
    }

    /** Called to add a plugin, which has been detected.
     * @param pPlugin The new plugin, which will be used
     * upon the next call to {@link #getDescription(String)}.
     */
    public void add(final CheckStyleRuleInfoPlugin pPlugin) {
        plugins.add(pPlugin);
    }

    /** Returns a description for the given rule.
     * @param pRule Rule name, for which a description is being requested.
     * @return Description string (never null).
     */
    public String getDescription(final String pRule) {
        for (CheckStyleRuleInfoPlugin plugin : plugins) {
            final String description = plugin.getDescription(pRule);
            if (description != null) {
                return description;
            }
        }
        return CheckStyleRules.getInstance().getDescription(pRule);
    }
}

