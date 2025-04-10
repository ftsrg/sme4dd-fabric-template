/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents a shift change request for soldiers in a secure facility.
public class ShiftChangeRequest implements AssetBase {
  String secureFacilityID; // ID of the secure facility
  String requestTimestamp; // Timestamp of the request

  String newSoldiersID; // ID of the new soldier
  String oldSoldiersID; // ID of the old soldier

  ShiftChangeRequestStatus status; // Status of the request

  @Override
  public String getTypeForCompositeKey() {
    return ShiftChangeRequest.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {secureFacilityID, requestTimestamp};
  }
}
