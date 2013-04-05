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
package org.hibernate4.osgi;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate4.osgi.internal.EntityManagerFactoryBuilder;
import org.hibernate4.osgi.internal.HibernateProprietaryFactoryBuilder;
import org.osgi.framework.Bundle;

public final class HibernateWithOsgiFactoryBuilder {

  private HibernateWithOsgiFactoryBuilder() {
  }

  /**
   * Build a proprietary hibernate context based on a {@link SessionFactory}.
   * <p>
   * Settings in the products config.ini are automatically set onto the {@link Configuration}.
   * 
   * @param consumerBundle
   *          is the bundle where all persistent beans are locatable and where the class loader starts looking for
   *          classes.
   * @param hibernateBundle
   *          the bundle containing the hibernate libs. When null is passed, this bundle itself is used.
   * @param configCallback
   *          used to add entities and custom properties on the {@link Configuration}
   */
  public static IHibernateWithOsgiFactory<EntityManagerFactory> createPersistenceFactory(Bundle[] consumerBundle, Bundle hibernateBundle, IConfigurationCallback configCallback) {
    return new EntityManagerFactoryBuilder(consumerBundle, hibernateBundle, configCallback);
  }

  /**
   * Build a JPA compliant context based on a {@link EntityManagerFactory}.
   * <p>
   * Settings in the products config.ini are automatically set onto the {@link Configuration}.
   * 
   * @param consumerBundle
   *          is the bundle where all persistent beans are locatable and where the class loader starts looking for
   *          classes.
   * @param hibernateBundle
   *          the bundle containing the hibernate libs. When null is passed, this bundle itself is used.
   * @param configCallback
   *          used to add entities and custom properties on the {@link Configuration}
   */
  public static IHibernateWithOsgiFactory<SessionFactory> createHibernateFactory(Bundle consumerBundle, Bundle hibernateBundle, IConfigurationCallback configCallback) {
    return new HibernateProprietaryFactoryBuilder(consumerBundle, hibernateBundle, configCallback);
  }

}
