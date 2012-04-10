package hudson.plugins.checkstyle;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Descriptor for the class {@link CheckStylePublisher}.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100) // NOCHECKSTYLE
public final class CheckStyleDescriptor extends PluginDescriptor {
    /** The ID of this plug-in is used as URL. */
    static final String PLUGIN_ID = "checkstyle";
    /** The URL of the result action. */
    static final String RESULT_URL = PluginDescriptor.createResultUrlName(PLUGIN_ID);
    /** Icon to use for the result and project action. */
    static final String ICON_URL = "/plugin/checkstyle/icons/checkstyle-24x24.png";

    /**
     * Instantiates a new find bugs descriptor.
     */
    public CheckStyleDescriptor() {
        super(CheckStylePublisher.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.Checkstyle_Publisher_Name();
    }

    @Override
    public String getPluginName() {
        return PLUGIN_ID;
    }

    @Override
    public String getIconUrl() {
        return ICON_URL;
    }
}