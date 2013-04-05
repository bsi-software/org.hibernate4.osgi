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
package org.hibernate4.osgi.example.person;

import javax.persistence.EntityManagerFactory;

import org.hibernate4.osgi.HibernateWithOsgiFactoryBuilder;
import org.hibernate4.osgi.IHibernateWithOsgiFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static IHibernateWithOsgiFactory<EntityManagerFactory> persistenceFactory;

	public static BundleContext getContext() {
		return context;
	}

	public static IHibernateWithOsgiFactory<EntityManagerFactory> getPersistenceFactory() {
		return persistenceFactory;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		Bundle[] consumerBundles = new Bundle[] { getContext().getBundle() };
		persistenceFactory = HibernateWithOsgiFactoryBuilder
				.createPersistenceFactory(consumerBundles, null,
						new HibernateConfigurationCallback());

		new Application().start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;

		persistenceFactory.getSessionFactoryImpl().close();
		persistenceFactory = null;
	}
}
