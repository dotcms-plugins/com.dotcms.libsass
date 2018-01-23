/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dotmarketing.osgi.servlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.util.Config;

public final class Activator implements BundleActivator {

    private ServletRegistration servReg = new ServletRegistration(new LibsassServlet(), "/sass");


    @Override
    public void start(BundleContext context) throws Exception {


        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                FilterWebInterceptorProvider.getInstance(Config.CONTEXT);
        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);

        // delegate.addFirst(filterReg);

        delegate.add(servReg);

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                FilterWebInterceptorProvider.getInstance(Config.CONTEXT);
        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(AutoLoginFilter.class);
        if (null != delegate) {
            System.out.println("Removing the ExampleAutoLoginWebInterceptor");
            delegate.remove(servReg.getName(), true);
            // delegate.remove(filterReg.getName(),true);
        }



    }
}
