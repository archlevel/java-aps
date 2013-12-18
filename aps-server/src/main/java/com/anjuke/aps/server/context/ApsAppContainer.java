package com.anjuke.aps.server.context;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import com.anjuke.aps.ModuleVersion;
import com.anjuke.aps.Request;
import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.Response;

/**
 *
 * @author Faye
 *
 */
public class ApsAppContainer implements RequestHandler {
    private static final String HANDLER_KEY = "aps.request.handler";

    private static final Logger LOG = LoggerFactory
            .getLogger(ApsAppContainer.class);

    private final String name;
    private final URLClassLoader classloader;

    private final String configFilePath;

    private RequestHandler handler;

    public ApsAppContainer(String name, String configFilePath,
            ClassLoader parent, URL... urls) {
        this.name = name;
        this.classloader = new URLClassLoader(urls, parent);
        this.configFilePath = configFilePath;
    }

    @Override
    public void init() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        try {
            InputStream configStream = classloader
                    .getResourceAsStream(configFilePath);
            Yaml yaml = new Yaml(new CustomClassLoaderConstructor(classloader));
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) yaml
                    .load(configStream);
            handler = (RequestHandler) map.get(HANDLER_KEY);

            handler.init();
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @Override
    public Set<String> getRequestMethods() {
        return handler.getRequestMethods();
    }

    @Override
    public void handle(Request request, Response response) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        try {
            handler.handle(request, response);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @Override
    public void destroy() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classloader);
        try {
            handler.destroy();
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }

    @Override
    public Set<ModuleVersion> getModules() {

        return handler.getModules();
    }

    @Override
    public String toString() {
        return "ApsAppContainer [name=" + name + "]";
    }

}
