package com.cvanes;

import hudson.model.Action;

import java.util.List;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

public class TagCloudAction implements Action {

    private final List<Tag> tags;

//    private final transient AbstractProject<?, ?> job;

    public TagCloudAction(String workspaceData) {
        Cloud tagCloud = new Cloud();
        tagCloud.setMinWeight(1);
        tagCloud.setMaxWeight(10);
        tagCloud.setMaxTagsToDisplay(50);
        tagCloud.addText(workspaceData);

        tags = tagCloud.tags();
    }

    public String getIconFileName() {
        return "/plugin/jenkins-tag-cloud-plugin/icons/24x24.png";
    }

    public String getDisplayName() {
        return "Tag Cloud From Source";
    }

    public String getUrlName() {
        return "tagCloud";
    }

    public List<Tag> getTags() {
        return tags;
    }
//
//    public AbstractProject<?,?> getOwner() {
//        return job;
//    }

//    public void doIndex(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
//    }

}
