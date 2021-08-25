package cn.cheney.xrouter.plugin.utils;

import com.android.SdkConstants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.cheney.xrouter.plugin.consts.XRouterConstant;

import static cn.cheney.xrouter.plugin.consts.XRouterConstant.MODULE_CLASS_DIR;

public class ScannerUtils {


    public static List<String> scanJar(File dir) {
        List<String> classList = new ArrayList<>();
        try {
            JarFile jarFile = new JarFile(dir);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(SdkConstants.DOT_CLASS)) {
                    String classname = trimName(name, 0).replace(File.separatorChar, '.');
                    if (classname.startsWith(XRouterConstant.MODULE_CLASS_PREFIX)) {
                        Pattern pattern = Pattern.compile("\\d+$");
                        Matcher matcher = pattern.matcher(classname);
                        //不是匿名内部类
                        if (!matcher.find()) {
                            classList.add(classname);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }


    public static List<String> scanDir(File dir) {
        if (null == dir || !dir.exists() || !dir.isDirectory()) {
            return null;
        }
        List<String> classList = new ArrayList<>();
        File moduleDir = new File(dir, MODULE_CLASS_DIR);
        if (moduleDir.exists() && moduleDir.isDirectory()) {
            Collection<File> classFiles = FileUtils.listFiles(moduleDir, new SuffixFileFilter(SdkConstants.DOT_CLASS,
                    IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
            for (File file : classFiles) {
                String className = trimName(file.getAbsolutePath(), dir.getAbsolutePath().length() + 1);
                className = className.replace(File.separatorChar, '.');
                if (className.contains(XRouterConstant.MODULE_CLASS_PREFIX)) {
                    Pattern pattern = Pattern.compile("\\d+$");
                    Matcher matcher = pattern.matcher(className);
                    //不是匿名内部类
                    if (!matcher.find()) {
                        classList.add(className);
                    }
                }
            }
        }
        return classList;
    }


    /**
     * [prefix] com/xxx/aaa.class --> com/xxx/aaa
     * [prefix] com\xxx\aaa.class --> com\xxx\aaa
     */
    private static String trimName(String packageClass, int start) {
        return packageClass.substring(start, packageClass.length() - SdkConstants.DOT_CLASS.length());
    }

}
