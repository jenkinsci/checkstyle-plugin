/*
 * The MIT License
 * 
 * Copyright (c) 2018 CloudBees, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.checkstyle;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.UnprotectedRootAction;
import hudson.tasks.Shell;
import hudson.util.HttpResponses;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestExtension;
import org.kohsuke.stapler.HttpResponse;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Tests the extraction of CheckStyle analysis results.
 */
public class CheckStylePublisherTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    @Issue("SECURITY-656")
    public void testXxe() throws Exception {
        String xxeInUserContentLink = j.getURL() + "userContent/xxe.xml";
        String oobInUserContentLink = j.getURL() + "userContent/oob.xml";
        String triggerLink = j.getURL() + "triggerMe";

        String xxeFile = this.getClass().getResource("testXxe-xxe.xml").getFile();
        String xxeFileContent = FileUtils.readFileToString(new File(xxeFile), StandardCharsets.UTF_8);
        String adaptedXxeFileContent = xxeFileContent.replace("$OOB_LINK$", oobInUserContentLink);

        String oobFile = this.getClass().getResource("testXxe-oob.xml").getFile();
        String oobFileContent = FileUtils.readFileToString(new File(oobFile), StandardCharsets.UTF_8);
        String adaptedOobFileContent = oobFileContent.replace("$TARGET_URL$", triggerLink);

        File userContentDir = new File(j.jenkins.getRootDir(), "userContent");
        FileUtils.writeStringToFile(new File(userContentDir, "xxe.xml"), adaptedXxeFileContent);
        FileUtils.writeStringToFile(new File(userContentDir, "oob.xml"), adaptedOobFileContent);

        FreeStyleProject project = j.createFreeStyleProject();
        Shell copyToWorkspace = new Shell("curl \"" + xxeInUserContentLink + "\" > xxe.xml");
        project.getBuildersList().add(copyToWorkspace);

        CheckStylePublisher publisher = new CheckStylePublisher();
        publisher.setPattern("xxe.xml");
        project.getPublishersList().add(publisher);

        assertEquals(Result.SUCCESS, project.scheduleBuild2(0).get().getResult());

        YouCannotTriggerMe urlHandler = j.jenkins.getExtensionList(UnprotectedRootAction.class).get(YouCannotTriggerMe.class);
        assertEquals(0, urlHandler.triggerCount);
    }

    @TestExtension("testXxe")
    public static class YouCannotTriggerMe implements UnprotectedRootAction {
        private int triggerCount = 0;

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return "triggerMe";
        }

        public HttpResponse doIndex() {
            triggerCount++;
            return HttpResponses.plainText("triggered");
        }
    }
}
