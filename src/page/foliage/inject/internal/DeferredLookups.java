/*
 * Copyright (C) 2009 Google Inc.
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

import java.util.List;

import page.foliage.guava.common.collect.Lists;
import page.foliage.inject.Key;
import page.foliage.inject.MembersInjector;
import page.foliage.inject.Provider;
import page.foliage.inject.TypeLiteral;
import page.foliage.inject.spi.Element;
import page.foliage.inject.spi.MembersInjectorLookup;
import page.foliage.inject.spi.ProviderLookup;

/**
 * Returns providers and members injectors that haven't yet been initialized. As a part of injector
 * creation it's necessary to {@link #initialize initialize} these lookups.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
final class DeferredLookups implements Lookups {
  private final InjectorImpl injector;
  private final List<Element> lookups = Lists.newArrayList();

  DeferredLookups(InjectorImpl injector) {
    this.injector = injector;
  }

  /** Initialize the specified lookups, either immediately or when the injector is created. */
  void initialize(Errors errors) {
    injector.lookups = injector;
    new LookupProcessor(errors).process(injector, lookups);
  }

  @Override
  public <T> Provider<T> getProvider(Key<T> key) {
    ProviderLookup<T> lookup = new ProviderLookup<>(key, key);
    lookups.add(lookup);
    return lookup.getProvider();
  }

  @Override
  public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> type) {
    MembersInjectorLookup<T> lookup = new MembersInjectorLookup<>(type, type);
    lookups.add(lookup);
    return lookup.getMembersInjector();
  }
}
