package org.alauer.simplecontainer;

import org.alauer.simplecontainer.servlet.config.WebappConfiguration;

public interface WebappLoader {

    void init();

    void start();

    ClassLoader getClassLoader();

    WebappConfiguration getWebappConfiguration();

}
