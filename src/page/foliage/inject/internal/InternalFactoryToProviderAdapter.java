/*
 * Copyright (C) 2006 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package page.foliage.inject.internal;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import page.foliage.inject.Provider;
import page.foliage.inject.spi.Dependency;

/** @author crazybob@google.com (Bob Lee) */
final class InternalFactoryToProviderAdapter<T> implements InternalFactory<T> {

  private final Provider<? extends T> provider;
  private final Object source;

  public InternalFactoryToProviderAdapter(Provider<? extends T> provider, Object source) {
    this.provider = checkNotNull(provider, "provider");
    this.source = checkNotNull(source, "source");
  }

  @Override
  public T get(InternalContext context, Dependency<?> dependency, boolean linked)
      throws InternalProvisionException {
    // Set the dependency here so it is available to scope implementations (such as SingletonScope)
    // The reason we need this is so that Scope implementations (and scope delegate providers) can
    // create proxies of super-interfaces to support cyclic dependencies.  It would be nice to
    // drop the setDependency method (and field), but that could only happen if cyclic proxies
    // were also dropped.
    context.setDependency(dependency);
    try {
      T t = provider.get();
      if (t == null && !dependency.isNullable()) {
        InternalProvisionException.onNullInjectedIntoNonNullableDependency(source, dependency);
      }
      return t;
    } catch (RuntimeException userException) {
      throw InternalProvisionException.errorInProvider(userException).addSource(source);
    }
  }

  @Override
  public String toString() {
    return provider.toString();
  }
}
