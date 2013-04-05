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

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.service.spi.BasicServiceInitiator;
import org.hibernate.service.spi.ServiceRegistryImplementor;

/**
 * Workaround for hibernate4 not taking care of either decently registering the driver or nesting the
 * {@link DriverManager#getConnection(String, java.util.Properties)} inside its class loader.
 * <p>
 * Therefore detect the jdbc driver and register it inside this initiator wrapper.
 */
public class CustomJdbcServicesInitiatorWrapper implements BasicServiceInitiator<JdbcServices> {
  private final BasicServiceInitiator<JdbcServices> m_realInitiator;

  @SuppressWarnings("unchecked")
  public CustomJdbcServicesInitiatorWrapper(BasicServiceInitiator<?> realInitiator) {
    m_realInitiator = (BasicServiceInitiator<JdbcServices>) realInitiator;
  }

  @Override
  public Class<JdbcServices> getServiceInitiated() {
    return JdbcServices.class;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public JdbcServices initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
    String driverClassName = (String) configurationValues.get(AvailableSettings.DRIVER);
    registerDriver(driverClassName);
    return m_realInitiator.initiateService(configurationValues, registry);
  }

  private void registerDriver(String driverClassName) {
    if (driverClassName == null) {
      return;
    }
    try {
      DriverManager.registerDriver((Driver) Class.forName(driverClassName).newInstance());
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
}
