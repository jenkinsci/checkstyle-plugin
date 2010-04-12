package hudson.plugins.checkstyle.dashboard;

import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.checkstyle.CheckStyleProjectAction;

/**
 * A base class for portlets of the Checkstyle plug-in.
 *
 * @author Ulli Hafner
 */
public abstract class CheckStylePortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link CheckStylePortlet}.
     *
     * @param name
     *            the name of the portlet
     */
    public CheckStylePortlet(final String name) {
        super(name);
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return CheckStyleProjectAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected String getPluginName() {
        return "checkstyle";
    }
}
