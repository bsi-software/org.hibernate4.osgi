/*******************************************************************************
 * Copyright (c) 2013 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSI CRM Software License v1.0
 * which accompanies this distribution as bsi-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package org.hibernate4.osgi.internal;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.service.StandardServiceInitiators;
import org.hibernate.service.spi.BasicServiceInitiator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 *
 */
public class CustomJdbcServiceIntegrator implements ServiceContributingIntegrator {

  @Override
  public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
  }

  @Override
  public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
  }

  @Override
  public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
  }

  @Override
  public void prepareServices(ServiceRegistryBuilder serviceRegistryBuilder) {
    /*
     * Note: since hibernate class loading is sometimes not taking care of class loader structures.
     * Therefore wrap the jdbc initiator in order to register jdbc driver
     */
    for (BasicServiceInitiator<?> init : StandardServiceInitiators.LIST) {
      if (JdbcServices.class == init.getServiceInitiated()) {
        serviceRegistryBuilder.addInitiator(new CustomJdbcServicesInitiatorWrapper(init));
        break;
      }
    }
  }

}
