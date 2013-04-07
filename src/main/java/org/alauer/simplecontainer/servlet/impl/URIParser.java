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


public class URIParser {

    private FilterChainImpl chain;

    private String servletPath;

    private String requestUri;

    private String pathInfo;

    private String queryString;

    public URIParser(FilterChainImpl chain) {
        this.chain = chain;
    }

    public void parse(String uri) {
        if (this.chain.getServletConfiguration() == null)
            this.servletPath = "/";
        else
            this.servletPath = this.chain.getServletConfiguration().getMatchingUrlPattern(uri);

        int indx = uri.indexOf('?');
        if (!this.servletPath.startsWith("/"))
            this.servletPath = "/" + this.servletPath;

        if (indx != -1) {
            this.pathInfo = uri.substring(servletPath.length(), indx);
            this.queryString = uri.substring(indx + 1);
            this.requestUri = uri.substring(0, indx);
        } else {
            this.pathInfo = uri.substring(servletPath.length());
            this.requestUri = uri;
        }

        if (this.requestUri.endsWith("/"))
            this.requestUri.substring(0, this.requestUri.length() - 1);

        if (this.pathInfo.equals(""))
            this.pathInfo = null;
        else if (!this.pathInfo.startsWith("/"))
            this.pathInfo = "/" + this.pathInfo;

    }

    public String getServletPath() {
        return servletPath;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getPathInfo() {
        return this.pathInfo;
    }

    public String getRequestUri() {
        return requestUri;
    }

}
