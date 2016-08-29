package com.youzan.cleaner

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import com.youzan.cleaner.internal.Cleaner

public class CleanTask extends DefaultTask {
    public CleanTask() {
        super()
        dependsOn "lint"
    }
    @TaskAction
    def clean() {
        def lintResult = project.android.lintOptions.xmlOutput
        def excludedFiles = project.resourceCleaner.excludedFiles
        //def manifest = project.android.sourceSets.getByName('main').manifest.srcFile
        Cleaner.clean(lintResult, excludedFiles)
    }
}
