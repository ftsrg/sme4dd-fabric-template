/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.*;

public interface AssetBase {
  String getTypeForCompositeKey();

  String[] getAttributesForCompositeKey();

  default String toJsonString() {
    return serialize(this);
  }
}
