org.hibernate4.osgi
===================

Proposal: Hibernate 4 &amp; OSGi


How to lunch the sample
-----------------------
The following instructions assume that you have imported the following projects into an Eclipse workspace:

* org.hibernate4.lib.fragment
* org.hibernate4.osgi
* org.hibernate4.osgi.example.person
* org.hibernate4.osgi.h2.fragment

Instructions:

1. Open "Run configurations..." and create a new "OSGi Framework" run configuration

2. Choose all four OSGi bundles from the workspace plus "org.eclipse.osgi" wich should come with your Target Platform
3. Finally you're ready to lunch the application. Along with several log messages from Hibernate you should see the fllowing output on the console:
   
        Hibernate: insert into Person (firstname, lastname, id) values (?, ?, ?)
        Hibernate: insert into Event (date, person_id, title, id) values (?, ?, ?, ?)
        Hibernate: insert into Event (date, person_id, title, id) values (?, ?, ?, ?)
        Hibernate: select person0_.id as id0_, person0_.firstname as firstname0_, person0_.lastname as lastname0_ from Person person0_
        Person [Id= 1, Firstname=Scott, Lastname=Tiger]
	        Event [Id= 1, Title=Meet Alice, Date=Thu Apr 04 22:16:43 CEST 2013]
	        Event [Id= 2, Title=Meet Bob, Date=Thu Apr 04 22:16:43 CEST 2013]
