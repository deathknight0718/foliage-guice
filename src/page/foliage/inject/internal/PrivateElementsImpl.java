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

import static page.foliage.guava.common.base.Preconditions.checkArgument;
import static page.foliage.guava.common.base.Preconditions.checkNotNull;
import static page.foliage.guava.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Set;

import page.foliage.guava.common.base.MoreObjects;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Lists;
import page.foliage.guava.common.collect.Maps;
import page.foliage.inject.Binder;
import page.foliage.inject.Injector;
import page.foliage.inject.Key;
import page.foliage.inject.PrivateBinder;
import page.foliage.inject.spi.Element;
import page.foliage.inject.spi.ElementVisitor;
import page.foliage.inject.spi.PrivateElements;

/** @author jessewilson@google.com (Jesse Wilson) */
public final class PrivateElementsImpl implements PrivateElements {

  /*
   * This class acts as both a value object and as a builder. When getElements() is called, an
   * immutable collection of elements is constructed and the original mutable list is nulled out.
   * Similarly, the exposed keys are made immutable on access.
   */

  private final Object source;

  private List<Element> elementsMutable = Lists.newArrayList();
  private List<ExposureBuilder<?>> exposureBuilders = Lists.newArrayList();

  /** lazily instantiated */
  private ImmutableList<Element> elements;

  /** lazily instantiated */
  private ImmutableMap<Key<?>, Object> exposedKeysToSources;

  private Injector injector;

  public PrivateElementsImpl(Object source) {
    this.source = checkNotNull(source, "source");
  }

  @Override
  public Object getSource() {
    return source;
  }

  @Override
  public List<Element> getElements() {
    if (elements == null) {
      elements = ImmutableList.copyOf(elementsMutable);
      elementsMutable = null;
    }

    return elements;
  }

  @Override
  public Injector getInjector() {
    return injector;
  }

  public void initInjector(Injector injector) {
    checkState(this.injector == null, "injector already initialized");
    this.injector = checkNotNull(injector, "injector");
  }

  @Override
  public Set<Key<?>> getExposedKeys() {
    if (exposedKeysToSources == null) {
      Map<Key<?>, Object> exposedKeysToSourcesMutable = Maps.newLinkedHashMap();
      for (ExposureBuilder<?> exposureBuilder : exposureBuilders) {
        exposedKeysToSourcesMutable.put(exposureBuilder.getKey(), exposureBuilder.getSource());
      }
      exposedKeysToSources = ImmutableMap.copyOf(exposedKeysToSourcesMutable);
      exposureBuilders = null;
    }

    return exposedKeysToSources.keySet();
  }

  @Override
  public <T> T acceptVisitor(ElementVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public List<Element> getElementsMutable() {
    return elementsMutable;
  }

  public void addExposureBuilder(ExposureBuilder<?> exposureBuilder) {
    exposureBuilders.add(exposureBuilder);
  }

  @Override
  public void applyTo(Binder binder) {
    PrivateBinder privateBinder = binder.withSource(source).newPrivateBinder();

    for (Element element : getElements()) {
      element.applyTo(privateBinder);
    }

    getExposedKeys(); // ensure exposedKeysToSources is populated
    for (Map.Entry<Key<?>, Object> entry : exposedKeysToSources.entrySet()) {
      privateBinder.withSource(entry.getValue()).expose(entry.getKey());
    }
  }

  @Override
  public Object getExposedSource(Key<?> key) {
    getExposedKeys(); // ensure exposedKeysToSources is populated
    Object source = exposedKeysToSources.get(key);
    checkArgument(source != null, "%s not exposed by %s.", key, this);
    return source;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(PrivateElements.class)
        .add("exposedKeys", getExposedKeys())
        .add("source", getSource())
        .toString();
  }
}
