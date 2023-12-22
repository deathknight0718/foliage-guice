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
import page.foliage.inject.spi.BindingTargetVisitor;
import page.foliage.inject.spi.Dependency;
import page.foliage.inject.spi.HasDependencies;
import page.foliage.inject.spi.LinkedKeyBinding;

final class LinkedBindingImpl<T> extends BindingImpl<T>
    implements LinkedKeyBinding<T>, HasDependencies {

  final Key<? extends T> targetKey;

  LinkedBindingImpl(
      InjectorImpl injector,
      Key<T> key,
      Object source,
      InternalFactory<? extends T> internalFactory,
      Scoping scoping,
      Key<? extends T> targetKey) {
    super(injector, key, source, internalFactory, scoping);
    this.targetKey = targetKey;
  }

  LinkedBindingImpl(Object source, Key<T> key, Scoping scoping, Key<? extends T> targetKey) {
    super(source, key, scoping);
    this.targetKey = targetKey;
  }

  @Override
  public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> visitor) {
    return visitor.visit(this);
  }

  @Override
  public Key<? extends T> getLinkedKey() {
    return targetKey;
  }

  @Override
  public Set<Dependency<?>> getDependencies() {
    return ImmutableSet.<Dependency<?>>of(Dependency.get(targetKey));
  }

  @Override
  public BindingImpl<T> withScoping(Scoping scoping) {
    return new LinkedBindingImpl<T>(getSource(), getKey(), scoping, targetKey);
  }

  @Override
  public BindingImpl<T> withKey(Key<T> key) {
    return new LinkedBindingImpl<T>(getSource(), key, getScoping(), targetKey);
  }

  @Override
  public void applyTo(Binder binder) {
    getScoping()
        .applyTo(
            withTrustedSource(GUICE_INTERNAL, binder, getSource())
                .bind(getKey())
                .to(getLinkedKey()));
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(LinkedKeyBinding.class)
        .add("key", getKey())
        .add("source", getSource())
        .add("scope", getScoping())
        .add("target", targetKey)
        .toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LinkedBindingImpl) {
      LinkedBindingImpl<?> o = (LinkedBindingImpl<?>) obj;
      return getKey().equals(o.getKey())
          && getScoping().equals(o.getScoping())
          && Objects.equal(targetKey, o.targetKey);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getKey(), getScoping(), targetKey);
  }
}
