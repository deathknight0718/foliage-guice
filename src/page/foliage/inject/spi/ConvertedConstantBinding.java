/**
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

package page.foliage.inject.spi;

import java.util.Set;

import page.foliage.inject.spi.Dependency;
import page.foliage.inject.spi.HasDependencies;
import page.foliage.inject.spi.TypeConverterBinding;

import page.foliage.inject.Binding;
import page.foliage.inject.Key;

/**
 * A binding created from converting a bound instance to a new type. The source binding has the same
 * binding annotation but a different type.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 * @since 2.0
 */
public interface ConvertedConstantBinding<T> extends Binding<T>, HasDependencies {

  /**
   * Returns the converted value.
   */
  T getValue();

  /**
   * Returns the type converter binding used to convert the constant.
   * 
   * @since 3.0
   */
  TypeConverterBinding getTypeConverterBinding();

  /**
   * Returns the key for the source binding. That binding can be retrieved from an injector using
   * {@link page.foliage.inject.Injector#getBinding(Key) Injector.getBinding(key)}.
   */
  Key<String> getSourceKey();

  /**
   * Returns a singleton set containing only the converted key.
   */
  Set<Dependency<?>> getDependencies();
}
