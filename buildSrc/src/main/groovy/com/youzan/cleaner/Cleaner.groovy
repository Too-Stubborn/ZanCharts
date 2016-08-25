package com.youzan.cleaner

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task;

/**
 * Created by liangfei on 8/22/16.
 */

public class Cleaner implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.afterEvaluate {
        }

        project.tasks.create("cleanResource", new Action<Task>() {
            @Override
            void execute(Task task) {
                println "clean resource"
            }
        })
    }
}
