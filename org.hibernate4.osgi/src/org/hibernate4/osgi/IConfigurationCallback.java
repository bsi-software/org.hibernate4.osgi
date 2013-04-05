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

import org.hibernate.cfg.Configuration;

/**
 * called by {@link HibernateWithOsgiFactoryBuilder} in order to initialize the configurations
 */
public interface IConfigurationCallback {

  /**
   * initialize the hibernate proprietary config
   */
  Configuration initialize(Configuration config);

}
