/* $Id$
 * 
 * Copyright 2007 BEKK Consulting
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */
package com.bekk.boss.pluto.embedded.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.pluto.driver.PortalDriverFilter;

public class PlutoPortalDriverFilter implements Filter {

	private PortalDriverFilter portalDriver;
	
	private FilterConfig filterConfig;
	
	private String[] portletIds;

	private String[] styles = new String[0];

	public PlutoPortalDriverFilter() {
		this.portalDriver = new PortalDriverFilter();
	}

	public void destroy() {
		portalDriver.destroy();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		request.setAttribute("org_apache_pluto_embedded_portletIds", portletIds);
		request.setAttribute("org_apache_pluto_embedded_extraStyles", styles);
		portalDriver.doFilter(request, response, chain);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		portalDriver.init(filterConfig);
		this.filterConfig = filterConfig;
		String ids = System.getProperty("org.apache.pluto.embedded.portletIds");
		String extraStyles = System.getProperty("org.apache.pluto.embedded.extraStyles");
		if (extraStyles != null) {
			styles = extraStyles.split(",");
		}
		if (ids == null || "".equals(ids.trim())) {
			throw new ServletException(
					"No portlet id specified. Please set the system property \"org.apache.pluto.embedded.portletIds\"");
		}
		portletIds = encodePortletIds(ids.split(","));
	}
	
	private String[] encodePortletIds(String[] rawIds) {
		String[] ids = new String[rawIds.length];
		String contextPath = filterConfig.getServletContext().getContextPath();
		StringBuffer tempId = new StringBuffer();
		if (!contextPath.startsWith("/")) {
			tempId.append("/");
		}
		for(int i = 0; i < rawIds.length; i++) {
			ids[i] = contextPath + "." + rawIds[i] + "!";
		}
		return ids;
	}

}
