/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/// This enum represents the possible statuses of a shift change request.
enum ShiftChangeRequestStatus {
  PENDING, // Request is pending approval
  APPROVED, // Request has been approved by the old soldier
  REJECTED // Request has been rejected
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents a shift change request for soldiers in a secure facility.
public class ShiftChangeRequest {
  String secureFacilityID; // ID of the secure facility
  String requestTimestamp; // Timestamp of the request

  String newSoldiersID; // ID of the new soldier
  String oldSoldiersID; // ID of the old soldier

  ShiftChangeRequestStatus status; // Status of the request
}
