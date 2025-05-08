/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hyperledger.fabric.shim.ChaincodeException;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents a shift change request for soldiers in a secure facility.
public class ShiftChangeRequest implements AssetBase {
  public static final ShiftChangeRequest TypeQueryInstance =
      new ShiftChangeRequest("TypeQueryInstance", "TypeQueryInstance", null, null, null);

  String secureFacilityID; // ID of the secure facility
  String requestTimestamp; // Timestamp of the request

  String newSoldiersID; // ID of the new soldier
  String oldSoldiersID; // ID of the old soldier

  ShiftChangeRequestStatus status; // Status of the request

  public ShiftChangeRequest(
      String secureFacilityID,
      String requestTimestamp,
      String newSoldiersID,
      String oldSoldiersID,
      ShiftChangeRequestStatus status) {
    if (secureFacilityID == null || secureFacilityID.isEmpty()) {
      throw new ChaincodeException("Secure facility ID cannot be null or empty");
    }

    if (requestTimestamp == null || requestTimestamp.isEmpty()) {
      throw new ChaincodeException("Request timestamp cannot be null or empty");
    }

    this.secureFacilityID = secureFacilityID;
    this.requestTimestamp = requestTimestamp;
    this.newSoldiersID = newSoldiersID;
    this.oldSoldiersID = oldSoldiersID;
    this.status = status;
  }

  @Override
  public String getTypeForCompositeKey() {
    return ShiftChangeRequest.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {secureFacilityID, requestTimestamp};
  }

  // checks
  public boolean isPending() {
    return status == ShiftChangeRequestStatus.PENDING;
  }

  // assertions
  public void assertPending() {
    if (!isPending()) {
      throw new ChaincodeException(
          String.format(
              "Shift change request %s for facility %s is not in pending state",
              requestTimestamp, secureFacilityID));
    }
  }
}
