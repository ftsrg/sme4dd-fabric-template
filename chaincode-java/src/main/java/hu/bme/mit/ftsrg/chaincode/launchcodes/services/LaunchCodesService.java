/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.services;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.SecureFacility;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.util.LaunchCodesRegistry;
import org.hyperledger.fabric.shim.ChaincodeException;

public class LaunchCodesService {

  private LaunchCodesRegistry registry;

  public LaunchCodesService(LaunchCodesRegistry registry) {
    this.registry = registry;
  }

  // services

  public void registerStaffCard(Card card) {
    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    card.assertStaffCard();
    registry.mustCreate(card);
    closeDoorEvent();
  }

  public void registerSoldierCard(Card card) {
    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    card.assertSoldierCard();
    registry.mustCreate(card);
    closeDoorEvent();
  }

  public void registerSecureFacility(SecureFacility facility, Card soldierOne, Card soldierTwo) {
    if (facility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (soldierOne == null) {
      throw new ChaincodeException("Soldier one is null");
    }

    if (soldierTwo == null) {
      throw new ChaincodeException("Soldier two is null");
    }

    soldierOne.assertDifferentCard(soldierTwo);
    soldierOne.assertSoldierCard();
    soldierOne.assertUnassigned();

    soldierTwo.assertSoldierCard();
    soldierTwo.assertUnassigned();

    facility.soldierOneID(soldierOne.cardID());
    facility.soldierTwoID(soldierTwo.cardID());
    soldierOne.secureFacilityID(facility.facilityID());
    soldierTwo.secureFacilityID(facility.facilityID());

    registry.mustCreate(facility);
    registry.mustUpdate(soldierOne);
    registry.mustUpdate(soldierTwo);

    closeDoorEvent(facility.facilityID());
  }

  public void requestEntry(SecureFacility facility, Card card) {
    if (facility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    card.assertUnassigned();
    facility.assertFree();
    facility.assertNoOngoingEntryRequest();

    var requestTimestamp = registry.getTransactionTimestamp();
    EntryRequest entryRequest =
        EntryRequest.builder()
            .secureFacilityID(facility.facilityID())
            .requestTimestamp(requestTimestamp)
            .requestBy(card.cardID())
            .status(EntryRequestStatus.PENDING)
            .build();

    facility.ongoingEntryRequestTimestamp(requestTimestamp);

    registry.mustCreate(entryRequest);
    registry.mustUpdate(facility);
    closeDoorEvent(facility.facilityID());
  }

  public void approveEntry(SecureFacility facility, Card card) {
    card.assertSoldierCard();
    assertSoldierAtFacility(card, facility);
    facility.assertOngoingEntryRequest();

    EntryRequest entryRequest =
        registry.mustRead(
            EntryRequest.builder()
                .secureFacilityID(facility.facilityID())
                .requestTimestamp(facility.ongoingEntryRequestTimestamp())
                .build(),
            EntryRequest.class);

    entryRequest.assertPending();
    assertEntryRequestNotApprovedBySoldier(entryRequest, card);
    entryRequest.assertNotApprovedByTwoSoldiers();
    entryRequest.addAuthorizingSoldier(card.cardID());

    if (entryRequest.isApprovedByTwoSoldiers()) {
      entryRequest.status(EntryRequestStatus.APPROVED);
      openDoorEvent(facility.facilityID());
    } else {
      closeDoorEvent(facility.facilityID());
    }

    registry.mustUpdate(entryRequest);
  }

  public void rejectEntry(SecureFacility facility, Card card) {
    if (facility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    assertSoldierAtFacility(card, facility);
    facility.assertOngoingEntryRequest();

    EntryRequest entryRequest =
        registry.mustRead(
            EntryRequest.builder()
                .secureFacilityID(facility.facilityID())
                .requestTimestamp(facility.ongoingEntryRequestTimestamp())
                .build(),
            EntryRequest.class);

    entryRequest.assertNotCompleted();

    entryRequest.status(EntryRequestStatus.REJECTED);
    facility.ongoingEntryRequestTimestamp(null);

    registry.mustUpdate(entryRequest);
    registry.mustUpdate(facility);
    closeDoorEvent(facility.facilityID());
  }

  public void logEntry(SecureFacility facility, Card card) {
    if (facility == null) {
      throw new ChaincodeException("Secure facility is null");
    }
    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    facility.assertOngoingEntryRequest();

    EntryRequest entryRequest =
        registry.mustRead(
            EntryRequest.builder()
                .secureFacilityID(facility.facilityID())
                .requestTimestamp(facility.ongoingEntryRequestTimestamp())
                .build(),
            EntryRequest.class);

    assertEntryRequestedByCard(entryRequest, card);
    entryRequest.assertApproved();

    entryRequest.status(EntryRequestStatus.ENTERED);
    facility.ongoingEntryRequestTimestamp(null);
    facility.visitorID(card.cardID());
    card.secureFacilityID(entryRequest.secureFacilityID());

    registry.mustUpdate(entryRequest);
    registry.mustUpdate(facility);
    registry.mustUpdate(card);

    closeDoorEvent(facility.facilityID());
  }

  public void requestExit(SecureFacility facility, Card card) {
    if (facility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    assertVisitorAtFacility(card, facility);
    facility.assertNoOngoingEntryRequest();

    var requestTimestamp = registry.getTransactionTimestamp();
    ExitRequest exitRequest =
        ExitRequest.builder()
            .secureFacilityID(facility.facilityID())
            .requestTimestamp(requestTimestamp)
            .requestBy(card.cardID())
            .status(ExitRequestStatus.PENDING)
            .build();

    facility.ongoingExitRequestTimestamp(requestTimestamp);

    registry.mustCreate(exitRequest);
    registry.mustUpdate(facility);
    closeDoorEvent(facility.facilityID());
  }

  public void approveExit(SecureFacility secureFacility, Card card) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    assertSoldierAtFacility(card, secureFacility);
    secureFacility.assertOngoingExitRequest();

    ExitRequest exitRequest =
        registry.mustRead(
            ExitRequest.builder()
                .secureFacilityID(secureFacility.facilityID())
                .requestTimestamp(secureFacility.ongoingExitRequestTimestamp())
                .build(),
            ExitRequest.class);

    exitRequest.assertPending();
    assertExitRequestNotApprovedBySoldier(exitRequest, card);
    exitRequest.assertNotApprovedByTwoSoldiers();

    exitRequest.addAuthorizingSoldier(card.cardID());

    if (exitRequest.isApprovedByTwoSoldiers()) {
      exitRequest.status(ExitRequestStatus.APPROVED);
      openDoorEvent(secureFacility.facilityID());
    } else {
      closeDoorEvent(secureFacility.facilityID());
    }

    registry.mustUpdate(exitRequest);
  }

  public void rejectExit(SecureFacility secureFacility, Card card) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    assertSoldierAtFacility(card, secureFacility);
    secureFacility.assertOngoingExitRequest();

    ExitRequest exitRequest =
        registry.mustRead(
            ExitRequest.builder()
                .secureFacilityID(secureFacility.facilityID())
                .requestTimestamp(secureFacility.ongoingExitRequestTimestamp())
                .build(),
            ExitRequest.class);

    exitRequest.assertNotCompleted();

    exitRequest.status(ExitRequestStatus.REJECTED);
    secureFacility.ongoingExitRequestTimestamp(null);

    registry.mustUpdate(exitRequest);
    registry.mustUpdate(secureFacility);
    closeDoorEvent(secureFacility.facilityID());
  }

  public void logExit(SecureFacility secureFacility, Card card) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (card == null) {
      throw new ChaincodeException("Card is null");
    }

    assertVisitorAtFacility(card, secureFacility);
    secureFacility.assertNoOngoingExitRequest();

    ExitRequest exitRequest =
        registry.mustRead(
            ExitRequest.builder()
                .secureFacilityID(secureFacility.facilityID())
                .requestTimestamp(secureFacility.ongoingExitRequestTimestamp())
                .build(),
            ExitRequest.class);

    assertExitRequestedByCard(exitRequest, card);
    exitRequest.assertApproved();

    exitRequest.status(ExitRequestStatus.EXITED);
    secureFacility.ongoingExitRequestTimestamp(null);
    secureFacility.visitorID(null);
    card.secureFacilityID(null);

    registry.mustUpdate(exitRequest);
    registry.mustUpdate(secureFacility);
    registry.mustUpdate(card);

    closeDoorEvent(secureFacility.facilityID());
  }

  public void initiateShiftChange(SecureFacility secureFacility, Card newSoldier, Card oldSoldier) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (newSoldier == null) {
      throw new ChaincodeException("New soldier is null");
    }

    if (oldSoldier == null) {
      throw new ChaincodeException("Old soldier is null");
    }

    newSoldier.assertSoldierCard();
    newSoldier.assertDifferentCard(oldSoldier);
    assertVisitorAtFacility(newSoldier, secureFacility);
    assertNotSoldierAtFacility(newSoldier, secureFacility);

    assertSoldierAtFacility(oldSoldier, secureFacility);
    secureFacility.assertNoOngoingShiftChangeRequest();

    String requestTimestampString = registry.getTransactionTimestamp();
    ShiftChangeRequest shiftChangeRequest =
        ShiftChangeRequest.builder()
            .secureFacilityID(secureFacility.facilityID())
            .requestTimestamp(requestTimestampString)
            .newSoldiersID(newSoldier.cardID())
            .oldSoldiersID(oldSoldier.cardID())
            .status(ShiftChangeRequestStatus.PENDING)
            .build();

    secureFacility.ongoingShiftChangeRequestTimestamp(requestTimestampString);

    registry.mustCreate(shiftChangeRequest);
    registry.mustUpdate(secureFacility);

    closeDoorEvent(secureFacility.facilityID());
  }

  public void approveShiftChange(SecureFacility secureFacility, Card oldSoldierCard) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (oldSoldierCard == null) {
      throw new ChaincodeException("Old soldier card is null");
    }

    oldSoldierCard.assertSoldierCard();
    oldSoldierCard.assertAssigned();
    assertSoldierAtFacility(oldSoldierCard, secureFacility);
    secureFacility.assertOngoingShiftChangeRequest();

    ShiftChangeRequest shiftChangeRequest =
        registry.mustRead(
            ShiftChangeRequest.builder()
                .secureFacilityID(secureFacility.facilityID())
                .requestTimestamp(secureFacility.ongoingShiftChangeRequestTimestamp())
                .build(),
            ShiftChangeRequest.class);

    shiftChangeRequest.assertPending();
    assertShiftChangeRequestTargetsCard(shiftChangeRequest, oldSoldierCard);

    shiftChangeRequest.status(ShiftChangeRequestStatus.APPROVED);
    secureFacility.ongoingShiftChangeRequestTimestamp(null);
    secureFacility.visitorID(oldSoldierCard.cardID());
    if (secureFacility.soldierOneID().equals(oldSoldierCard.cardID())) {
      secureFacility.soldierOneID(shiftChangeRequest.newSoldiersID());
    } else {
      secureFacility.soldierTwoID(shiftChangeRequest.newSoldiersID());
    }

    registry.mustUpdate(secureFacility);
    registry.mustUpdate(shiftChangeRequest);
    closeDoorEvent(secureFacility.facilityID());
  }

  public void rejectShiftChange(SecureFacility secureFacility, Card oldSoldierCard) {
    if (secureFacility == null) {
      throw new ChaincodeException("Secure facility is null");
    }

    if (oldSoldierCard == null) {
      throw new ChaincodeException("Old soldier card is null");
    }

    assertSoldierAtFacility(oldSoldierCard, secureFacility);
    secureFacility.assertOngoingShiftChangeRequest();

    ShiftChangeRequest shiftChangeRequest =
        registry.mustRead(
            ShiftChangeRequest.builder()
                .secureFacilityID(secureFacility.facilityID())
                .requestTimestamp(secureFacility.ongoingShiftChangeRequestTimestamp())
                .build(),
            ShiftChangeRequest.class);

    shiftChangeRequest.assertPending();
    assertShiftChangeRequestTargetsCard(shiftChangeRequest, oldSoldierCard);

    shiftChangeRequest.status(ShiftChangeRequestStatus.REJECTED);
    secureFacility.ongoingShiftChangeRequestTimestamp(null);

    registry.mustUpdate(shiftChangeRequest);
    registry.mustUpdate(secureFacility);

    closeDoorEvent(secureFacility.facilityID());
  }

  // events
  private void closeDoorEvent() {
    closeDoorEvent(null);
  }

  private void closeDoorEvent(String secureFacilityID) {
    registry.closeDoor(CloseDoorEvent.builder().secureFacilityID(secureFacilityID).build());
  }

  private void openDoorEvent(String secureFacilityID) {
    registry.openDoor(OpenDoorEvent.builder().secureFacilityID(secureFacilityID).build());
  }

  // checks
  private boolean isCardAssignedToFacility(Card card, SecureFacility facility) {
    return card != null
        && facility != null
        && card.secureFacilityID().equals(facility.facilityID());
  }

  private boolean isCardVisitorAtFacility(Card card, SecureFacility facility) {
    return isCardAssignedToFacility(card, facility) && facility.visitorID().equals(card.cardID());
  }

  private boolean isCardSoldierAtFacility(Card card, SecureFacility facility) {
    return card != null
        && facility != null
        && card.isSoldier()
        && isCardAssignedToFacility(card, facility)
        && (card.cardID().equals(facility.soldierOneID())
            || card.cardID().equals(facility.soldierTwoID()));
  }

  private boolean isEntryRequestedByCard(Card card, EntryRequest entryRequest) {
    return card != null && entryRequest != null && card.cardID().equals(entryRequest.requestBy());
  }

  private boolean isEntryRequestApprovedBySoldier(EntryRequest entryRequest, Card card) {
    return card != null
        && entryRequest != null
        && card.isSoldier()
        && (card.cardID().equals(entryRequest.authorizingSoldierOne())
            || card.cardID().equals(entryRequest.authorizingSoldierTwo()));
  }

  private boolean isExitRequestApprovedBySoldier(ExitRequest exitRequest, Card card) {
    return card != null
        && exitRequest != null
        && card.isSoldier()
        && (card.cardID().equals(exitRequest.authorizingSoldierOne())
            || card.cardID().equals(exitRequest.authorizingSoldierTwo()));
  }

  private boolean isExitRequestedByCard(Card card, ExitRequest exitRequest) {
    return card != null && exitRequest != null && card.cardID().equals(exitRequest.requestBy());
  }

  private boolean shiftChangeRequestTargetsCard(ShiftChangeRequest shiftChangeRequest, Card card) {
    return card != null
        && shiftChangeRequest != null
        && card.cardID().equals(shiftChangeRequest.oldSoldiersID());
  }

  // assertions
  private void assertShiftChangeRequestTargetsCard(
      ShiftChangeRequest shiftChangeRequest, Card card) {
    if (shiftChangeRequest == null) {
      throw new ChaincodeException(String.format("Shift change request is null"));
    }

    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (!shiftChangeRequestTargetsCard(shiftChangeRequest, card)) {
      throw new ChaincodeException(
          String.format(
              "Shift change request %s for facility %s does not target card %s",
              shiftChangeRequest.requestTimestamp(),
              shiftChangeRequest.secureFacilityID(),
              card.cardID()));
    }
  }

  private void assertExitRequestedByCard(ExitRequest exitRequest, Card card) {
    if (exitRequest == null) {
      throw new ChaincodeException(String.format("Exit request is null"));
    }

    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (!isExitRequestedByCard(card, exitRequest)) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is not requested by card %s",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID(), card.cardID()));
    }
  }

  private void assertExitRequestNotApprovedBySoldier(ExitRequest exitRequest, Card card) {
    if (exitRequest == null) {
      throw new ChaincodeException(String.format("Exit request is null"));
    }

    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (isExitRequestApprovedBySoldier(exitRequest, card)) {
      throw new ChaincodeException(
          String.format(
              "Exit request %s for facility %s is already approved by soldier %s",
              exitRequest.requestTimestamp(), exitRequest.secureFacilityID(), card.cardID()));
    }
  }

  private void assertEntryRequestNotApprovedBySoldier(EntryRequest entryRequest, Card card) {
    if (entryRequest == null) {
      throw new ChaincodeException(String.format("Entry request is null"));
    }

    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (isEntryRequestApprovedBySoldier(entryRequest, card)) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is already approved by soldier %s",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID(), card.cardID()));
    }
  }

  private void assertEntryRequestedByCard(EntryRequest entryRequest, Card card) {
    if (entryRequest == null) {
      throw new ChaincodeException(String.format("Entry request is null"));
    }

    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (!isEntryRequestedByCard(card, entryRequest)) {
      throw new ChaincodeException(
          String.format(
              "Entry request %s for facility %s is not requested by card %s",
              entryRequest.requestTimestamp(), entryRequest.secureFacilityID(), card.cardID()));
    }
  }

  private void assertVisitorAtFacility(Card card, SecureFacility facility) {
    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (facility == null) {
      throw new ChaincodeException(String.format("Secure facility is null"));
    }

    if (!isCardVisitorAtFacility(card, facility)) {
      throw new ChaincodeException(
          String.format(
              "Card %s is not a visitor at secure facility %s",
              card.cardID(), facility.facilityID()));
    }
  }

  private void assertSoldierAtFacility(Card card, SecureFacility facility) {
    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (facility == null) {
      throw new ChaincodeException(String.format("Secure facility is null"));
    }

    if (!isCardSoldierAtFacility(card, facility)) {
      throw new ChaincodeException(
          String.format(
              "Card %s is not a soldier of secure facility %s",
              card.cardID(), facility.facilityID()));
    }
  }

  private void assertNotSoldierAtFacility(Card card, SecureFacility facility) {
    if (card == null) {
      throw new ChaincodeException(String.format("Card is null"));
    }

    if (facility == null) {
      throw new ChaincodeException(String.format("Secure facility is null"));
    }

    if (isCardSoldierAtFacility(card, facility)) {
      throw new ChaincodeException(
          String.format(
              "Card %s is a soldier of secure facility %s", card.cardID(), facility.facilityID()));
    }
  }
}
