/*
 * Copyright (C) 2007 Google Inc.
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

import static page.foliage.inject.internal.GuiceInternal.GUICE_INTERNAL;
import static page.foliage.inject.spi.Elements.withTrustedSource;

import java.util.Set;

import page.foliage.guava.common.base.MoreObjects;
import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.inject.Binder;
import page.foliage.inject.Key;
import page.foliage.inject.Provider;
import page.foliage.inject.spi.BindingTargetVisitor;
import page.foliage.inject.spi.Dependency;
import page.foliage.inject.spi.HasDependencies;
import page.foliage.inject.spi.InjectionPoint;
import page.foliage.inject.spi.ProviderInstanceBinding;
import page.foliage.inject.spi.ProviderWithExtensionVisitor;
import page.foliage.inject.util.Providers;

class ProviderInstanceBindingImpl<T> extends BindingImpl<T> implements ProviderInstanceBinding<T> {

  final javax.inject.Provider<? extends T> providerInstance;
  final ImmutableSet<InjectionPoint> injectionPoints;

  public ProviderInstanceBindingImpl(
      InjectorImpl injector,
      Key<T> key,
      Object source,
      InternalFactory<? extends T> internalFactory,
      Scoping scoping,
      javax.inject.Provider<? extends T> providerInstance,
      Set<InjectionPoint> injectionPoints) {
    super(injector, key, source, internalFactory, scoping);
    this.providerInstance = providerInstance;
    this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
  }

  public ProviderInstanceBindingImpl(
      Object source,
      Key<T> key,
      Scoping scoping,
      Set<InjectionPoint> injectionPoints,
      javax.inject.Provider<? extends T> providerInstance) {
    super(source, key, scoping);
    this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
    this.providerInstance = providerInstance;
  }

  @Override
  public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
    if (providerInstance instanceof ProviderWithExtensionVisitor) {
      return ((ProviderWithExtensionVisitor<? extends T>) providerInstance)
          .acceptExtensionVisitor(visitor, this);
    } else {
      return visitor.visit(this);
    }
  }

  public Provider<? extends T> getProviderInstance() {
    return Providers.guicify(providerInstance);
  }

  @Override
  public javax.inject.Provider<? extends T> getUserSuppliedProvider() {
    return providerInstance;
  }

  @Override
  public Set<InjectionPoint> getInjectionPoints() {
    return injectionPoints;
  }

  @Override
  public Set<Dependency<?>> getDependencies() {
    return providerInstance instanceof HasDependencies
        ? ImmutableSet.copyOf(((HasDependencies) providerInstance).getDependencies())
        : Dependency.forInjectionPoints(injectionPoints);
  }

  @Override
  public BindingImpl<T> withScoping(Scoping scoping) {
    return new ProviderInstanceBindingImpl<T>(
        getSource(), getKey(), scoping, injectionPoints, providerInstance);
  }

  @Override
  public BindingImpl<T> withKey(Key<T> key) {
    return new ProviderInstanceBindingImpl<T>(
        getSource(), key, getScoping(), injectionPoints, providerInstance);
  }

  @Override
  public void applyTo(Binder binder) {
    getScoping()
        .applyTo(
            withTrustedSource(GUICE_INTERNAL, binder, getSource())
                .bind(getKey())
                .toProvider(getUserSuppliedProvider()));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ProviderInstanceBinding.class)
        .add("key", getKey())
        .add("source", getSource())
        .add("scope", getScoping())
        .add("provider", providerInstance)
        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ProviderInstanceBindingImpl) {
      ProviderInstanceBindingImpl<?> o = (ProviderInstanceBindingImpl<?>) obj;
      return getKey().equals(o.getKey())
          && getScoping().equals(o.getScoping())
          && Objects.equal(providerInstance, o.providerInstance);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getKey(), getScoping());
  }
}
