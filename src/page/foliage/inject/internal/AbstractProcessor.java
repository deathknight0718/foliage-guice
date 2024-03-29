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

import java.util.List;

import page.foliage.inject.spi.DefaultElementVisitor;
import page.foliage.inject.spi.Element;

/**
 * Abstract base class for creating an injector from module elements.
 *
 * <p>Extending classes must return {@code true} from any overridden {@code visit*()} methods, in
 * order for the element processor to remove the handled element.
 *
 * @author jessewilson@google.com (Jesse Wilson)
 */
abstract class AbstractProcessor extends DefaultElementVisitor<Boolean> {

  protected Errors errors;
  protected InjectorImpl injector;

  protected AbstractProcessor(Errors errors) {
    this.errors = errors;
  }

  public void process(Iterable<InjectorShell> isolatedInjectorBuilders) {
    for (InjectorShell injectorShell : isolatedInjectorBuilders) {
      process(injectorShell.getInjector(), injectorShell.getElements());
    }
  }

  public void process(InjectorImpl injector, List<Element> elements) {
    Errors errorsAnyElement = this.errors;
    this.injector = injector;
    try {
      elements.removeIf(
          e -> {
            this.errors = errorsAnyElement.withSource(e.getSource());
            return e.acceptVisitor(this);
          });
    } finally {
      this.errors = errorsAnyElement;
      this.injector = null;
    }
  }

  @Override
  protected Boolean visitOther(Element element) {
    return false;
  }
}
