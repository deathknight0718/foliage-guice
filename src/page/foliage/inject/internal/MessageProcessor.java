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

import java.util.logging.Level;
import java.util.logging.Logger;

import page.foliage.inject.internal.AbstractProcessor;
import page.foliage.inject.internal.Errors;

import page.foliage.inject.Guice;
import page.foliage.inject.spi.Message;

/**
 * Handles {@code Binder.addError} commands.
 *
 * @author crazybob@google.com (Bob Lee)
 * @author jessewilson@google.com (Jesse Wilson)
 */
final class MessageProcessor extends AbstractProcessor {

  private static final Logger logger = Logger.getLogger(Guice.class.getName());

  MessageProcessor(Errors errors) {
    super(errors);
  }

  @Override public Boolean visit(Message message) {
    if (message.getCause() != null) {
      String rootMessage = getRootMessage(message.getCause());
      logger.log(Level.INFO,
          "An exception was caught and reported. Message: " + rootMessage,
          message.getCause());
    }

    errors.addMessage(message);
    return true;
  }

  public static String getRootMessage(Throwable t) {
    Throwable cause = t.getCause();
    return cause == null ? t.toString() : getRootMessage(cause);
  }
}
