/*
 * Copyright (C) 2011 Google Inc.
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
import java.util.logging.Logger;

import page.foliage.guava.common.cache.CacheBuilder;
import page.foliage.guava.common.cache.CacheLoader;
import page.foliage.guava.common.cache.LoadingCache;
import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.guava.common.collect.ImmutableSet;
import page.foliage.guava.common.collect.Lists;
import page.foliage.inject.Binding;
import page.foliage.inject.Injector;
import page.foliage.inject.Key;
import page.foliage.inject.Stage;
import page.foliage.inject.spi.ProvisionListener;
import page.foliage.inject.spi.ProvisionListenerBinding;

/**
 * {@link ProvisionListenerStackCallback} for each key.
 *
 * @author sameb@google.com (Sam Berlin)
 */
final class ProvisionListenerCallbackStore {

  // TODO(sameb): Consider exposing this in the API somehow?  Maybe?
  // Lots of code often want to skip over the internal stuffs.
  private static final ImmutableSet<Key<?>> INTERNAL_BINDINGS =
      ImmutableSet.of(Key.get(Injector.class), Key.get(Stage.class), Key.get(Logger.class));

  private final ImmutableList<ProvisionListenerBinding> listenerBindings;

  private final LoadingCache<KeyBinding, ProvisionListenerStackCallback<?>> cache =
      CacheBuilder.newBuilder()
          .build(
              new CacheLoader<KeyBinding, ProvisionListenerStackCallback<?>>() {
                @Override
                public ProvisionListenerStackCallback<?> load(KeyBinding key) {
                  return create(key.binding);
                }
              });

  ProvisionListenerCallbackStore(List<ProvisionListenerBinding> listenerBindings) {
    this.listenerBindings = ImmutableList.copyOf(listenerBindings);
  }

  /**
   * Returns a new {@link ProvisionListenerStackCallback} for the key or {@code null} if there are
   * no listeners
   */
  @SuppressWarnings(
      "unchecked") // the ProvisionListenerStackCallback type always agrees with the passed type
  public <T> ProvisionListenerStackCallback<T> get(Binding<T> binding) {
    // Never notify any listeners for internal bindings.
    if (!INTERNAL_BINDINGS.contains(binding.getKey())) {
      ProvisionListenerStackCallback<T> callback =
          (ProvisionListenerStackCallback<T>)
              cache.getUnchecked(new KeyBinding(binding.getKey(), binding));
      return callback.hasListeners() ? callback : null;
    }
    return null;
  }

  /**
   * Purges a key from the cache. Use this only if the type is not actually valid for binding and
   * needs to be purged. (See issue 319 and
   * ImplicitBindingTest#testCircularJitBindingsLeaveNoResidue and
   * #testInstancesRequestingProvidersForThemselvesWithChildInjectors for examples of when this is
   * necessary.)
   *
   * <p>Returns true if the type was stored in the cache, false otherwise.
   */
  boolean remove(Binding<?> type) {
    return cache.asMap().remove(type) != null;
  }

  /**
   * Creates a new {@link ProvisionListenerStackCallback} with the correct listeners for the key.
   */
  private <T> ProvisionListenerStackCallback<T> create(Binding<T> binding) {
    List<ProvisionListener> listeners = null;
    for (ProvisionListenerBinding provisionBinding : listenerBindings) {
      if (provisionBinding.getBindingMatcher().matches(binding)) {
        if (listeners == null) {
          listeners = Lists.newArrayList();
        }
        listeners.addAll(provisionBinding.getListeners());
      }
    }
    if (listeners == null || listeners.isEmpty()) {
      // Optimization: don't bother constructing the callback if there are
      // no listeners.
      return ProvisionListenerStackCallback.emptyListener();
    }
    return new ProvisionListenerStackCallback<T>(binding, listeners);
  }

  /** A struct that holds key & binding but uses just key for equality/hashcode. */
  private static class KeyBinding {
    final Key<?> key;
    final Binding<?> binding;

    KeyBinding(Key<?> key, Binding<?> binding) {
      this.key = key;
      this.binding = binding;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof KeyBinding && key.equals(((KeyBinding) obj).key);
    }

    @Override
    public int hashCode() {
      return key.hashCode();
    }
  }
}
