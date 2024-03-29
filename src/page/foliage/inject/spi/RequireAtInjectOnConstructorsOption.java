/*
 * Copyright (C) 2012 Google Inc.
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

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import page.foliage.inject.Binder;
import page.foliage.inject.Inject;

/**
 * A request to require explicit {@literal @}{@link Inject} annotations on constructors.
 *
 * @author sameb@google.com (Sam Berlin)
 * @since 4.0
 */
public final class RequireAtInjectOnConstructorsOption implements Element {
  private final Object source;

  RequireAtInjectOnConstructorsOption(Object source) {
    this.source = checkNotNull(source, "source");
  }

  @Override
  public Object getSource() {
    return source;
  }

  @Override
  public void applyTo(Binder binder) {
    binder.withSource(getSource()).requireAtInjectOnConstructors();
  }

  @Override
  public <T> T acceptVisitor(ElementVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
