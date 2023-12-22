/*
 * Copyright (C) 2008 Google Inc.
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
import page.foliage.inject.spi.BindingTargetVisitor;
import page.foliage.inject.spi.Dependency;
import page.foliage.inject.spi.HasDependencies;
import page.foliage.inject.spi.InjectionPoint;
import page.foliage.inject.spi.InstanceBinding;

final class InstanceBindingImpl<T> extends BindingImpl<T> implements InstanceBinding<T> {

  final T instance;
  final ImmutableSet<InjectionPoint> injectionPoints;

  public InstanceBindingImpl(
      InjectorImpl injector,
      Key<T> key,
      Object source,
      InternalFactory<? extends T> internalFactory,
      Set<InjectionPoint> injectionPoints,
      T instance) {
    super(injector, key, source, internalFactory, Scoping.EAGER_SINGLETON);
    this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
    this.instance = instance;
  }

  public InstanceBindingImpl(
      Object source, Key<T> key, Scoping scoping, Set<InjectionPoint> injectionPoints, T instance) {
    super(source, key, scoping);
    this.injectionPoints = ImmutableSet.copyOf(injectionPoints);
    this.instance = instance;
  }

  @Override
  public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
    return visitor.visit(this);
  }

  @Override
  public T getInstance() {
    return instance;
  }

  @Override
  public Set<InjectionPoint> getInjectionPoints() {
    return injectionPoints;
  }

  @Override
  public Set<Dependency<?>> getDependencies() {
    return instance instanceof HasDependencies
        ? ImmutableSet.copyOf(((HasDependencies) instance).getDependencies())
        : Dependency.forInjectionPoints(injectionPoints);
  }

  @Override
  public BindingImpl<T> withScoping(Scoping scoping) {
    return new InstanceBindingImpl<T>(getSource(), getKey(), scoping, injectionPoints, instance);
  }

  @Override
  public BindingImpl<T> withKey(Key<T> key) {
    return new InstanceBindingImpl<T>(getSource(), key, getScoping(), injectionPoints, instance);
  }

  @Override
  public void applyTo(Binder binder) {
    // instance bindings aren't scoped
    withTrustedSource(GUICE_INTERNAL, binder, getSource()).bind(getKey()).toInstance(instance);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(InstanceBinding.class)
        .add("key", getKey())
        .add("source", getSource())
        .add("instance", instance)
        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof InstanceBindingImpl) {
      InstanceBindingImpl<?> o = (InstanceBindingImpl<?>) obj;
      return getKey().equals(o.getKey())
          && getScoping().equals(o.getScoping())
          && Objects.equal(instance, o.instance);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getKey(), getScoping());
  }
}
