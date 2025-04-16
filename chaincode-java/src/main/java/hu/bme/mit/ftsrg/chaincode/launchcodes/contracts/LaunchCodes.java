/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.contracts;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.CardType;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.SecureFacility;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequest;
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
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (carHolderName == null || carHolderName.isEmpty()) {
      throw new ChaincodeException("Card holder name cannot be null or empty");
    }

    Card card =
        Card.builder()
            .cardID(cardID)
            .cardHolderName(carHolderName)
            .cardType(CardType.STAFF)
            .build();
    ctx.getService().registerStaffCard(card);
  }

  @Transaction(name = "RegisterSoldierCard", intent = TYPE.SUBMIT)
  public void registerSoldierCard(LaunchCodeContext ctx, String cardID, String carHolderName) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (carHolderName == null || carHolderName.isEmpty()) {
      throw new ChaincodeException("Card holder name cannot be null or empty");
    }

    Card card =
        Card.builder()
            .cardID(cardID)
            .cardHolderName(carHolderName)
            .cardType(CardType.SOLDIER)
            .build();
    ctx.getService().registerSoldierCard(card);
  }

  @Transaction(name = "RegisterSecureFacility", intent = TYPE.SUBMIT)
  public void registerSecureFacility(
      LaunchCodeContext ctx,
      String facilityID,
      String facilityName,
      String soldierOneID,
      String soldierTwoID) {
    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }
    if (facilityName == null || facilityName.isEmpty()) {
      throw new ChaincodeException("Facility name cannot be null or empty");
    }
    if (soldierOneID == null || soldierOneID.isEmpty()) {
      throw new ChaincodeException("Soldier One ID cannot be null or empty");
    }
    if (soldierTwoID == null || soldierTwoID.isEmpty()) {
      throw new ChaincodeException("Soldier Two ID cannot be null or empty");
    }

    var soldier1 =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierOneID).build(), Card.class);
    var soldier2 =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierTwoID).build(), Card.class);

    SecureFacility secureFacility =
        SecureFacility.builder().facilityID(facilityID).facilityName(facilityName).build();

    ctx.getService().registerSecureFacility(secureFacility, soldier1, soldier2);
  }

  @Transaction(name = "RequestEntry", intent = TYPE.SUBMIT)
  public void requestEntry(LaunchCodeContext ctx, String facilityID, String cardID) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().requestEntry(secureFacility, card);
  }

  @Transaction(name = "ApproveEntry", intent = TYPE.SUBMIT)
  public void approveEntry(LaunchCodeContext ctx, String facilityID, String soldierCardID) {
    if (soldierCardID == null || soldierCardID.isEmpty()) {
      throw new ChaincodeException("Soldier Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card soldierCard =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierCardID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().approveEntry(secureFacility, soldierCard);
  }

  @Transaction(name = "RejectEntry", intent = TYPE.SUBMIT)
  public void rejectEntry(LaunchCodeContext ctx, String facilityID, String soldierCardID) {
    if (soldierCardID == null || soldierCardID.isEmpty()) {
      throw new ChaincodeException("Soldier Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card card =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierCardID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().rejectEntry(secureFacility, card);
  }

  @Transaction(name = "LogEntry", intent = TYPE.SUBMIT)
  public void logEntry(LaunchCodeContext ctx, String facilityID, String cardID) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);

    ctx.getService().logEntry(secureFacility, card);
  }

  @Transaction(name = "RequestExit", intent = TYPE.SUBMIT)
  public void requestExit(LaunchCodeContext ctx, String facilityID, String cardID) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);
    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().requestExit(secureFacility, card);
  }

  @Transaction(name = "ApproveExit", intent = TYPE.SUBMIT)
  public void approveExit(LaunchCodeContext ctx, String facilityID, String soldierCardID) {
    if (soldierCardID == null || soldierCardID.isEmpty()) {
      throw new ChaincodeException("Soldier Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card card =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierCardID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().approveExit(secureFacility, card);
  }

  @Transaction(name = "RejectExit", intent = TYPE.SUBMIT)
  public void rejectExit(LaunchCodeContext ctx, String facilityID, String soldierCardID) {
    if (soldierCardID == null || soldierCardID.isEmpty()) {
      throw new ChaincodeException("Soldier Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card card =
        ctx.getRegistry().mustRead(Card.builder().cardID(soldierCardID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().rejectExit(secureFacility, card);
  }

  @Transaction(name = "LogExit", intent = TYPE.SUBMIT)
  public void logExit(LaunchCodeContext ctx, String facilityID, String cardID) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    Card card = ctx.getRegistry().mustRead(Card.builder().cardID(cardID).build(), Card.class);

    ctx.getService().logExit(secureFacility, card);
  }

  @Transaction(name = "InitiateShiftChange", intent = TYPE.SUBMIT)
  public void initiateShiftChange(
      LaunchCodeContext ctx, String facilityID, String newSoldiersID, String oldSoldiersID) {
    if (newSoldiersID == null || newSoldiersID.isEmpty()) {
      throw new ChaincodeException("New Soldier ID cannot be null or empty");
    }

    if (oldSoldiersID == null || oldSoldiersID.isEmpty()) {
      throw new ChaincodeException("Old Soldier ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    Card newSoldier =
        ctx.getRegistry().mustRead(Card.builder().cardID(newSoldiersID).build(), Card.class);
    Card oldSoldier =
        ctx.getRegistry().mustRead(Card.builder().cardID(oldSoldiersID).build(), Card.class);

    ctx.getService().initiateShiftChange(secureFacility, newSoldier, oldSoldier);
  }

  @Transaction(name = "ApproveShiftChange", intent = TYPE.SUBMIT)
  public void approveShiftChange(LaunchCodeContext ctx, String facilityID, String oldSoldiersID) {
    if (oldSoldiersID == null || oldSoldiersID.isEmpty()) {
      throw new ChaincodeException("Old Soldier ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card oldSoldierCard =
        ctx.getRegistry().mustRead(Card.builder().cardID(oldSoldiersID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().approveShiftChange(secureFacility, oldSoldierCard);
  }

  @Transaction(name = "RejectShiftChange", intent = TYPE.SUBMIT)
  public void rejectShiftChange(LaunchCodeContext ctx, String facilityID, String oldSoldiersID) {
    if (oldSoldiersID == null || oldSoldiersID.isEmpty()) {
      throw new ChaincodeException("Old Soldier ID cannot be null or empty");
    }

    if (facilityID == null || facilityID.isEmpty()) {
      throw new ChaincodeException("Facility ID cannot be null or empty");
    }

    Card oldSoldierCard =
        ctx.getRegistry().mustRead(Card.builder().cardID(oldSoldiersID).build(), Card.class);

    SecureFacility secureFacility =
        ctx.getRegistry()
            .mustRead(
                SecureFacility.builder().facilityID(facilityID).build(), SecureFacility.class);

    ctx.getService().rejectShiftChange(secureFacility, oldSoldierCard);
  }

  // QUERIES

  @Transaction(name = "GetSecureFacility", intent = TYPE.EVALUATE)
  public String getSecureFacility(LaunchCodeContext ctx, String lockID) {
    var secureFacility =
        ctx.getRegistry()
            .tryRead(SecureFacility.builder().facilityID(lockID).build(), SecureFacility.class);
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
  public String getEntryRequest(
      LaunchCodeContext ctx, String facilityID, String requestTimestampString) {
    var entryRequest =
        ctx.getRegistry()
            .tryRead(
                EntryRequest.builder()
                    .secureFacilityID(facilityID)
                    .requestTimestamp(requestTimestampString)
                    .build(),
                EntryRequest.class);
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
  public String getExitRequest(
      LaunchCodeContext ctx, String facilityID, String requestTimestampString) {
    var exitRequest =
        ctx.getRegistry()
            .tryRead(
                ExitRequest.builder()
                    .secureFacilityID(facilityID)
                    .requestTimestamp(requestTimestampString)
                    .build(),
                ExitRequest.class);
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
  public String getShiftChangeRequest(
      LaunchCodeContext ctx, String facilityID, String requestTimestampString) {
    var shiftChangeRequest =
        ctx.getRegistry()
            .tryRead(
                ShiftChangeRequest.builder()
                    .secureFacilityID(facilityID)
                    .requestTimestamp(requestTimestampString)
                    .build(),
                ShiftChangeRequest.class);
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
