/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Serializer {

  private final Genson genson =
      new GensonBuilder()
          .useIndentation(false)
          .useFields(true, VisibilityFilter.PRIVATE)
          .useMethods(false)
          .create();

  public String serialize(Object obj) {
    return genson.serialize(obj);
  }

  public <T> T deserialize(String data, Class<T> clazz) {
    return genson.deserialize(data, clazz);
  }
}
