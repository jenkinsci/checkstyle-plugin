package hudson.plugins.checkstyle;

import hudson.plugins.checkstyle.util.PluginDescriptor;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for the class {@link CheckStylePublisher}. Used as a singleton. The
 * class is marked as public so that it can be accessed from views.
 *
 * @author Ulli Hafner
 */
public final class CheckStyleDescriptor extends PluginDescriptor {
    /** Plug-in name. */
    private static final String PLUGIN_NAME = "checkstyle";
    /** Icon to use for the result and project action. */
    private static final String ACTION_ICON = "/plugin/checkstyle/icons/checkstyle-24x24.png";

    /**
     * Instantiates a new find bugs descriptor.
     */
    CheckStyleDescriptor() {
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

    /** {@inheritDoc} */
    @Override
    public CheckStylePublisher newInstance(final StaplerRequest request, final JSONObject formData) throws FormException {
        return request.bindJSON(CheckStylePublisher.class, formData);
    }
}