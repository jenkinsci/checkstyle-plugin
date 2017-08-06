package hudson.plugins.checkstyle;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.*;

import hudson.FilePath;
import hudson.model.Result;

public class CheckstyleWorkflowTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    /**
     * Run a workflow job using {@link CheckStylePublisher} and check for success.
     */
    @Test @Ignore
    public void checkstylePublisherWorkflowStep() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "checkstylePublisherWorkflowStep");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("checkstyle-result.xml");
        report.copyFrom(CheckstyleWorkflowTest.class.getResourceAsStream("/hudson/plugins/checkstyle/parser/checkstyle-result-build1.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'CheckStylePublisher'])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatusSuccess(job.scheduleBuild2(0));
        CheckStyleResultAction result = job.getLastBuild().getAction(CheckStyleResultAction.class);
        assertEquals(1, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link CheckStylePublisher} with a failing threshold of 0, so the given example file
     * "/hudson/plugins/checkstyle/parser/checkstyle-result-build1.xml" will make the build to fail.
     */
    @Test @Ignore
    public void checkstylePublisherWorkflowStepSetLimits() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "checkstylePublisherWorkflowStepSetLimits");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("checkstyle-result.xml");
        report.copyFrom(CheckstyleWorkflowTest.class.getResourceAsStream("/hudson/plugins/checkstyle/parser/checkstyle-result-build1.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'CheckStylePublisher', pattern: '**/checkstyle-result.xml', failedTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n", true)
        );
        jenkinsRule.assertBuildStatus(Result.FAILURE, job.scheduleBuild2(0).get());
        CheckStyleResultAction result = job.getLastBuild().getAction(CheckStyleResultAction.class);
        assertEquals(1, result.getResult().getAnnotations().size());
    }

    /**
     * Run a workflow job using {@link CheckStylePublisher} with a unstable threshold of 0, so the given example file
     * "/hudson/plugins/checkstyle/parser/checkstyle-result-build1.xml" will make the build to fail.
     */
    @Test    @Ignore
    public void checkstylePublisherWorkflowStepFailure() throws Exception {
        WorkflowJob job = jenkinsRule.jenkins.createProject(WorkflowJob.class, "checkstylePublisherWorkflowStepFailure");
        FilePath workspace = jenkinsRule.jenkins.getWorkspaceFor(job);
        FilePath report = workspace.child("target").child("checkstyle-result.xml");
        report.copyFrom(CheckstyleWorkflowTest.class.getResourceAsStream("/hudson/plugins/checkstyle/parser/checkstyle-result-build1.xml"));
        job.setDefinition(new CpsFlowDefinition(""
                        + "node {\n"
                        + "  step([$class: 'CheckStylePublisher', pattern: '**/checkstyle-result.xml', unstableTotalAll: '0', usePreviousBuildAsReference: false])\n"
                        + "}\n")
        );
        jenkinsRule.assertBuildStatus(Result.UNSTABLE, job.scheduleBuild2(0).get());
        CheckStyleResultAction result = job.getLastBuild().getAction(CheckStyleResultAction.class);
        assertEquals(1, result.getResult().getAnnotations().size());
    }
}

