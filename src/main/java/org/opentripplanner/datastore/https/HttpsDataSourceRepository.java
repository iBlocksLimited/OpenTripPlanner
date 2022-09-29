package org.opentripplanner.datastore.https;

import java.net.URI;
import javax.annotation.Nonnull;
import org.opentripplanner.datastore.api.CompositeDataSource;
import org.opentripplanner.datastore.api.DataSource;
import org.opentripplanner.datastore.api.FileType;
import org.opentripplanner.datastore.base.DataSourceRepository;
import org.opentripplanner.datastore.base.ZipStreamDataSourceDecorator;

/**
 * This data store accesses files in read-only mode over HTTPS.
 */
public class HttpsDataSourceRepository implements DataSourceRepository {

  @Override
  public String description() {
    return "HTTPS";
  }

  @Override
  public void open() {}

  @Override
  public DataSource findSource(@Nonnull URI uri, @Nonnull FileType type) {
    if (skipUri(uri)) {
      return null;
    }
    return createSource(uri, type);
  }

  @Override
  public CompositeDataSource findCompositeSource(@Nonnull URI uri, @Nonnull FileType type) {
    if (skipUri(uri)) {
      return null;
    }
    return createCompositeSource(uri, type);
  }

  /* private methods */

  private static boolean skipUri(URI uri) {
    return !"https".equals(uri.getScheme());
  }

  private DataSource createSource(URI uri, FileType type) {
    return new HttpsFileDataSource(uri, type);
  }

  private CompositeDataSource createCompositeSource(URI uri, FileType type) {
    if (uri.getPath().endsWith(".zip")) {
      DataSource httpsSource = new HttpsFileDataSource(uri, type);
      return new ZipStreamDataSourceDecorator(httpsSource);
    } else {
      throw new UnsupportedOperationException(
        "Only ZIP archives are supported as composite sources for the HTTPS data source"
      );
    }
  }
}
