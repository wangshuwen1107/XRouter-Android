package cn.cheney.xrouter.plugin;


import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import cn.cheney.xrouter.plugin.utils.XLogger;


class XRouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        XLogger.init(project.getLogger());
        AppExtension android = project.getExtensions().findByType(AppExtension.class);
        if (null != android) {
            android.registerTransform(new XRouterTransform());
        }
    }

}
