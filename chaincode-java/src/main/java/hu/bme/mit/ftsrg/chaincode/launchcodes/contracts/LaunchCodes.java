/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.contracts;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.CardType;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.SecureFacility;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequestStatus;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.util.LaunchCodeContext;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Contract(
    name = "hw-launch-codes",
    info =
        @Info(
            title = "Launch Codes",
            description = "Chaincode for the BTA homework L(a)unchCodes",
            version = "0.1.0",
            license = @License(name = "Apache-2.0"),
            contact =
                @Contact(
                    email = "john.doe@example.com",
                    name = "John Doe",
                    url = "http://example.com")))
@Default
public final class LaunchCodes implements ContractInterface {

  @Override
  public Context createContext(ChaincodeStub stub) {
    return new LaunchCodeContext(stub);
  }

  @Transaction(name = "Ping", intent = TYPE.EVALUATE)
  public String ping(LaunchCodeContext ignoredCtx) {
    return "Pong";
  }

  @Transaction(name = "RegisterStaffCard", intent = TYPE.SUBMIT)
  public void registerStaffCard(LaunchCodeContext ctx, String cardID, String carHolderName) {
    Card card =
        Card.builder()
            .cardID(cardID)
            .cardHolderName(carHolderName)
            .cardType(CardType.STAFF)
            .build();
    ctx.getRegistry().mustCreate(card);
    ctx.getRegistry().closeDoor(CloseDoorEvent.builder().build());
  }

  @Transaction(name = "RegisterSoldierCard", intent = TYPE.SUBMIT)
  public void registerSoldierCard(LaunchCodeContext ctx, String cardID, String carHolderName) {
    Card card =
        Card.builder()
            .cardID(cardID)
            .cardHolderName(carHolderName)
            .cardType(CardType.SOLDIER)
            .build();
    ctx.getRegistry().mustCreate(card);
    ctx.getRegistry().closeDoor(CloseDoorEvent.builder().build());
  }

  @Transaction(name = "RegisterSecureFacility", intent = TYPE.SUBMIT)
  public void registerSecureFacility(
      LaunchCodeContext ctx,
      String lockID,
      String facilityName,
      String soldierOneID,
      String soldierTwoID) {
    var soldier1 =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierOneID).build(), Card.class);
    var soldier2 =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierTwoID).build(), Card.class);

    if (soldier1.cardType() != CardType.SOLDIER || soldier2.cardType() != CardType.SOLDIER) {
      throw new ChaincodeException("Both cards must belong to soldiers");
    }

    if (soldier1.cardID().equals(soldier2.cardID())) {
      throw new ChaincodeException("Soldiers must be different");
    }

    if (soldier1.secureFacilityID() != null || soldier2.secureFacilityID() != null) {
      throw new ChaincodeException("Soldiers are already assigned to a secure facility");
    }

    SecureFacility secureFacility =
        SecureFacility.builder()
            .lockID(lockID)
            .facilityName(facilityName)
            .soldierOneID(soldierOneID)
            .soldierTwoID(soldierTwoID)
            .build();
    soldier1.secureFacilityID(lockID);
    soldier2.secureFacilityID(lockID);

    ctx.getRegistry().mustCreate(secureFacility);
    ctx.getRegistry().mustUpdate(soldier1);
    ctx.getRegistry().mustUpdate(soldier2);

    ctx.getRegistry().closeDoor(CloseDoorEvent.builder().secureFacilityID(lockID).build());
  }

  @Transaction(name = "RequestEntry", intent = TYPE.SUBMIT)
  public String requestEntry(LaunchCodeContext ctx, String cardID, String lockID) {
    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    if (card.secureFacilityID() != null) {
      throw new ChaincodeException("Card is already present at a secure facility");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(SecureFacility.builder().lockID(lockID).build(), SecureFacility.class);
    if (secureFacility.visitorID() != null) {
      throw new ChaincodeException("Secure facility is already occupied by a visitor");
    }

    if (secureFacility.ongoingEntryRequestID() != null) {
      throw new ChaincodeException("Secure facility already has an ongoing entry request");
    }

    var requestTimestamp = ctx.getStub().getTxTimestamp().toString();
    EntryRequest entryRequest =
        EntryRequest.builder()
            .secureFacilityID(lockID)
            .requestTimestamp(requestTimestamp)
            .requestBy(cardID)
            .status(EntryRequestStatus.PENDING)
            .build();
    var requestID = ctx.getRegistry().createCompositeKey(entryRequest);
    secureFacility.ongoingEntryRequestID(requestID);

    ctx.getRegistry().mustCreate(entryRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder().secureFacilityID(lockID).relatedRequestID(requestID).build());

    return ctx.getRegistry().createCompositeKey(entryRequest);
  }

  @Transaction(name = "ApproveEntry", intent = TYPE.SUBMIT)
  public String approveEntry(LaunchCodeContext ctx, String requestID, String soldierCardID) {
    EntryRequest entryRequest = ctx.getRegistry().mustReadEntryRequestFromCompositeKey(requestID);
    if (entryRequest.status() != EntryRequestStatus.PENDING) {
      throw new ChaincodeException("Entry request is not in pending status");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(entryRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!soldierCardID.equals(secureFacility.soldierOneID())
        && !soldierCardID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    if (entryRequest.authorizingSoldierOne() == null) {
      entryRequest.authorizingSoldierOne(soldierCardID);
    } else if (entryRequest.authorizingSoldierTwo() == null) {
      entryRequest.authorizingSoldierTwo(soldierCardID);
    } else {
      throw new ChaincodeException("Entry request already has two authorizing soldiers");
    }

    if (entryRequest.authorizingSoldierOne() != null
        && entryRequest.authorizingSoldierTwo() != null) {
      entryRequest.status(EntryRequestStatus.APPROVED);
      ctx.getRegistry()
          .openDoor(
              OpenDoorEvent.builder()
                  .secureFacilityID(entryRequest.secureFacilityID())
                  .relatedRequestID(requestID)
                  .build());
    } else {
      ctx.getRegistry()
          .closeDoor(
              CloseDoorEvent.builder()
                  .secureFacilityID(entryRequest.secureFacilityID())
                  .relatedRequestID(requestID)
                  .build());
    }

    ctx.getRegistry().mustUpdate(entryRequest);

    return requestID;
  }

  @Transaction(name = "RejectEntry", intent = TYPE.SUBMIT)
  public String rejectEntry(LaunchCodeContext ctx, String requestID, String soldierCardID) {
    EntryRequest entryRequest = ctx.getRegistry().mustReadEntryRequestFromCompositeKey(requestID);
    if (entryRequest.status() == EntryRequestStatus.ENTERED) {
      throw new ChaincodeException("Entry is already completed");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(entryRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!soldierCardID.equals(secureFacility.soldierOneID())
        && !soldierCardID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    entryRequest.status(EntryRequestStatus.REJECTED);
    secureFacility.ongoingEntryRequestID(null);
    ctx.getRegistry().mustUpdate(entryRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder()
                .secureFacilityID(entryRequest.secureFacilityID())
                .relatedRequestID(requestID)
                .build());

    return requestID;
  }

  @Transaction(name = "LogEntry", intent = TYPE.SUBMIT)
  public void logEntry(LaunchCodeContext ctx, String requestID, String cardID) {
    EntryRequest entryRequest = ctx.getRegistry().mustReadEntryRequestFromCompositeKey(requestID);
    if (entryRequest.status() != EntryRequestStatus.APPROVED) {
      throw new ChaincodeException("Entry request was not approved");
    }

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    if (!card.cardID().equals(entryRequest.requestBy())) {
      throw new ChaincodeException("Card does not match the requestor");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(entryRequest.secureFacilityID()).build(),
                SecureFacility.class);

    entryRequest.status(EntryRequestStatus.ENTERED);
    secureFacility.ongoingEntryRequestID(null);
    secureFacility.visitorID(cardID);
    card.secureFacilityID(entryRequest.secureFacilityID());

    ctx.getRegistry().mustUpdate(entryRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry().mustUpdate(card);

    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder()
                .secureFacilityID(entryRequest.secureFacilityID())
                .relatedRequestID(requestID)
                .build());
  }

  @Transaction(name = "RequestExit", intent = TYPE.SUBMIT)
  public String requestExit(LaunchCodeContext ctx, String cardID, String lockID) {
    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(SecureFacility.builder().lockID(lockID).build(), SecureFacility.class);

    if (card.secureFacilityID() == null || !card.secureFacilityID().equals(lockID)) {
      throw new ChaincodeException("Card is not present at the secure facility");
    }

    if (secureFacility.visitorID() == null || !secureFacility.visitorID().equals(cardID)) {
      throw new ChaincodeException("Card is not the visitor at the secure facility");
    }

    if (secureFacility.ongoingExitRequestID() != null) {
      throw new ChaincodeException("Secure facility already has an ongoing exit request");
    }

    var requestTimestamp = ctx.getStub().getTxTimestamp().toString();
    ExitRequest exitRequest =
        ExitRequest.builder()
            .secureFacilityID(lockID)
            .requestTimestamp(requestTimestamp)
            .requestBy(cardID)
            .status(ExitRequestStatus.PENDING)
            .build();
    var requestID = ctx.getRegistry().createCompositeKey(exitRequest);
    secureFacility.ongoingExitRequestID(requestID);

    ctx.getRegistry().mustCreate(exitRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder().secureFacilityID(lockID).relatedRequestID(requestID).build());

    return ctx.getRegistry().createCompositeKey(exitRequest);
  }

  @Transaction(name = "ApproveExit", intent = TYPE.SUBMIT)
  public String approveExit(LaunchCodeContext ctx, String requestID, String soldierCardID) {
    ExitRequest exitRequest = ctx.getRegistry().mustReadExitRequestFromCompositeKey(requestID);
    if (exitRequest.status() != ExitRequestStatus.PENDING) {
      throw new ChaincodeException("Exit request is not in pending status");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(exitRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!soldierCardID.equals(secureFacility.soldierOneID())
        && !soldierCardID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    if (exitRequest.authorizingSoldierOne() == null) {
      exitRequest.authorizingSoldierOne(soldierCardID);
    } else if (exitRequest.authorizingSoldierTwo() == null) {
      exitRequest.authorizingSoldierTwo(soldierCardID);
    } else {
      throw new ChaincodeException("Exit request already has two authorizing soldiers");
    }

    if (exitRequest.authorizingSoldierOne() != null
        && exitRequest.authorizingSoldierTwo() != null) {
      exitRequest.status(ExitRequestStatus.APPROVED);
      ctx.getRegistry()
          .openDoor(
              OpenDoorEvent.builder()
                  .secureFacilityID(exitRequest.secureFacilityID())
                  .relatedRequestID(requestID)
                  .build());
    } else {
      ctx.getRegistry()
          .closeDoor(
              CloseDoorEvent.builder()
                  .secureFacilityID(exitRequest.secureFacilityID())
                  .relatedRequestID(requestID)
                  .build());
    }

    ctx.getRegistry().mustUpdate(exitRequest);

    return requestID;
  }

  @Transaction(name = "RejectExit", intent = TYPE.SUBMIT)
  public String rejectExit(LaunchCodeContext ctx, String requestID, String soldierCardID) {
    ExitRequest exitRequest = ctx.getRegistry().mustReadExitRequestFromCompositeKey(requestID);
    if (exitRequest.status() == ExitRequestStatus.EXITED) {
      throw new ChaincodeException("Exit is already completed");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(exitRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!soldierCardID.equals(secureFacility.soldierOneID())
        && !soldierCardID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    exitRequest.status(ExitRequestStatus.REJECTED);
    secureFacility.ongoingExitRequestID(null);
    ctx.getRegistry().mustUpdate(exitRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder()
                .secureFacilityID(exitRequest.secureFacilityID())
                .relatedRequestID(requestID)
                .build());

    return requestID;
  }

  @Transaction(name = "LogExit", intent = TYPE.SUBMIT)
  public void logExit(LaunchCodeContext ctx, String requestID, String cardID) {
    ExitRequest exitRequest = ctx.getRegistry().mustReadExitRequestFromCompositeKey(requestID);
    if (exitRequest.status() != ExitRequestStatus.APPROVED) {
      throw new ChaincodeException("Exit request was not approved");
    }

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    if (!card.cardID().equals(exitRequest.requestBy())) {
      throw new ChaincodeException("Card does not match the requestor");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(exitRequest.secureFacilityID()).build(),
                SecureFacility.class);

    exitRequest.status(ExitRequestStatus.EXITED);
    secureFacility.ongoingExitRequestID(null);
    secureFacility.visitorID(cardID);
    card.secureFacilityID(null);

    ctx.getRegistry().mustUpdate(exitRequest);
    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry().mustUpdate(card);

    ctx.getRegistry()
        .closeDoor(
            CloseDoorEvent.builder()
                .secureFacilityID(exitRequest.secureFacilityID())
                .relatedRequestID(requestID)
                .build());
  }

  @Transaction(name = "InitiateShiftChange", intent = TYPE.SUBMIT)
  public String initiateShiftChange(
      LaunchCodeContext ctx, String lockID, String newSoldiersID, String oldSoldiersID) {
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(SecureFacility.builder().lockID(lockID).build(), SecureFacility.class);

    Card newSoldier =
        ctx.getRegistry().mustRead(Card.builder().cardID(newSoldiersID).build(), Card.class);
    Card oldSoldier =
        ctx.getRegistry().mustRead(Card.builder().cardID(oldSoldiersID).build(), Card.class);

    if (newSoldier.cardID().equals(oldSoldier.cardID())) {
      throw new ChaincodeException("New soldier must be different from the old soldier");
    }

    if (newSoldier.cardType() != CardType.SOLDIER) {
      throw new ChaincodeException("New soldier must be a soldier");
    }

    if (newSoldier.cardID().equals(secureFacility.soldierOneID())
        || newSoldier.cardID().equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException("New soldier is already assigned to the secure facility");
    }

    if (newSoldier.secureFacilityID() != lockID
        || secureFacility.visitorID() != newSoldier.cardID()) {
      throw new ChaincodeException("New soldier is not present at the secure facility");
    }

    if (oldSoldier.secureFacilityID() != secureFacility.soldierOneID()
        && oldSoldier.secureFacilityID() != secureFacility.soldierTwoID()) {
      throw new ChaincodeException("Old soldier is not assigned to the secure facility");
    }

    ShiftChangeRequest shiftChangeRequest =
        ShiftChangeRequest.builder()
            .secureFacilityID(lockID)
            .requestTimestamp(ctx.getStub().getTxTimestamp().toString())
            .newSoldiersID(newSoldiersID)
            .oldSoldiersID(oldSoldiersID)
            .status(ShiftChangeRequestStatus.PENDING)
            .build();

    ctx.getRegistry().mustCreate(shiftChangeRequest);
    return ctx.getRegistry().createCompositeKey(shiftChangeRequest);
  }

  @Transaction(name = "ApproveShiftChange", intent = TYPE.SUBMIT)
  public String approveShiftChange(LaunchCodeContext ctx, String requestID, String oldSoldiersID) {
    ShiftChangeRequest shiftChangeRequest =
        ctx.getRegistry().mustReadShiftChangeRequestFromCompositeKey(requestID);
    if (shiftChangeRequest.status() != ShiftChangeRequestStatus.PENDING) {
      throw new ChaincodeException("Shift change request is not in pending status");
    }
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(shiftChangeRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!oldSoldiersID.equals(secureFacility.soldierOneID())
        && !oldSoldiersID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    if (!shiftChangeRequest.oldSoldiersID().equals(oldSoldiersID)) {
      throw new ChaincodeException("Old soldier ID does not match the request");
    }

    shiftChangeRequest.status(ShiftChangeRequestStatus.APPROVED);
    secureFacility.visitorID(oldSoldiersID);
    if (secureFacility.soldierOneID().equals(oldSoldiersID)) {
      secureFacility.soldierOneID(shiftChangeRequest.newSoldiersID());
    } else {
      secureFacility.soldierTwoID(shiftChangeRequest.newSoldiersID());
    }

    ctx.getRegistry().mustUpdate(secureFacility);
    ctx.getRegistry().mustUpdate(shiftChangeRequest);

    return requestID;
  }

  @Transaction(name = "RejectShiftChange", intent = TYPE.SUBMIT)
  public String rejectShiftChange(LaunchCodeContext ctx, String requestID, String soldierID) {
    ShiftChangeRequest shiftChangeRequest =
        ctx.getRegistry().mustReadShiftChangeRequestFromCompositeKey(requestID);
    if (shiftChangeRequest.status() != ShiftChangeRequestStatus.PENDING) {
      throw new ChaincodeException("Shift change request is not in pending status");
    }
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().lockID(shiftChangeRequest.secureFacilityID()).build(),
                SecureFacility.class);

    if (!soldierID.equals(secureFacility.soldierOneID())
        && !soldierID.equals(secureFacility.soldierTwoID())) {
      throw new ChaincodeException(
          "Card must be one of the soldiers assigned to the secure facility");
    }

    if (!shiftChangeRequest.oldSoldiersID().equals(soldierID)) {
      throw new ChaincodeException("Old soldier ID does not match the request");
    }

    shiftChangeRequest.status(ShiftChangeRequestStatus.REJECTED);
    ctx.getRegistry().mustUpdate(shiftChangeRequest);

    return requestID;
  }

  // QUERIES

  @Transaction(name = "GetSecureFacility", intent = TYPE.EVALUATE)
  public String getSecureFacility(LaunchCodeContext ctx, String lockID) {
    var secureFacility =
        ctx.getRegistry()
            .tryRead(SecureFacility.builder().lockID(lockID).build(), SecureFacility.class);
    if (secureFacility == null) {
      return null;
    }
    return secureFacility.toJsonString();
  }

  @Transaction(name = "GetAllSecureFacilities", intent = TYPE.EVALUATE)
  public String getAllSecureFacilities(LaunchCodeContext ctx) {
    return serialize(
        ctx.getRegistry()
            .readAllAssetOfType(SecureFacility.builder().build(), SecureFacility.class));
  }

  @Transaction(name = "GetEntryRequest", intent = TYPE.EVALUATE)
  public String getEntryRequest(LaunchCodeContext ctx, String requestID) {
    var entryRequest = ctx.getRegistry().tryReadEntryRequestFromCompositeKey(requestID);
    if (entryRequest == null) {
      return entryRequest.toJsonString();
    }
    return entryRequest.toJsonString();
  }

  @Transaction(name = "GetAllEntryRequests", intent = TYPE.EVALUATE)
  public String getAllEntryRequests(LaunchCodeContext ctx) {
    return serialize(
        ctx.getRegistry().readAllAssetOfType(EntryRequest.builder().build(), EntryRequest.class));
  }

  @Transaction(name = "GetExitRequest", intent = TYPE.EVALUATE)
  public String getExitRequest(LaunchCodeContext ctx, String requestID) {
    var exitRequest = ctx.getRegistry().tryReadExitRequestFromCompositeKey(requestID);
    if (exitRequest == null) {
      return null;
    }
    return exitRequest.toJsonString();
  }

  @Transaction(name = "GetAllExitRequests", intent = TYPE.EVALUATE)
  public String getAllExitRequests(LaunchCodeContext ctx) {
    return serialize(
        ctx.getRegistry().readAllAssetOfType(ExitRequest.builder().build(), ExitRequest.class));
  }

  @Transaction(name = "GetShiftChangeRequest", intent = TYPE.EVALUATE)
  public String getShiftChangeRequest(LaunchCodeContext ctx, String requestID) {
    var shiftChangeRequest = ctx.getRegistry().tryReadShiftChangeRequestFromCompositeKey(requestID);
    if (shiftChangeRequest == null) {
      return null;
    }
    return shiftChangeRequest.toJsonString();
  }

  @Transaction(name = "GetAllShiftChangeRequests", intent = TYPE.EVALUATE)
  public String getAllShiftChangeRequests(LaunchCodeContext ctx) {
    return serialize(
        ctx.getRegistry()
            .readAllAssetOfType(ShiftChangeRequest.builder().build(), ShiftChangeRequest.class));
  }

  @Transaction(name = "GetCard", intent = TYPE.EVALUATE)
  public String getCard(LaunchCodeContext ctx, String cardID) {
    var card = ctx.getRegistry().tryRead(Card.builder().cardID(cardID).build(), Card.class);
    if (card == null) {
      return null;
    }
    return card.toJsonString();
  }

  @Transaction(name = "GetAllCards", intent = TYPE.EVALUATE)
  public String getAllCards(LaunchCodeContext ctx) {
    return serialize(ctx.getRegistry().readAllAssetOfType(Card.builder().build(), Card.class));
  }
}
