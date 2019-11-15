package xyz.glorin.sniper.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class SniperPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("===========SniperPlugin begin===============")

        project.dependencies {
            implementation 'xyz.glorin:sniperlib:0.0.1-SNAPSHOT'
        }

        project.getExtensions().findByType(AppExtension.class)
                .registerTransform(new SniperTransform())
    }
}