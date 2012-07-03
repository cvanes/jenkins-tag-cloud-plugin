package jenkins.plugins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class TagCloudGenerator extends Recorder {

    private final String includes;

    private final String excludes;

    private final Integer maxTags;

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public String getDisplayName() {
            return "Generate tag cloud from workspace";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/jenkins-tag-cloud-plugin/help.html";
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public TagCloudGenerator newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(TagCloudGenerator.class, formData);
        }
    }

    /*
     * Data bound from configuration screen.
     */

    @DataBoundConstructor
    public TagCloudGenerator(String includes, String excludes, Integer maxTags) {
        this.includes = includes;
        this.excludes = excludes;
        this.maxTags = maxTags;
    }

    /*
     * Getters needed to show persisted data in job config.
     */

    public String getIncludes() {
        return includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {

        String workspaceData = build.getWorkspace().act(new SourceCodeReader(includes, excludes));
        int mt = 50;
        if (maxTags == null) {
            // mt = 50;
        } else {
            if (maxTags == 0) {
                mt = 50;
            } else {
                mt = maxTags;
            }
        }
        build.addAction(new TagCloudAction(build.getProject(), workspaceData,mt));

        return true;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        // we use a reference to the project to make sure we always show
        // the tag cloud from the latest build on the project page
        return Collections.singletonList(new TagCloudAction(project));
    }

}
