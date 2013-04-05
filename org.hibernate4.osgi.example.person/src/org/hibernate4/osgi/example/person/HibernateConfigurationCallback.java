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

import org.hibernate.cfg.Configuration;
import org.hibernate4.osgi.IConfigurationCallback;
import org.hibernate4.osgi.example.person.entity.Event;
import org.hibernate4.osgi.example.person.entity.Person;

public class HibernateConfigurationCallback implements IConfigurationCallback {

	@Override
	public Configuration initialize(Configuration configuration) {
		configuration.setProperty("hibernate.current_session_context_class", "thread");
		configuration.setProperty("hibernate.ejb.metamodel.population", "disabled");

		// DB Connection
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
		configuration.setProperty("hibernate.connection.url", "jdbc:h2:mem:");
		configuration.setProperty("hibernate.connection.username", "");
		configuration.setProperty("hibernate.connection.password", "");

		configuration.setProperty("hibernate.hbm2ddl.auto", "update");

		configuration.setProperty("hibernate.show_sql", "true");

		// Register entitiy classes
		configuration.addAnnotatedClass(Person.class);
		configuration.addAnnotatedClass(Event.class);

		return configuration;
	}
}
