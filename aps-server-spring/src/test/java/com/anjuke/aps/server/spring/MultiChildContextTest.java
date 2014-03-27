package com.anjuke.aps.server.spring;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.anjuke.aps.ApsContext;
import com.anjuke.aps.server.context.ApsAppContainer;
import com.anjuke.aps.server.context.ApsContextContainer;
import com.google.common.collect.Lists;

public class MultiChildContextTest {
    ApsContext context=new ApsContext();

    private ApsAppContainer createContainer(String project, ClassLoader parent)
            throws Exception {
        String userdir = System.getProperty("user.dir");

        ApsAppContainer container = new ApsAppContainer(project,
                "META-INF/aps/aps-app.yaml", parent, getJarURL(userdir
                        + "/../aps-test-support/" + project + "/target/"));
        container.init(context);
        return container;
    }

    URL[] getJarURL(String path) throws Exception {
        File tmp = new File(path);
        if (tmp.isFile() && tmp.getName().endsWith(".jar")) {
            return new URL[] { tmp.toURI().toURL() };
        }

        List<URL> list = Lists.newArrayList();
        for (File child : tmp.listFiles()) {
            if (child.getName().endsWith(".jar")) {
                list.add(child.toURI().toURL());
            }
        }
        return list.toArray(new URL[list.size()]);
    }

    @Test
    public void test() throws Exception {
        String userdir = System.getProperty("user.dir");

        assertTrue(userdir.endsWith("/aps-server-spring"));

        URLClassLoader parentCL = new URLClassLoader(getJarURL(userdir
                + "/../aps-test-support/parent-lib/target/"),ApsContextContainer.class.getClassLoader());
        System.out.println(Arrays.toString(parentCL.getURLs()));
        ApsAppContainer child = createContainer("child-app", parentCL);
        ApsAppContainer childAnother = createContainer("child-another",
                parentCL);

        child.destroy(context);
        childAnother.destroy(context);

    }
}
