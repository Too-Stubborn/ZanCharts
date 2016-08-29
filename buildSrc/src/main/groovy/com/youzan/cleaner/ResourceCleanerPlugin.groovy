package com.youzan.cleaner

import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by liangfei on 8/22/16.
 */

public class ResourceCleanerPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.extensions.create('resourceCleaner', ResourceCleanerExtension);
        project.afterEvaluate {
            project.android.lintOptions.xmlOutput = new File(project.buildDir, "lintResult.xml");
        }
        project.tasks.create('cleanResource', CleanTask)
    }
}
