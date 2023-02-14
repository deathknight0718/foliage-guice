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

package page.foliage.inject.internal;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import java.lang.annotation.Annotation;

import page.foliage.inject.internal.AbstractProcessor;
import page.foliage.inject.internal.Annotations;
import page.foliage.inject.internal.Errors;

import page.foliage.inject.Scope;
import page.foliage.inject.spi.ScopeBinding;

/**
 * Handles {@code Binder.bindScope} commands.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
final class ScopeBindingProcessor extends AbstractProcessor {

  ScopeBindingProcessor(Errors errors) {
    super(errors);
  }

  @Override public Boolean visit(ScopeBinding command) {
    Scope scope = checkNotNull(command.getScope(), "scope");
    Class<? extends Annotation> annotationType = checkNotNull(command.getAnnotationType(), "annotation type");

    if (!Annotations.isScopeAnnotation(annotationType)) {
      errors.missingScopeAnnotation(annotationType);
      // Go ahead and bind anyway so we don't get collateral errors.
    }

    if (!Annotations.isRetainedAtRuntime(annotationType)) {
      errors.missingRuntimeRetention(annotationType);
      // Go ahead and bind anyway so we don't get collateral errors.
    }

    ScopeBinding existing = injector.state.getScopeBinding(annotationType);
    if (existing != null) {
      if (!scope.equals(existing.getScope())) {
        errors.duplicateScopes(existing, annotationType, scope);
      }
    } else {
      injector.state.putScopeBinding(annotationType, command);
    }

    return true;
  }
}