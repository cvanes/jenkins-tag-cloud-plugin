package jenkins.plugins;

import hudson.model.Action;
import hudson.model.AbstractProject;

import java.util.List;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

public class TagCloudAction implements Action {
  
  private final transient AbstractProject<?, ?> project;
  
  private Cloud cloud;
  
  private boolean projectLevelView = false;
  
  public TagCloudAction(AbstractProject<?, ?> project, String workspaceData, int maxTags) {
    this.project = project;
    
    cloud = new Cloud();
    cloud.setMinWeight(1);
    cloud.setMaxWeight(10);
    cloud.setMaxTagsToDisplay(maxTags);
    cloud.addText(workspaceData);
    
    // the Cloud object could be huge so we don't want to persist everything to xml
    List<Tag> tagList = cloud.tags();
    cloud.clear();
    cloud.addTags(tagList);
  }
  
  public TagCloudAction(AbstractProject<?, ?> project) {
    this.project = project;
    projectLevelView = true;
  }
  
  public String getIconFileName() {
    return "/plugin/jenkins-tag-cloud-plugin/icons/24x24.png";
  }
  
  public String getDisplayName() {
    return "Tag Cloud";
  }
  
  public String getUrlName() {
    return "tagCloud";
  }
  
  public Cloud getCloud() {
    if (projectLevelView && project.getLastBuild() != null) {
      TagCloudAction actionFromLastBuild = project.getLastBuild().getAction(TagCloudAction.class);
      if (actionFromLastBuild != null) {
        return actionFromLastBuild.getCloud();
      }
    }
    return cloud;
  }
  
  public AbstractProject<?, ?> getOwner() {
    return project;
  }
  
}
