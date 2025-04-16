/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hyperledger.fabric.shim.ChaincodeException;

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
  String facilityID; // ID of the facility
  String facilityName; // Name of the facility

  String soldierOneID; // ID of the first soldier on duty
  String soldierTwoID; // ID of the second soldier on duty
  String visitorID; // ID of the current visitor (staff or soldier), if any, otherwise null

  // Timestamp of the ongoing entry request, if any, otherwise null
  String ongoingEntryRequestTimestamp;
  // Timestamp of the ongoing exit request, if any, otherwise null
  String ongoingExitRequestTimestamp;
  // Timestamp of the ongoing shift request, if any, otherwise null
  String ongoingShiftChangeRequestTimestamp;

  @Override
  public String getTypeForCompositeKey() {
    return SecureFacility.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {facilityID};
  }

  // checks
  public boolean isFree() {
    return visitorID == null;
  }

  public boolean hasOngoingEntryRequest() {
    return ongoingEntryRequestTimestamp != null;
  }

  public boolean hasOngoingExitRequest() {
    return ongoingExitRequestTimestamp != null;
  }

  public boolean hasOngoingShiftChangeRequest() {
    return ongoingShiftChangeRequestTimestamp != null;
  }

  // assertions
  public void assertFree() {
    if (!isFree()) {
      throw new ChaincodeException(
          String.format("Secure facility %s is already occupied by visitor", facilityID));
    }
  }

  public void assertNoOngoingEntryRequest() {
    if (hasOngoingEntryRequest()) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing entry request", facilityID));
    }
  }

  public void assertOngoingEntryRequest() {
    if (!hasOngoingEntryRequest()) {
      throw new ChaincodeException(
          String.format("Secure facility %s does not have an ongoing entry request", facilityID));
    }
  }

  public void assertNoOngoingShiftChangeRequest() {
    if (hasOngoingShiftChangeRequest()) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing shift change request", facilityID));
    }
  }

  public void assertOngoingShiftChangeRequest() {
    if (!hasOngoingShiftChangeRequest()) {
      throw new ChaincodeException(
          String.format(
              "Secure facility %s does not have an ongoing shift change request", facilityID));
    }
  }

  public void assertNoOngoingExitRequest() {
    if (hasOngoingExitRequest()) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing exit request", facilityID));
    }
  }

  public void assertOngoingExitRequest() {
    if (!hasOngoingExitRequest()) {
      throw new ChaincodeException(
          String.format("Secure facility %s does not have an ongoing exit request", facilityID));
    }
  }
}
