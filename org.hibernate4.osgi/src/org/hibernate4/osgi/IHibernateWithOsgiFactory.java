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

/**
 * S is either {@link SessionFactory} or {@link EntityManagerFactory} depending on the factory method used.
 */
public interface IHibernateWithOsgiFactory<F> {

  F getSessionFactoryImpl();

  /**
   * just update the schema (add...)
   */
  void updateSchema(final boolean script, final boolean doPhysicalChanges);

  /**
   * re-create the schema
   */
  void createSchema(final String filename, final boolean doPhysicalChanges, final boolean justDrop, final boolean justCreate);

  /**
   * run some code inside the osgi class loader for hibernate 4
   */
  void withClassLoaderDo(Runnable r);

}
