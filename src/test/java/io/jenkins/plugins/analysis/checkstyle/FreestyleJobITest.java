package io.jenkins.plugins.analysis.checkstyle;

import java.io.IOException;

import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.assertj.core.api.Assertions.*;

import hudson.FilePath;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.plugins.checkstyle.CheckStylePublisher;
import hudson.plugins.checkstyle.CheckStyleResultAction;

/**
 * Integration tests for freestyle jobs with checkstyle plugin.
 *
 * @author Ullrich Hafner
 */
public class FreestyleJobITest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void shouldCreateEmptyResult() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();

        CheckStylePublisher publisher = new CheckStylePublisher();
        project.getPublishersList().add(publisher);

        FreeStyleBuild build = project.scheduleBuild2(0).get();

        j.assertBuildStatusSuccess(build);

        CheckStyleResultAction action = build.getAction(CheckStyleResultAction.class);

        assertThat(action).isNotNull();

        assertThat(action.getResult().getNumberOfAnnotations()).isEqualTo(0);
        assertThat(action.getResult().getErrors()).isNotEmpty();
    }

    @Test
    public void shouldCreateResultWithWarnings() throws Exception {
        FreeStyleProject job = createFreestyleJob("checkstyle-result.xml");

        CheckStylePublisher publisher = new CheckStylePublisher();
        job.getPublishersList().add(publisher);

        FreeStyleBuild build = job.scheduleBuild2(0).get();

        j.assertBuildStatusSuccess(build);

        CheckStyleResultAction action = build.getAction(CheckStyleResultAction.class);

        assertThat(action).isNotNull();

        assertThat(action.getResult().getNumberOfAnnotations()).isEqualTo(6);
        assertThat(action.getResult().getErrors()).isEmpty();
    }

    private FreeStyleProject createFreestyleJob(final String fileName) throws IOException, InterruptedException {
        FreeStyleProject job = j.createFreeStyleProject();
        FilePath workspace = j.jenkins.getWorkspaceFor(job);
        workspace.child(fileName).copyFrom(getClass().getResourceAsStream("checkstyle.xml"));
        return job;
    }

    @Test
    public void shouldCreateUnstableResult() throws Exception {
        FreeStyleProject job = createFreestyleJob("checkstyle-result.xml");

        CheckStylePublisher publisher = new CheckStylePublisher();
        publisher.setUnstableTotalAll("5");

        job.getPublishersList().add(publisher);

        FreeStyleBuild build = job.scheduleBuild2(0).get();

        j.assertBuildStatus(Result.UNSTABLE, build);

        CheckStyleResultAction action = build.getAction(CheckStyleResultAction.class);

        assertThat(action).isNotNull();
        assertThat(action.getResult().getNumberOfAnnotations()).isEqualTo(6);
        assertThat(action.getResult().getPluginResult()).isEqualTo(Result.UNSTABLE);
    }
}
