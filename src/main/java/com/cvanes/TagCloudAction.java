package com.cvanes;

import hudson.model.Action;

import org.mcavallo.opencloud.Cloud;

public class TagCloudAction implements Action {

    private final Cloud wordCloud;

    public TagCloudAction(String workspaceData) {
        wordCloud = new Cloud();
        wordCloud.addText(workspaceData);
    }

    public String getIconFileName() {
        return "/plugin/jenkins-tag-cloud-plugin/icons/24x24.png";
    }

    public String getDisplayName() {
        return "CvE test!!!";
    }

    public String getUrlName() {
        return null;
    }

    public Cloud getCloud() {
        return wordCloud;
    }

}
