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
/// This class represents an entry request for a secure facility.
/// It contains the ID of the secure facility, the timestamp of the request,
/// the ID of the staff/soldier making the request, and the status of the request.
public class EntryRequest implements AssetBase {
  String secureFacilityID; // ID of the secure facility
  String requestTimestamp; // Timestamp of the request
  String requestBy; // ID of the staff/soldier making the request

  String authorizingSoldierOne; // ID of the first authorizing soldier
  String authorizingSoldierTwo; // ID of the second authorizing soldier

  EntryRequestStatus status; // Status of the request

  public EntryRequest(
      String secureFacilityID,
      String requestTimestamp,
      String requestBy,
      String authorizingSoldierOne,
      String authorizingSoldierTwo,
      EntryRequestStatus status) {
    if (secureFacilityID == null || secureFacilityID.isEmpty()) {
      throw new ChaincodeException("Secure facility ID cannot be null or empty");
    }
    if (requestTimestamp == null || requestTimestamp.isEmpty()) {
      throw new ChaincodeException("Request timestamp cannot be null or empty");
    }

    this.secureFacilityID = secureFacilityID;
    this.requestTimestamp = requestTimestamp;
    this.requestBy = requestBy;
    this.authorizingSoldierOne = authorizingSoldierOne;
    this.authorizingSoldierTwo = authorizingSoldierTwo;
    this.status = status;
  }

  @Override
  public String getTypeForCompositeKey() {
    return EntryRequest.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {secureFacilityID, requestTimestamp};
  }

  public void addAuthorizingSoldier(String soldierID) {
    if (authorizingSoldierOne == null) {
      authorizingSoldierOne = soldierID;
    } else if (authorizingSoldierTwo == null) {
      authorizingSoldierTwo = soldierID;
    } else {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s already has two authorizing soldiers",
              requestTimestamp, secureFacilityID));
    }
  }

  // checks
  public boolean isPending() {
    return status == EntryRequestStatus.PENDING;
  }

  public boolean isCompleted() {
    return status == EntryRequestStatus.ENTERED;
  }

  public boolean isApproved() {
    return status == EntryRequestStatus.APPROVED;
  }

  public boolean isApprovedByTwoSoldiers() {
    return authorizingSoldierOne != null && authorizingSoldierTwo != null;
  }

  // assertions
  public void assertPending() {
    if (!isPending()) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not in pending state",
              requestTimestamp, secureFacilityID));
    }
  }

  public void assertNotCompleted() {
    if (isCompleted()) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already completed",
              requestTimestamp, secureFacilityID));
    }
  }

  public void assertApproved() {
    if (!isApproved()) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not approved",
              requestTimestamp, secureFacilityID));
    }
  }

  public void assertNotApprovedByTwoSoldiers() {
    if (isApprovedByTwoSoldiers()) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already approved by two soldiers",
              requestTimestamp, secureFacilityID));
    }
  }
}
