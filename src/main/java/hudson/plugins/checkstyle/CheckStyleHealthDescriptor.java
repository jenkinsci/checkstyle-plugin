package hudson.plugins.checkstyle;

import org.jvnet.localizer.Localizable;

import hudson.plugins.analysis.core.AbstractHealthDescriptor;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.AnnotationProvider;
import hudson.plugins.analysis.util.model.Priority;

/**
 * A health descriptor for CheckStyle build results.
 *
 * @author Ulli Hafner
 */
public class CheckStyleHealthDescriptor extends AbstractHealthDescriptor {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -3404826986876607396L;

    /**
     * Creates a new instance of {@link CheckStyleHealthDescriptor} based on the values of the specified descriptor.
     *
     * @param healthDescriptor the descriptor to copy the values from
     */
    public CheckStyleHealthDescriptor(final HealthDescriptor healthDescriptor) {
        super(healthDescriptor);
    }

    /**
     * Creates a new instance of {@link CheckStyleHealthDescriptor} based on the specified values.
     *
     * @param healthy         the healthy threshold, i.e. the number of issues when health is reported as 100%
     * @param unHealthy       the unhealthy threshold, i.e. the number of issues when health is reported as 0%
     * @param minimumPriority the minimum priority to consider when computing the health report. Issues with a priority
     *                        less than this value will be ignored.
     */
    public CheckStyleHealthDescriptor(final String healthy, final String unHealthy, final Priority minimumPriority) {
        super(healthy, unHealthy, minimumPriority);
    }

    @Override
    protected Localizable createDescription(final AnnotationProvider result) {
        if (result.getNumberOfAnnotations() == 0) {
            return Messages._Checkstyle_ResultAction_HealthReportNoItem();
        }
        else if (result.getNumberOfAnnotations() == 1) {
            return Messages._Checkstyle_ResultAction_HealthReportSingleItem();
        }
        else {
            return Messages._Checkstyle_ResultAction_HealthReportMultipleItem(result.getNumberOfAnnotations());
        }
    }
}

