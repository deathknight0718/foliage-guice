/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package page.foliage.inject.spi;

import page.foliage.inject.Binding;
import page.foliage.inject.Provider;
import page.foliage.inject.Scope;

/**
 * Listens for provisioning of objects. Useful for gathering timing information about provisioning,
 * post-provision initialization, and more.
 *
 * @author sameb@google.com (Sam Berlin)
 * @since 4.0
 */
public interface ProvisionListener {

  /**
   * Invoked by Guice when an object requires provisioning. Provisioning occurs when Guice locates
   * and injects the dependencies for a binding. For types bound to a Provider, provisioning
   * encapsulates the {@link Provider#get} method. For toInstance or constant bindings, provisioning
   * encapsulates the injecting of {@literal @}{@code Inject}ed fields or methods. For other types,
   * provisioning encapsulates the construction of the object. If a type is bound within a {@link
   * Scope}, provisioning depends on the scope. Types bound in Singleton scope will only be
   * provisioned once. Types bound in no scope will be provisioned every time they are injected.
   * Other scopes define their own behavior for provisioning.
   *
   * <p>To perform the provision, call {@link ProvisionInvocation#provision()}. If you do not
   * explicitly call provision, it will be automatically done after this method returns. It is an
   * error to call provision more than once.
   */
  <T> void onProvision(ProvisionInvocation<T> provision);

  /**
   * Encapsulates a single act of provisioning.
   *
   * @since 4.0
   */
  public abstract static class ProvisionInvocation<T> {

    /**
     * Returns the Binding this is provisioning.
     *
     * <p>You must not call {@link Provider#get()} on the provider returned by {@link
     * Binding#getProvider}, otherwise you will get confusing error messages.
     */
    public abstract Binding<T> getBinding();

    /** Performs the provision, returning the object provisioned. */
    public abstract T provision();
  }
}
