package page.foliage.inject.internal;

import static page.foliage.guava.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.Formatter;
import java.util.List;

import page.foliage.guava.common.base.Preconditions;
import page.foliage.guava.common.collect.Lists;
import page.foliage.inject.spi.ErrorDetail;

/** Generic error message representing a Guice internal error. */
public final class GenericErrorDetail extends InternalErrorDetail<GenericErrorDetail>
    implements Serializable {
  public GenericErrorDetail(
      ErrorId errorId, String message, List<Object> sources, Throwable cause) {
    super(errorId, checkNotNull(message, "message"), sources, cause);
  }

  @Override
  public void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
    Preconditions.checkArgument(mergeableErrors.isEmpty(), "Unexpected mergeable errors");
    List<Object> dependencies = getSources();
    for (Object source : Lists.reverse(dependencies)) {
      formatter.format("  ");
      new SourceFormatter(source, formatter, /* omitPreposition= */ false).format();
    }
  }

  @Override
  public GenericErrorDetail withSources(List<Object> newSources) {
    return new GenericErrorDetail(errorId, getMessage(), newSources, getCause());
  }
}
