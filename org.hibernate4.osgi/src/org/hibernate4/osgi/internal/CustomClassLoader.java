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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.osgi.framework.Bundle;

/**
 * This class must be located inside the bundle that provides orm mappings and configs
 */
public class CustomClassLoader extends ClassLoader {
  private final Bundle m_hibernateBundle;
  private final Bundle[] m_consumerBundle;
  private final Hashtable<String, Class<?>> m_classCache = new Hashtable<String, Class<?>>();

  public CustomClassLoader(Bundle[] consumerBundle, Bundle hibernateBundle) {
    m_consumerBundle = consumerBundle;
    m_hibernateBundle = hibernateBundle;
  }

  private Class<?> putInCache(String name, Class<?> c) {
    if (c != null) {
      m_classCache.put(name, c);
    }
    return c;
  }

  @Override
  public Class<?> loadClass(String className) throws ClassNotFoundException {
    Class<?> c = m_classCache.get(className);
    if (c != null) {
      return c;
    }
    if (m_classCache.containsKey(className)) {
      throw new ClassNotFoundException(className);
    }
    // System.out.println("CL.classForName " + className);
    if (m_consumerBundle != null) {
      for (Bundle bundle : m_consumerBundle)
      {
        try {
          c = bundle.loadClass(className);
          return putInCache(className, c);
        }
        catch (Exception e) {
          //nop
        }
      }
    }
    try {
      c = m_hibernateBundle.loadClass(className);
      return putInCache(className, c);
    }
    catch (Exception e) {
      //nop
    }
    try {
      c = Class.forName(className);
      return putInCache(className, c);
    }
    catch (Exception e) {
      //nop
    }
    putInCache(className, null);
    //System.out.println("CL.classForName " + className + " ERROR");
    throw new ClassNotFoundException(className);
  }

  @Override
  public URL getResource(String name) {
    //System.out.println("CL.getResource " + name);
    // try name as URL
    try {
      return new URL(name);
    }
    catch (Exception e) {
      //nop
    }
    if (m_consumerBundle != null) {
      for (Bundle bundle : m_consumerBundle)
      {
        try {
          return bundle.getResource(name);
        }
        catch (Exception e) {
          // nop
        }
      }
    }
    try {
      return m_hibernateBundle.getResource(name);
    }
    catch (Exception e) {
      //nop
    }
    //System.out.println("CL.locateResource " + name + " ERROR");
    return null;
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    //System.out.println("CL.getResourceStream " + name);
    try {
      URL u = getResource(name);
      if (u != null) {
        return u.openStream();
      }
    }
    catch (Exception e) {
      //nop
    }
    //System.out.println("CL.getResourceAsStream " + name + " ERROR");
    return null;
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    //System.out.println("CL.getResources " + name);
    Vector<URL> urlList = new Vector<URL>();

    // Consumer bundle.
    if (m_consumerBundle != null) {
      for (Bundle bundle : m_consumerBundle) {
        try {
          Enumeration<URL> en = bundle.getResources(name);
          if (en != null && en.hasMoreElements()) {
            while (en.hasMoreElements()) {
              URL url = en.nextElement();
              //System.out.println(" found " + url);
              urlList.add(url);
            }
          }
        }
        catch (Exception e) {
          //nop
        }
      }
    }

    // Hibernate bundle.
    try {
      Enumeration<URL> en = m_hibernateBundle.getResources(name);
      if (en != null && en.hasMoreElements()) {
        while (en.hasMoreElements()) {
          URL url = en.nextElement();
          //System.out.println(" found " + url);
          urlList.add(url);
        }
      }
    }
    catch (Exception e) {
      //nop
    }

    return urlList.elements();
  }
}
