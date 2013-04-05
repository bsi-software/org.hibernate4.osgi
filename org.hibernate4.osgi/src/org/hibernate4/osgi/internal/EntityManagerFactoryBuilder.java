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

import javax.persistence.EntityManagerFactory;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.BootstrapServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate4.osgi.IConfigurationCallback;
import org.osgi.framework.Bundle;

@SuppressWarnings("deprecation")
public class EntityManagerFactoryBuilder extends AbstractFactoryBuilder<EntityManagerFactory> {
  private Configuration m_configuration;
  private EntityManagerFactory m_sessionFactory;

  public EntityManagerFactoryBuilder(final Bundle[] consumerBundle, Bundle hibernateBundle, final IConfigurationCallback configCallback) {
    super(consumerBundle, hibernateBundle);
    withClassLoaderDo(new Runnable() {
      @Override
      public void run() {
        initialize(consumerBundle, configCallback);
      }
    });
  }

  protected void initialize(Bundle[] consumerBundle, IConfigurationCallback configCallback) {
    BootstrapServiceRegistryBuilder bootstrapServiceRegBuilder = createBootstrapServiceRegistryBuilder();

    Ejb3Configuration ejbConfiguration = new Ejb3Configuration();
    m_configuration = ejbConfiguration.getHibernateConfiguration();
    for (Field f : AvailableSettings.class.getFields()) {
      try {
        String key = (String) f.get(null);
        // FIXME mot do a loop (or at least a check if array length > 0)
        String value = consumerBundle[0].getBundleContext().getProperty(key);
        if (value == null) {
          continue;
        }
        ejbConfiguration.setProperty(key, value);
      }
      catch (Throwable t) {
        t.printStackTrace();
      }
    }
    configCallback.initialize(ejbConfiguration.getHibernateConfiguration());

    m_sessionFactory = ejbConfiguration.buildEntityManagerFactory(bootstrapServiceRegBuilder);
  }

  @Override
  protected ServiceRegistry getServiceRegistry() {
    return ((SessionFactoryImplementor) ((HibernateEntityManagerFactory) m_sessionFactory).getSessionFactory()).getServiceRegistry();
  }

  @Override
  protected Configuration getConfiguration() {
    return m_configuration;
  }

  @Override
  public EntityManagerFactory getSessionFactoryImpl() {
    return m_sessionFactory;
  }
}
