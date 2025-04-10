/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

/// This enum represents the possible statuses of an exit request.
public enum ExitRequestStatus {
  PENDING, // Request is pending approval
  APPROVED, // Request has been approved by two soldiers
  REJECTED, // Request has been rejected
  EXITED // Requester has logged exit from the secure facility
}
