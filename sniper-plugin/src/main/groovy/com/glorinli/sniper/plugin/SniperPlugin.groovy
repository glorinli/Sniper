package com.glorinli.sniper.plugin

import com.android.build.gradle.BaseExtension
import com.glorinli.sniper.internal.SniperTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class SniperPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new SniperTransform())
    }
}