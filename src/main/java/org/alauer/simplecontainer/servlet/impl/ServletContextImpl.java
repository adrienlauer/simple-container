/*
 * Copyright 2013 by Maxim Kalina
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.alauer.simplecontainer.servlet.impl;

import org.alauer.simplecontainer.servlet.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@SuppressWarnings("unchecked")
public class ServletContextImpl extends ConfigAdapter implements ServletContext {

    private static final Logger log = LoggerFactory
            .getLogger(ServletContextImpl.class);

    private static ServletContextImpl instance;

    private Map<String, Object> attributes;

    private String servletContextName;

    public static final ServletContextImpl get() {
        if (instance == null)
            instance = new ServletContextImpl();

        return instance;
    }

    private ServletContextImpl() {
        super("Netty Servlet Bridge");
    }

    @Override
    public Object getAttribute(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return Utils.enumerationFromKeys(attributes);
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public int getMajorVersion() {
        return 2;
    }

    @Override
    public int getMinorVersion() {
        return 4;
    }

    @Override
    public URL getResource(String path) throws MalformedURLException {
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    @Override
    public InputStream getResourceAsStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    @Override
    public String getServerInfo() {
        return super.getOwnerName();
    }

    @Override
    public void log(String msg) {
        log.info(msg);
    }

    @Override
    public void log(Exception exception, String msg) {
        log.error(msg, exception);
    }

    @Override
    public void log(String message, Throwable throwable) {
        log.error(message, throwable);
    }

    @Override
    public void removeAttribute(String name) {
        if (this.attributes != null)
            this.attributes.remove(name);
    }

    @Override
    public void setAttribute(String name, Object object) {
        if (this.attributes == null)
            this.attributes = new HashMap<String, Object>();

        this.attributes.put(name, object);
    }

    @Override
    public String getServletContextName() {
        return this.servletContextName;
    }

    void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    @Override
    public Servlet getServlet(String name) throws ServletException {
        throw new IllegalStateException(
                "Deprecated as of Java Servlet API 2.1, with no direct replacement!");
    }

    @Override
    public Enumeration getServletNames() {
        throw new IllegalStateException(
                "Method 'getServletNames' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public Enumeration getServlets() {
        throw new IllegalStateException(
                "Method 'getServlets' deprecated as of Java Servlet API 2.0, with no replacement.");
    }

    @Override
    public ServletContext getContext(String uripath) {
        return this;
    }

    @Override
    public String getMimeType(String file) {
        return Utils.getMimeType(file);

    }

    @Override
    public Set getResourcePaths(String path) {
        File explodedWarFolder = ServletBridgeWebapp.get().getWebappConfig().getExplodedWarFolder();
        if (explodedWarFolder != null) {
            Set<String> paths = new HashSet<String>();
            int length = explodedWarFolder.getAbsolutePath().length();
            File[] files = new File(explodedWarFolder, path).listFiles();

            if (files != null) {
                for (File file : files)
                    paths.add(file.getAbsolutePath().substring(length).replace(File.separatorChar, '/'));
            }
            return paths;
        } else
            throw new IllegalStateException("Cannot compute 'getResourcePaths', exploded war folder has not been set!");
    }

    @Override
    public RequestDispatcher getNamedDispatcher(String name) {
        throw new IllegalStateException(
                "Method 'getNamedDispatcher' not yet implemented!");
    }

    @Override
    public String getRealPath(String path) {
        File staticResourceFolder = ServletBridgeWebapp.get().getStaticResourcesFolder();
        return (staticResourceFolder != null && path != null ? ServletBridgeWebapp.get()
                .getStaticResourcesFolder().getAbsolutePath()
                + File.separator + path : null);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new IllegalStateException(
                "Method 'getRequestDispatcher' not yet implemented!");
    }

}
