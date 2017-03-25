package hudson.plugins.checkstyle;

import hudson.Extension;
import hudson.plugins.analysis.core.PluginDescriptor;
import org.jenkinsci.Symbol;

/**
 * Descriptor for the class {@link CheckStylePublisher}.
 *
 * @author Ulli Hafner
 */
@Extension(ordinal = 100) @Symbol("checkstyle") // NOCHECKSTYLE
public final class CheckStyleDescriptor extends PluginDescriptor {
    /** The ID of this plug-in is used as URL. */
    static final String PLUGIN_ID = "checkstyle";
    /** The URL of the result action. */
    static final String RESULT_URL = PluginDescriptor.createResultUrlName(PLUGIN_ID);
    /** Icons prefix. */
    static final String ICON_URL_PREFIX = "/plugin/checkstyle/icons/";
    /** Icon to use for the result and project action. */
    static final String ICON_URL = ICON_URL_PREFIX + "checkstyle-24x24.png";

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

    @Override
    public String getSummaryIconUrl() {
        return ICON_URL_PREFIX + "checkstyle-48x48.png";
    }
}