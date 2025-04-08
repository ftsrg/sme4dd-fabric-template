/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

/// This enum represents the possible statuses of an entry request.
enum EntryRequestStatus {
  PENDING, // Request is pending approval
  APPROVED, // Request has been approved by two soldiers
  REJECTED, // Request has been rejected
  ENTERED // Requester has logged entry into the secure facility
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents an entry request for a secure facility.
/// It contains the ID of the secure facility, the timestamp of the request,
/// the ID of the staff/soldier making the request, and the status of the request.
public class EntryRequest {
  String secureFacilityID; // ID of the secure facility
  String requestTimestamp; // Timestamp of the request
  String requestBy; // ID of the staff/soldier making the request

  String authorizingSoldierOne; // ID of the first authorizing soldier
  String authorizingSoldierTwo; // ID of the second authorizing soldier

  EntryRequestStatus status; // Status of the request
}
