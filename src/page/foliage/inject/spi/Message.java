/**
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

package page.foliage.inject.spi;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import page.foliage.guava.common.base.Objects;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.inject.Binder;
import page.foliage.inject.internal.Errors;
import page.foliage.inject.internal.util.SourceProvider;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.List;

import page.foliage.inject.spi.Element;
import page.foliage.inject.spi.ElementVisitor;
import page.foliage.inject.spi.Message;

/**
 * An error message and the context in which it occured. Messages are usually created internally by
 * Guice and its extensions. Messages can be created explicitly in a module using {@link
 * page.foliage.inject.Binder#addError(Throwable) addError()} statements:
 * <pre>
 *     try {
 *       bindPropertiesFromFile();
 *     } catch (IOException e) {
 *       addError(e);
 *     }</pre>
 *
 * @author crazybob@google.com (Bob Lee)
 */
public final class Message implements Serializable, Element {
  private final String message;
  private final Throwable cause;
  private final List<Object> sources;

  /**
   * @since 2.0
   */
  public Message(List<Object> sources, String message, Throwable cause) {
    this.sources = ImmutableList.copyOf(sources);
    this.message = checkNotNull(message, "message");
    this.cause = cause;
  }

  /**
   * @since 4.0
   */
  public Message(String message, Throwable cause) {
    this(ImmutableList.of(), message, cause);
  }

  public Message(Object source, String message) {
    this(ImmutableList.of(source), message, null);
  }

  public Message(String message) {
    this(ImmutableList.of(), message, null);
  }

  public String getSource() {
    return sources.isEmpty()
        ? SourceProvider.UNKNOWN_SOURCE.toString()
        : Errors.convert(sources.get(sources.size() - 1)).toString();
  }

  /** @since 2.0 */
  public List<Object> getSources() {
    return sources;
  }

  /**
   * Gets the error message text.
   */
  public String getMessage() {
    return message;
  }

  /** @since 2.0 */
  public <T> T acceptVisitor(ElementVisitor<T> visitor) {
    return visitor.visit(this);
  }

  /**
   * Returns the throwable that caused this message, or {@code null} if this
   * message was not caused by a throwable.
   *
   * @since 2.0
   */
  public Throwable getCause() {
    return cause;
  }

  @Override public String toString() {
    return message;
  }

  @Override public int hashCode() {
    return Objects.hashCode(message, cause, sources);
  }

  @Override public boolean equals(Object o) {
    if (!(o instanceof Message)) {
      return false;
    }
    Message e = (Message) o;
    return message.equals(e.message) && Objects.equal(cause, e.cause) && sources.equals(e.sources);
  }

  /** @since 2.0 */
  public void applyTo(Binder binder) {
    binder.withSource(getSource()).addError(this);
  }

  /**
   * When serialized, we eagerly convert sources to strings. This hurts our formatting, but it
   * guarantees that the receiving end will be able to read the message.
   */
  private Object writeReplace() throws ObjectStreamException {
    Object[] sourcesAsStrings = sources.toArray();
    for (int i = 0; i < sourcesAsStrings.length; i++) {
      sourcesAsStrings[i] = Errors.convert(sourcesAsStrings[i]).toString();
    }
    return new Message(ImmutableList.copyOf(sourcesAsStrings), message, cause);
  }

  private static final long serialVersionUID = 0;
}
