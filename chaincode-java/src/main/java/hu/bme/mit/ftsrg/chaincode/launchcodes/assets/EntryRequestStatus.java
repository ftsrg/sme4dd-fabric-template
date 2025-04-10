/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

/// This enum represents the possible statuses of an entry request.
public enum EntryRequestStatus {
  PENDING, // Request is pending approval
  APPROVED, // Request has been approved by two soldiers
  REJECTED, // Request has been rejected
  ENTERED // Requester has logged entry into the secure facility
}
