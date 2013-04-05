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

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate4.osgi.example.person.entity.Event;
import org.hibernate4.osgi.example.person.entity.Person;

public class Application {

	private EntityManager entityManager;

	public void start() {
		entityManager = Activator.getPersistenceFactory().getSessionFactoryImpl().createEntityManager();
		entityManager.getTransaction().begin();
		try {
			doPersistanceLogic();
			entityManager.flush();
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			entityManager.getTransaction().rollback();
		}
		entityManager.close();
	}

	private void doPersistanceLogic() {
		persistPerson();
		persistAndAddEvents();
		findAndPrintPersonAndEvents();
	}


	private void persistPerson() {
		Person person = new Person();
		person.setId(1L);
		person.setFirstname("Scott");
		person.setLastname("Tiger");

		entityManager.persist(person);
	}

	private void persistAndAddEvents() {
		Person person = entityManager.find(Person.class, 1L);

		Event event1 = new Event();
		event1.setId(1L);
		event1.setDate(new Date());
		event1.setTitle("Meet Alice");
		event1.setPerson(person);
		person.getEvents().add(event1);
		entityManager.persist(event1);

		Event event2 = new Event();
		event2.setId(2L);
		event2.setDate(new Date());
		event2.setTitle("Meet Bob");
		event2.setPerson(person);
		person.getEvents().add(event2);
		entityManager.persist(event2);
	}

	private void findAndPrintPersonAndEvents() {
		@SuppressWarnings("unchecked")
		List<Person> persons = entityManager.createQuery("from Person").getResultList();
		for (Person person : persons) {
			System.out.println("Person [Id= "+person.getId()+", Firstname="+person.getFirstname()+", Lastname="+person.getLastname()+"]");
			for (Event event : person.getEvents()) {
				System.out.println("\tEvent [Id= "+event.getId()+", Title="+event.getTitle()+", Date="+event.getDate()+"]");
			}
		}
	}
}
