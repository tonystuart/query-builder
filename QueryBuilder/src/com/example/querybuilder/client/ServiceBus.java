// Copyright 2011 Anthony F. Stuart - All rights reserved.
//
// This program and the accompanying materials are made available
// under the terms of the GNU General Public License. For other license
// options please contact the copyright owner.
//
// This program is made available on an "as is" basis, without
// warranties or conditions of any kind, either express or implied.

package com.example.querybuilder.client;

import java.util.LinkedList;
import java.util.List;

public class ServiceBus
{
  private static List<ServiceProvider> serviceProviders = new LinkedList<ServiceProvider>();

  /**
   * Adds a service provider to the service bus. Be sure to invoke
   * {@link removeServiceProvider} when the service provider goes out of
   * scope to prevent memory leaks and unexpected effects.
   */
  public static void addServiceProvider(ServiceProvider serviceProvider)
  {
    serviceProviders.add(serviceProvider);
  }

  public static void post(ServiceRequest serviceRequest)
  {
    for (ServiceProvider serviceProvider : serviceProviders)
    {
      serviceProvider.onServiceRequest(serviceRequest);
    }
  }

  public static void removeServiceProvider(ServiceProvider serviceProvider)
  {
    serviceProviders.remove(serviceProvider);
  }

  public interface ServiceProvider
  {
    public void onServiceRequest(ServiceRequest serviceRequest);
  }

  public static class ServiceRequest
  {
  }

}
