package com.youzan.cleaner

import org.gradle.api.Project
import org.junit.Test;

class CleanTaskTest {
    //@Test
    public void canAddTaskToProject() {
        Project project = ProjectBuilder.builder().build()
        def task = project.task('greeting', type: "cleanResource")
        assertTrue(task.name.equals("cleanResource"))
    }
}