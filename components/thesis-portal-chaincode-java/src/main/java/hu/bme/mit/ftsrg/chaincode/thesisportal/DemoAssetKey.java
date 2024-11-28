/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class DemoAssetKey {
  private String ID;

  public DemoAssetKey(String ID) {
    this.ID = ID;
  }

  public DemoAssetKey(DemoAsset asset) {
    this.ID = asset.ID();
  }
  
  String getType() {
    return "DemoAsset";
  }

  String[] getKeyParts() {
    return new String[] {ID};
  }

  public String toString() {
    return String.join("_", getType(),String.join("_", getKeyParts()));
  }

  static DemoAssetKey from(DemoAsset asset) {
    return new DemoAssetKey(asset);
  }

  static DemoAssetKey from(String assetId) {
    return new DemoAssetKey(assetId);
  }
}
