package com.anjuke.aps.server.context;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjuke.aps.ApsConfig;
import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.ApsStatus;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;
import com.anjuke.aps.exception.ApsException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ApsContextContainer implements RequestHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(ApsContextContainer.class);

    private String contextLibPath = ApsConfig.getApsHome() + "/context";

    private String appPathRoot = ApsConfig.getApsHome() + "/temp";

    private String appConfigFilePath = "META-INF/aps/aps-app.yaml";

    private URLClassLoader contextClassLoader;

    private Map<String, RequestHandler> methodMapping = new HashMap<String, RequestHandler>();

    private List<ApsAppContainer> appContainerList;

    private Set<ModuleVersion> modules = Sets.newHashSet();

    public String getContextLibPath() {
        return contextLibPath;
    }

    public void setContextLibPath(String contextLibPath) {
        this.contextLibPath = contextLibPath;
    }

    public String getAppPathRoot() {
        return appPathRoot;
    }

    public void setAppPathRoot(String appPathRoot) {
        this.appPathRoot = appPathRoot;
    }

    @Override
    public void init() {
        try {
            contextClassLoader = new URLClassLoader(
                    getLibPathUrl(contextLibPath),
                    ApsContextContainer.class.getClassLoader());
            initAppContainer();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Lib path init error", e);
        }
    }

    private void initAppContainer() throws MalformedURLException {
        File appRootFile = new File(appPathRoot);
        if (appRootFile.isFile()) {
            throw new IllegalStateException(
                    "APS app path root is a file, need a directory. Path is "
                            + appRootFile.getAbsolutePath());
        }

        File[] appFiles = appRootFile.listFiles();
        appContainerList = Lists.newArrayListWithCapacity(appFiles.length);
        for (File appPath : appFiles) {

            URL[] urls = getLibPathUrl(appPath.getAbsolutePath());
            if (urls.length == 0) {
                continue;
            }
            LOG.info("Loading {}",appPath.getName());
            ApsAppContainer container = new ApsAppContainer(appPath.getName(),
                    appConfigFilePath, contextClassLoader, urls);
            container.init();

            appContainerList.add(container);
            for (String method : container.getRequestMethods()) {
                Object object = methodMapping.put(method, container);
                if (object != null) {
                    throw new ApsException("Confilct method of " + method
                            + ", mapping 2 handler, " + object + " and "
                            + container);
                }
            }
            modules.addAll(container.getModules());
        }

    }

    private URL[] getLibPathUrl(String path) throws MalformedURLException {
        File filePath = new File(path);
        if (filePath.isFile()) {
            if (filePath.getName().endsWith(".jar")) {
                LOG.debug("add {} to classloader path",
                        filePath.getAbsoluteFile());
                return new URL[] { filePath.toURI().toURL() };
            } else {
                return new URL[0];
            }
        }
        File[] files = filePath.listFiles();
        List<URL> list = Lists.newArrayList();
        for (File f : files) {
            if (!f.getName().endsWith(".jar")) {
                continue;
            }
            list.add(f.toURI().toURL());
            LOG.debug("add {} to classloader path", f.getAbsoluteFile());
        }
        return list.toArray(new URL[list.size()]);
    }

    @Override
    public void destroy() {
        for (ApsAppContainer container : appContainerList) {
            container.destroy();
        }
    }

    public Set<String> getRequestMethods() {
        return methodMapping.keySet();
    };

    @Override
    public void handle(Request request, Response response) {
        RequestHandler handler = methodMapping.get(request.getRequestMethod());
        if (handler == null) {
            response.setStatus(ApsStatus.METHOD_NOT_FOUND);
            response.setErrorMessage("Method Not Fount");
            return;
        }
        handler.handle(request, response);
    }

    @Override
    public Set<ModuleVersion> getModules() {
        return modules;
    }

}
