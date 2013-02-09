package jenkins.plugins;

import hudson.FilePath;
import hudson.Util;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * Executed on the slave to find source files.
 */
public class SourceCodeReader implements FilePath.FileCallable<String> {

    private static final long serialVersionUID = 2306949616575685812L;

    private static final String GIT_DIR = ".git" + File.separator;

    private static final String HG_DIR = ".hg" + File.separator;

    private static final MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();

    private final byte[] buffer = new byte[4096];

    private final String includes;

    private final String excludes;

    public SourceCodeReader(String includes, String excludes) {
        this.includes = includes == null ? "**" : includes;
        this.excludes = excludes;
    }

    /**
     * Executed on the slave where the build is running.
     */
    public String invoke(File workspace, VirtualChannel channel) throws IOException, InterruptedException {
        FileSet fs = Util.createFileSet(workspace, includes, excludes);
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        StringBuilder allFileContents = new StringBuilder();
        for (String filename : files) {
            File file = new File(workspace.getAbsolutePath() + File.separator + filename);
            if (!scmMetadataFile(filename.trim())) {
                FileInputStream inputStream = new FileInputStream(file);
                while (inputStream.read(buffer) != -1) {
                    allFileContents.append(new String(buffer));
                    allFileContents.append("\n");
                }
            }
        }
        return allFileContents.toString();
    }

    private boolean scmMetadataFile(String filename) {
        return filename.startsWith(GIT_DIR) || filename.startsWith(HG_DIR);
    }

}
