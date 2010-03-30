package hudson.plugins.checkstyle;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Descriptor for the class {@link CheckStylePublisher}.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100)
public final class CheckStyleDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    private static final String PLUGIN_NAME = "checkstyle";
    /** Icon to use for the result and project action. */
    private static final String ACTION_ICON = "/plugin/checkstyle/icons/checkstyle-24x24.png";

    /**
     * Instantiates a new find bugs descriptor.
     */
    public CheckStyleDescriptor() {
        super(CheckStylePublisher.class);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return Messages.Checkstyle_Publisher_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getPluginName() {
        return PLUGIN_NAME;
    }

    /** {@inheritDoc} */
    @Override
    public String getIconUrl() {
        return ACTION_ICON;
    }
}