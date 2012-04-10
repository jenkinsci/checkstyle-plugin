package hudson.plugins.checkstyle.dashboard;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.analysis.graph.BuildResultGraph;
import hudson.plugins.analysis.graph.TotalsGraph;
import hudson.plugins.checkstyle.CheckStyleProjectAction;
import hudson.plugins.checkstyle.Messages;
import hudson.plugins.view.dashboard.DashboardPortlet;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A portlet that shows the warnings totals trend graph.
 *
 * @author Ulli Hafner
 */
public final class WarningsTotalsGraphPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link WarningsTotalsGraphPortlet}.
     *
     * @param name
     *            the name of the portlet
     * @param width
     *            width of the graph
     * @param height
     *            height of the graph
     * @param dayCountString
     *            number of days to consider
     */
    @DataBoundConstructor
    public WarningsTotalsGraphPortlet(final String name, final String width, final String height, final String dayCountString) {
        super(name, width, height, dayCountString);

        configureGraph(getGraphType());
    }

    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return CheckStyleProjectAction.class;
    }

    @Override
    protected String getPluginName() {
        return "checkstyle";
    }

    @Override
    protected BuildResultGraph getGraphType() {
        return new TotalsGraph();
    }

    /**
     * Extension point registration.
     *
     * @author Ulli Hafner
     */
    @Extension(optional = true)
    public static class WarningsGraphDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Messages.Portlet_WarningsTotalsGraph();
        }
    }
}

