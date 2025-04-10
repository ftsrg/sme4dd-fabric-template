/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.CardType;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.SecureFacility;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequestStatus;
import org.hyperledger.fabric.shim.ChaincodeException;

public final class RelationAsserts {

  public static void cardBelongsToSoldier(Card card) {
    if (card.cardType() != CardType.SOLDIER) {
      throw new ChaincodeException(
          String.format("Card %s does not belong to a soldier", card.cardID()));
    }
  }

  public static void cardsAreDifferent(Card card1, Card card2) {
    if (card1.cardID().equals(card2.cardID())) {
      throw new ChaincodeException(
          String.format("Cards %s and %s must be different", card1.cardID(), card2.cardID()));
    }
  }

  public static void cardIsUnassigned(Card card) {
    if (card.secureFacilityID() != null) {
      throw new ChaincodeException(
          String.format("Card %s is already assigned to a secure facility", card.cardID()));
    }
  }

  public static void cardIsAssigned(Card card) {
    if (card.secureFacilityID() != null) {
      throw new ChaincodeException(
          String.format("Card %s is already assigned to a secure facility", card.cardID()));
    }
  }

  public static void cardIsAssignedToFacility(Card card, SecureFacility facility) {
    if (!facility.facilityID().equals(card.secureFacilityID())) {
      throw new ChaincodeException(
          String.format(
              "Card %s is not assigned to secure facility %s",
              card.cardID(), facility.facilityID()));
    }
  }

  public static void cardIsVisitorAtFacility(Card card, SecureFacility facility) {
    if (!facility.visitorID().equals(card.cardID())) {
      throw new ChaincodeException(
          String.format(
              "Card %s is not a visitor at secure facility %s",
              card.cardID(), facility.facilityID()));
    }
  }

  public static void cardIsSoldierOfFacility(Card card, SecureFacility facility) {
    if (!card.cardID().equals(facility.soldierOneID())
        && !card.cardID().equals(facility.soldierTwoID())) {
      throw new ChaincodeException(
          String.format(
              "Card %s is not a soldier of secure facility %s",
              card.cardID(), facility.facilityID()));
    }
  }

  public static void cardNotSoldierOfFacility(Card card, SecureFacility facility) {
    if (card.cardID().equals(facility.soldierOneID())
        || card.cardID().equals(facility.soldierTwoID())) {
      throw new ChaincodeException(
          String.format(
              "Card %s is a soldier of secure facility %s", card.cardID(), facility.facilityID()));
    }
  }

  public static void facilityIsFree(SecureFacility facility) {
    if (facility.visitorID() != null) {
      throw new ChaincodeException(
          String.format(
              "Secure facility %s is already occupied by visitor", facility.facilityID()));
    }
  }

  public static void facilityNoOngoingEntryRequest(SecureFacility facility) {
    if (facility.ongoingEntryRequestTimestamp() != null) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing entry request", facility.facilityID()));
    }
  }

  public static void facilityNoOngoingShiftChange(SecureFacility facility) {
    if (facility.ongoingShiftRequestTimestamp() != null) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing shift change", facility.facilityID()));
    }
  }

  public static void facilityHasOngoingShiftChange(SecureFacility facility) {
    if (facility.ongoingShiftRequestTimestamp() == null) {
      throw new ChaincodeException(
          String.format(
              "Secure facility %s does not have an ongoing shift change", facility.facilityID()));
    }
  }

  public static void facilityHasOngoingEntryRequest(SecureFacility facility) {
    if (facility.ongoingEntryRequestTimestamp() == null) {
      throw new ChaincodeException(
          String.format(
              "Secure facility %s does not have an ongoing entry request", facility.facilityID()));
    }
  }

  public static void facilityNoOngoingExitRequest(SecureFacility facility) {
    if (facility.ongoingExitRequestTimestamp() != null) {
      throw new ChaincodeException(
          String.format("Secure facility %s has an ongoing exit request", facility.facilityID()));
    }
  }

  public static void facilityHasOngoingExitRequest(SecureFacility facility) {
    if (facility.ongoingExitRequestTimestamp() == null) {
      throw new ChaincodeException(
          String.format(
              "Secure facility %s does not have an ongoing exit request", facility.facilityID()));
    }
  }

  public static void entryRequestIsPending(EntryRequest entryRequest) {
    if (entryRequest.status() != EntryRequestStatus.PENDING) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not in pending state",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID()));
    }
  }

  public static void exitRequestIsPending(ExitRequest exitRequest) {
    if (exitRequest.status() != ExitRequestStatus.PENDING) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is not in pending state",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID()));
    }
  }

  public static void entryRequestedByCard(EntryRequest entryRequest, Card card) {
    if (!card.cardID().equals(entryRequest.requestBy())) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not requested by card %s",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID(), card.cardID()));
    }
  }

  public static void entryRequestNotCompleted(EntryRequest entryRequest) {
    if (entryRequest.status() == EntryRequestStatus.ENTERED) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already completed",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID()));
    }
  }

  public static void entryRequestIsApproved(EntryRequest entryRequest) {
    if (entryRequest.status() != EntryRequestStatus.APPROVED) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not approved",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID()));
    }
  }

  public static void entryRequestIsNotApprovedBySoldier(EntryRequest entryRequest, Card card) {
    if (card.cardID().equals(entryRequest.authorizingSoldierOne())
        || card.cardID().equals(entryRequest.authorizingSoldierTwo())) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already approved by soldier %s",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID(), card.cardID()));
    }
  }

  public static void entryRequestIsNotApprovedByTwoSoldiers(EntryRequest entryRequest) {
    if (entryRequest.authorizingSoldierOne() != null
        && entryRequest.authorizingSoldierTwo() != null) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already approved by two soldiers",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID()));
    }
  }

  public static void exitRequestIsNotApprovedBySoldier(ExitRequest exitRequest, Card card) {
    if (card.cardID().equals(exitRequest.authorizingSoldierOne())
        || card.cardID().equals(exitRequest.authorizingSoldierTwo())) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is already approved by soldier %s",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID(), card.cardID()));
    }
  }

  public static void exitRequestIsNotApprovedByTwoSoldiers(ExitRequest exitRequest) {
    if (exitRequest.authorizingSoldierOne() != null
        && exitRequest.authorizingSoldierTwo() != null) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is already approved by two soldiers",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID()));
    }
  }

  public static void exitRequestNotCompleted(ExitRequest exitRequest) {
    if (exitRequest.status() == ExitRequestStatus.EXITED) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is already completed",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID()));
    }
  }

  public static void exitRequestIsApproved(ExitRequest exitRequest) {
    if (exitRequest.status() != ExitRequestStatus.APPROVED) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is not approved",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID()));
    }
  }

  public static void exitRequestedByCard(ExitRequest exitRequest, Card card) {
    if (!card.cardID().equals(exitRequest.requestBy())) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is not requested by card %s",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID(), card.cardID()));
    }
  }

  public static void shiftChangeRequestIsPending(ShiftChangeRequest shiftChangeRequest) {
    if (shiftChangeRequest.status() != ShiftChangeRequestStatus.PENDING) {
      throw new ChaincodeException(
          String.format(
              "Shift change request %s for facility %s is not in pending state",
              shiftChangeRequest.requestTimestamp(), shiftChangeRequest.secureFacilityID()));
    }
  }

  public static void shiftChangeRequestedTargetsCard(
      ShiftChangeRequest shiftChangeRequest, Card card) {
    if (!card.cardID().equals(shiftChangeRequest.oldSoldiersID())) {
      throw new ChaincodeException(
          String.format(
              "Shift change request %s for facility %s does not target card %s",
              shiftChangeRequest.requestTimestamp(),
              shiftChangeRequest.secureFacilityID(),
              card.cardID()));
    }
  }
}
