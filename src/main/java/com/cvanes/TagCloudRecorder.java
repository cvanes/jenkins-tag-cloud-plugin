package com.cvanes;

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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class TagCloudRecorder extends Recorder {

    private final String includes;

    private final String excludes;

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
        public TagCloudRecorder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(TagCloudRecorder.class, formData);
        }
    }

    /*
     * Data bound from configuration screen.
     */

    @DataBoundConstructor
    public TagCloudRecorder(String includes, String excludes) {
        this.includes = includes;
        this.excludes = excludes;
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
    public boolean perform(AbstractBuild<?, ?> build,
                           Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();
        String workspaceData = build.getWorkspace().act(new WorkspaceReader(includes, excludes));
        logger.println(workspaceData);
        logger.flush();
        System.out.println(workspaceData);

        // store for use in the job main page later
        build.addAction(new TagCloudAction(workspaceData));

        return true;
    }

    @Override
    public Collection<? extends Action> getProjectActions(AbstractProject<?, ?> project) {
        List<TagCloudAction> actions = new ArrayList<TagCloudAction>();
        if (project.getLastBuild() != null) {
            TagCloudAction action = project.getLastBuild().getAction(TagCloudAction.class);
            if (action != null) {
                actions.add(action);
            }
        }

        return actions;
    }


}
