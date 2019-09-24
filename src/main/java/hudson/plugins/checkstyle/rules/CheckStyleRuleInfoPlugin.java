package hudson.plugins.checkstyle.rules;

import hudson.ExtensionPoint;

/**
 * FIXME: Document type RuleInfoPlugin.
 *
 * @author Ulli Hafner
 */
public abstract class CheckStyleRuleInfoPlugin implements ExtensionPoint {
    /** Returns a description for the given rule.
     * @return Non-null description string, if available, or null (no description available
     *   for this rule).
     */
    public abstract String getDescription(String pRule);

}

