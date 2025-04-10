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
/// This class represents a secure facility.
/// It contains the ID of the lock, the name of the facility,
/// the IDs of the soldiers and visitors, and the IDs of the ongoing entry and exit requests.
public class SecureFacility implements AssetBase {
  String lockID; // ID of the lock
  String facilityName; // Name of the facility

  String soldierOneID; // ID of the first soldier on duty
  String soldierTwoID; // ID of the second soldier on duty
  String visitorID; // ID of the current visitor (staff or soldier), if any, otherwise null

  String ongoingEntryRequestID; // ID of the ongoing entry request, if any, otherwise null
  String ongoingExitRequestID; // ID of the ongoing exit request, if any, otherwise null

  @Override
  public String getTypeForCompositeKey() {
    return SecureFacility.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {lockID};
  }
}
