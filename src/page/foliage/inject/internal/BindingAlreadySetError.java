package page.foliage.inject.internal;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

import page.foliage.guava.common.collect.ImmutableList;
import page.foliage.inject.Binding;
import page.foliage.inject.spi.ErrorDetail;

/** Error reported by Guice when a key is bound at multiple places the injector. */
final class BindingAlreadySetError extends InternalErrorDetail<BindingAlreadySetError> {
  private final Binding<?> binding;
  private final Binding<?> original;

  BindingAlreadySetError(Binding<?> binding, Binding<?> original, List<Object> sources) {
    super(
        ErrorId.BINDING_ALREADY_SET,
        String.format("%s was bound multiple times.", Messages.convert(binding.getKey())),
        sources,
        null);
    this.binding = binding;
    this.original = original;
  }

  @Override
  public boolean isMergeable(ErrorDetail<?> otherError) {
    return otherError instanceof BindingAlreadySetError
        && ((BindingAlreadySetError) otherError).binding.getKey().equals(binding.getKey());
  }

  @Override
  public void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
    List<List<Object>> sourcesList = new ArrayList<>();
    sourcesList.add(ImmutableList.of(original.getSource()));
    sourcesList.add(ImmutableList.of(binding.getSource()));
    sourcesList.addAll(
        mergeableErrors.stream()
            .map(e -> ((BindingAlreadySetError) e).binding.getSource())
            .map(ImmutableList::of)
            .collect(Collectors.toList()));
    formatter.format("%n%s%n", Messages.bold("Bound at:"));
    for (int i = 0; i < sourcesList.size(); i++) {
      ErrorFormatter.formatSources(i + 1, sourcesList.get(i), formatter);
    }
  }

  @Override
  public BindingAlreadySetError withSources(List<Object> newSources) {
    return new BindingAlreadySetError(binding, original, newSources);
  }
}
