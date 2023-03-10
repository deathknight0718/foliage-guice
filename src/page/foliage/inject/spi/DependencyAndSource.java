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

import java.lang.reflect.Member;

import page.foliage.inject.spi.Dependency;

import page.foliage.inject.Binder;
import page.foliage.inject.Binding;
import page.foliage.inject.Injector;
import page.foliage.inject.internal.util.StackTraceElements;

/**
 * A combination of a {@link Dependency} and the {@link Binding#getSource()
 * source} where the dependency was bound.
 * 
 * @author sameb@google.com (Sam Berlin)
 * @since 4.0
 */
public final class DependencyAndSource {
  private final Dependency<?> dependency;
  private final Object source;

  public DependencyAndSource(Dependency<?> dependency, Object source) {
    this.dependency = dependency;
    this.source = source;
  }

  /**
   * Returns the Dependency, if one exists. For anything that can be referenced
   * by {@link Injector#getBinding}, a dependency exists. A dependency will not
   * exist (and this will return null) for types initialized with
   * {@link Binder#requestInjection} or {@link Injector#injectMembers(Object)},
   * nor will it exist for objects injected into Providers bound with
   * LinkedBindingBuilder#toProvider(Provider).
   */
  public Dependency<?> getDependency() {
    return dependency;
  }

  /**
   * Returns a string describing where this dependency was bound. If the binding
   * was just-in-time, there is no valid binding source, so this describes the
   * class in question.
   */
  public String getBindingSource() {
    if (source instanceof Class) {
      return StackTraceElements.forType((Class) source).toString();
    } else if (source instanceof Member) {
      return StackTraceElements.forMember((Member) source).toString();
    } else {
      return source.toString();
    }
  }

  @Override
  public String toString() {
    Dependency<?> dep = getDependency();
    Object source = getBindingSource();
    if (dep != null) {
      return "Dependency: " + dep + ", source: " + source;
    } else {
      return "Source: " + source;
    }
  }
}