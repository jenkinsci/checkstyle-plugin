package hudson.plugins.checkstyle;

import org.kohsuke.stapler.DataBoundConstructor;

import hudson.Extension;

import hudson.plugins.analysis.views.WarningsCountColumn;

import hudson.views.ListViewColumnDescriptor;

/**
 * A column that shows the total number of Checkstyle warnings in a job.
 *
 * @author Ulli Hafner
 */
public class CheckStyleColumn extends WarningsCountColumn<CheckStyleProjectAction> {
    /**
     * Creates a new instance of {@link CheckStyleColumn}.
     */
    @DataBoundConstructor
    public CheckStyleColumn() { // NOPMD: data binding
        super();
    }

    @Override
    protected Class<CheckStyleProjectAction> getProjectAction() {
        return CheckStyleProjectAction.class;
    }

    @Override
    public String getColumnCaption() {
        return Messages.Checkstyle_Warnings_ColumnHeader();
    }

    /**
     * Descriptor for the column.
     */
    @Extension
    public static class ColumnDescriptor extends ListViewColumnDescriptor {
        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return Messages.Checkstyle_Warnings_Column();
        }
    }
}
