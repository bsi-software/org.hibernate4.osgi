/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.hibernate4.osgi.internal;

import javassist.util.proxy.ProxyFactory;

import org.hibernate.cfg.Configuration;
import org.hibernate.service.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate4.osgi.IHibernateWithOsgiFactory;
import org.osgi.framework.Bundle;

public abstract class AbstractFactoryBuilder<F> implements IHibernateWithOsgiFactory<F> {
  private ClassLoader m_cl;

  public AbstractFactoryBuilder(final Bundle[] consumerBundle, Bundle hibernateBundle) {
    if (hibernateBundle == null) {
      hibernateBundle = org.hibernate4.osgi.internal.Activator.getContext().getBundle();
    }
    m_cl = new CustomClassLoader(consumerBundle, hibernateBundle);
  }

  protected BootstrapServiceRegistryBuilder createBootstrapServiceRegistryBuilder() {
    return new BootstrapServiceRegistryBuilder().
        //with(new CustomJdbcServiceIntegrator()).//activate when needed
        withApplicationClassLoader(m_cl).
        withEnvironmentClassLoader(m_cl).
        withHibernateClassLoader(m_cl).
        withResourceClassLoader(m_cl);
  }

  protected abstract ServiceRegistry getServiceRegistry();

  protected abstract Configuration getConfiguration();

  /**
   * just update the schema (add...)
   */
  @Override
  public void updateSchema(final boolean script, final boolean doPhysicalChanges) {
    withClassLoaderDo(new Runnable() {
      @Override
      public void run() {
        new SchemaUpdate(getServiceRegistry(), getConfiguration()).execute(script, doPhysicalChanges);
      }
    });
  }

  /**
   * re-create the schema
   */
  @Override
  public void createSchema(final String filename, final boolean doPhysicalChanges, final boolean justDrop, final boolean justCreate) {
    withClassLoaderDo(new Runnable() {
      @Override
      public void run() {
        SchemaExport x = new SchemaExport(getServiceRegistry(), getConfiguration());
        if (filename != null) {
          x.setOutputFile(filename);
        }
        x.execute(true, doPhysicalChanges, justDrop, justCreate);
      }
    });
  }

  @Override
  public void withClassLoaderDo(Runnable r) {
    final ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
    final ProxyFactory.ClassLoaderProvider oldProvider = ProxyFactory.classLoaderProvider;
    final Thread myThread = Thread.currentThread();
    try {
      myThread.setContextClassLoader(m_cl);
      ProxyFactory.classLoaderProvider = new ProxyFactory.ClassLoaderProvider() {
        @Override
        public ClassLoader get(ProxyFactory proxyfactory) {
          if (Thread.currentThread() == myThread) {
            return m_cl;
          }
          return oldProvider.get(proxyfactory);
        }
      };
      //
      r.run();
    }
    finally {
      myThread.setContextClassLoader(oldCl);
      ProxyFactory.classLoaderProvider = oldProvider;
    }
  }

}
