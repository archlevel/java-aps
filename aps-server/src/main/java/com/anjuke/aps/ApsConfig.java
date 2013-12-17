package com.anjuke.aps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.anjuke.aps.RequestHandler;
import com.anjuke.aps.message.MessageFilter;
import com.anjuke.aps.server.ApsServerStatusListener;

public class ApsConfig {
    public static final String CONFIG_PATH_KEY = "aps.config.file";

    public static final String APS_HOME_PATH_KEY = "aps.home";

    private static final Logger LOG = LoggerFactory.getLogger(ApsConfig.class);
    private final Map<String, Object> confMap = new HashMap<String, Object>();

    private ApsConfig() {
        String confFile = System.getProperty(CONFIG_PATH_KEY);

        try {
            readConf(confFile);
        } catch (IOException e) {
            throw new IllegalStateException("Read config error", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void readConf(String confFile) throws IOException {
        InputStream is;
        if (confFile == null) {
            LOG.info("Load APS config from classpath:/aps.yaml");
            is = ApsConfig.class.getResourceAsStream("/aps.yaml");
        } else {
            LOG.info("Load APS config from {}", confFile);
            is = new URL(confFile).openStream();
        }
        Yaml yaml = new Yaml();
        Map<String, Object> conf = (Map<String, Object>) yaml.load(is);
        confMap.putAll(conf);
        LOG.debug("Config content: {}", conf);
        is.close();
    }

    public static ApsConfig getInstance() {
        return ConfigHolder.config;
    }

    public static String getApsHome() {
        String home = System.getenv(APS_HOME_PATH_KEY);
        if (home == null || "".equals(home)) {
            return ".";
        }
        return home;
    }

    public int getPort() {
        return ((Number) confMap.get("aps.zmq.server.port")).intValue();
    }

    @SuppressWarnings("unchecked")
    public List<RequestHandler> getRequestHandler() {
        return (List<RequestHandler>) confMap.get("aps.server.request.handler");
    }

    @SuppressWarnings("unchecked")
    public List<MessageFilter> getMessageFilter() {
        return (List<MessageFilter>) confMap.get("aps.server.message.filter");
    }

    @SuppressWarnings("unchecked")
    public List<ApsServerStatusListener> getServerStatusListener(){
        return (List<ApsServerStatusListener>) confMap.get("aps.server.status.listener");
    }

    public static void main(String[] args) {
        ApsConfig.getInstance();
    }

    private static class ConfigHolder {
        static final ApsConfig config = new ApsConfig();
    }

}
