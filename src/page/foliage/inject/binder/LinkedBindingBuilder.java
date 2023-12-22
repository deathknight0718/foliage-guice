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

package page.foliage.inject.binder;

import java.lang.reflect.Constructor;

import page.foliage.inject.Key;
import page.foliage.inject.Provider;
import page.foliage.inject.TypeLiteral;

/**
 * See the EDSL examples at {@link page.foliage.inject.Binder}.
 *
 * @author crazybob@google.com (Bob Lee)
 */
public interface LinkedBindingBuilder<T> extends ScopedBindingBuilder {

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder to(Class<? extends T> implementation);

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder to(TypeLiteral<? extends T> implementation);

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder to(Key<? extends T> targetKey);

  /**
   * See the EDSL examples at {@link page.foliage.inject.Binder}.
   *
   * @see page.foliage.inject.Injector#injectMembers
   */
  void toInstance(T instance);

  /**
   * See the EDSL examples at {@link page.foliage.inject.Binder}.
   *
   * @see page.foliage.inject.Injector#injectMembers
   */
  ScopedBindingBuilder toProvider(Provider<? extends T> provider);

  /**
   * See the EDSL examples at {@link page.foliage.inject.Binder}.
   *
   * @see page.foliage.inject.Injector#injectMembers
   * @since 4.0
   */
  ScopedBindingBuilder toProvider(javax.inject.Provider<? extends T> provider);

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder toProvider(Class<? extends javax.inject.Provider<? extends T>> providerType);

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder toProvider(
      TypeLiteral<? extends javax.inject.Provider<? extends T>> providerType);

  /** See the EDSL examples at {@link page.foliage.inject.Binder}. */
  ScopedBindingBuilder toProvider(Key<? extends javax.inject.Provider<? extends T>> providerKey);

  /**
   * See the EDSL examples at {@link page.foliage.inject.Binder}.
   *
   * @since 3.0
   */
  <S extends T> ScopedBindingBuilder toConstructor(Constructor<S> constructor);

  /**
   * See the EDSL examples at {@link page.foliage.inject.Binder}.
   *
   * @since 3.0
   */
  <S extends T> ScopedBindingBuilder toConstructor(
      Constructor<S> constructor, TypeLiteral<? extends S> type);
}
