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

import java.lang.reflect.Field;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate4.osgi.IConfigurationCallback;
import org.osgi.framework.Bundle;

public class HibernateProprietaryFactoryBuilder extends AbstractFactoryBuilder<SessionFactory> {
  private Configuration m_configuration;
  private SessionFactory m_sessionFactory;

  public HibernateProprietaryFactoryBuilder(final Bundle consumerBundle, Bundle hibernateBundle, final IConfigurationCallback configCallback) {
    super(new Bundle[]{consumerBundle}, hibernateBundle);
    withClassLoaderDo(new Runnable() {
      @Override
      public void run() {
        initialize(consumerBundle, configCallback);
      }
    });
  }

  protected void initialize(Bundle consumerBundle, IConfigurationCallback configCallback) {
    BootstrapServiceRegistryBuilder bootstrapServiceRegBuilder = createBootstrapServiceRegistryBuilder();

    ServiceRegistry serviceRegistry = new ServiceRegistryBuilder(bootstrapServiceRegBuilder.build()).buildServiceRegistry();

    m_configuration = new Configuration();
    for (Field f : AvailableSettings.class.getFields()) {
      try {
        String key = (String) f.get(null);
        String value = consumerBundle.getBundleContext().getProperty(key);
        if (value == null) {
          continue;
        }
        m_configuration = m_configuration.setProperty(key, value);
      }
      catch (Throwable t) {
        t.printStackTrace();
      }
    }
    m_configuration = configCallback.initialize(m_configuration);

    m_sessionFactory = m_configuration.buildSessionFactory(serviceRegistry);
  }

  @Override
  protected ServiceRegistry getServiceRegistry() {
    return ((SessionFactoryImplementor) m_sessionFactory).getServiceRegistry();
  }

  @Override
  protected Configuration getConfiguration() {
    return m_configuration;
  }

  @Override
  public SessionFactory getSessionFactoryImpl() {
    return m_sessionFactory;
  }
}
