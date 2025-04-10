/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.AssetBase;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.EntryRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ExitRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.ShiftChangeRequest;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import java.util.ArrayList;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

public class LaunchCodeRegistry {

  private ChaincodeStub stub;

  public ChaincodeStub getStub() {
    return stub;
  }

  public LaunchCodeRegistry(ChaincodeStub stub) {
    this.stub = stub;
  }

  public void closeDoor(CloseDoorEvent event) {
    getStub().setEvent(
        CloseDoorEvent.class.getName(),
        serialize(event).getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  public void openDoor(OpenDoorEvent event) {
    getStub().setEvent(
        OpenDoorEvent.class.getName(),
        serialize(event).getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  public String createCompositeKey(AssetBase asset) {
    return getStub().createCompositeKey(
            asset.getTypeForCompositeKey(), asset.getAttributesForCompositeKey())
        .toString();
  }

  public boolean exists(AssetBase asset) {
    String compositeKey = createCompositeKey(asset);
    var assetString = getStub().getStringState(compositeKey);
    return assetString != null && !assetString.isEmpty();
  }

  public void mustNotExist(AssetBase asset) {
    if (exists(asset)) {
      throw new IllegalStateException("Asset already exists: " + asset.toJsonString());
    }
  }

  public void mustExist(AssetBase asset) {
    if (!exists(asset)) {
      throw new IllegalStateException("Asset not found: " + asset.toJsonString());
    }
  }

  public void mustCreate(AssetBase asset) {
    mustNotExist(asset);
    String compositeKey = createCompositeKey(asset);
    getStub().putStringState(compositeKey, asset.toJsonString());
  }

  public <T> T tryRead(AssetBase asset, Class<T> clazz) {
    String compositeKey = createCompositeKey(asset);
    var assetString = getStub().getStringState(compositeKey);
    if (assetString == null || assetString.isEmpty()) {
      return null;
    }
    return deserialize(assetString, clazz);
  }

  public <T> T mustRead(AssetBase asset, Class<T> clazz) {
    T result = tryRead(asset, clazz);
    if (result == null) {
      throw new IllegalStateException("Asset not found: " + asset.toJsonString());
    }
    return result;
  }

  public EntryRequest tryReadEntryRequestFromCompositeKey(String requestID) {
    CompositeKey entryRequestKey = CompositeKey.parseCompositeKey(requestID);
    String requestType = entryRequestKey.getObjectType();
    if (!requestType.equals(EntryRequest.class.getName())) {
      throw new ChaincodeException(String.format("Invalid request type: '%s'", requestType));
    }

    String secureFacilityID = entryRequestKey.getAttributes().get(0);
    String requestTimestamp = entryRequestKey.getAttributes().get(1);

    return tryRead(
        EntryRequest.builder()
            .secureFacilityID(secureFacilityID)
            .requestTimestamp(requestTimestamp)
            .build(),
        EntryRequest.class);
  }

  public EntryRequest mustReadEntryRequestFromCompositeKey(String requestID) {
    EntryRequest entryRequest = tryReadEntryRequestFromCompositeKey(requestID);
    if (entryRequest == null) {
      throw new ChaincodeException("Entry request not found: " + requestID);
    }
    return entryRequest;
  }

  public ExitRequest tryReadExitRequestFromCompositeKey(String requestID) {
    CompositeKey exitRequestKey = CompositeKey.parseCompositeKey(requestID);
    String requestType = exitRequestKey.getObjectType();
    if (!requestType.equals(ExitRequest.class.getName())) {
      throw new ChaincodeException(String.format("Invalid request type: '%s'", requestType));
    }

    String secureFacilityID = exitRequestKey.getAttributes().get(0);
    String requestTimestamp = exitRequestKey.getAttributes().get(1);

    return tryRead(
        ExitRequest.builder()
            .secureFacilityID(secureFacilityID)
            .requestTimestamp(requestTimestamp)
            .build(),
        ExitRequest.class);
  }

  public ExitRequest mustReadExitRequestFromCompositeKey(String requestID) {
    ExitRequest exitRequest = tryReadExitRequestFromCompositeKey(requestID);
    if (exitRequest == null) {
      throw new ChaincodeException("Exit request not found: " + requestID);
    }
    return exitRequest;
  }

  public ShiftChangeRequest tryReadShiftChangeRequestFromCompositeKey(String requestID) {
    CompositeKey shiftChangeRequestKey = CompositeKey.parseCompositeKey(requestID);
    String requestType = shiftChangeRequestKey.getObjectType();
    if (!requestType.equals(ShiftChangeRequest.class.getName())) {
      throw new ChaincodeException(String.format("Invalid request type: '%s'", requestType));
    }

    String secureFacilityID = shiftChangeRequestKey.getAttributes().get(0);
    String requestTimestamp = shiftChangeRequestKey.getAttributes().get(1);

    return tryRead(
        ShiftChangeRequest.builder()
            .secureFacilityID(secureFacilityID)
            .requestTimestamp(requestTimestamp)
            .build(),
        ShiftChangeRequest.class);
  }

  public ShiftChangeRequest mustReadShiftChangeRequestFromCompositeKey(String requestID) {
    ShiftChangeRequest shiftChangeRequest = tryReadShiftChangeRequestFromCompositeKey(requestID);
    if (shiftChangeRequest == null) {
      throw new ChaincodeException("Shift change request not found: " + requestID);
    }
    return shiftChangeRequest;
  }

  public <T> ArrayList<T> readAllAssetOfType(AssetBase asset, Class<T> clazz) {
    CompositeKey partialKey = getStub().createCompositeKey(asset.getTypeForCompositeKey());
    var iterator = getStub().getStateByPartialCompositeKey(partialKey).iterator();
    var result = new ArrayList<T>();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      var assetString = entry.getStringValue();
      if (assetString == null || assetString.isEmpty()) {
        continue;
      }
      var deserializedAsset = deserialize(assetString, clazz);
      result.add(deserializedAsset);
    }

    return result;
  }

  public void mustUpdate(AssetBase asset) {
    mustExist(asset);
    String compositeKey = createCompositeKey(asset);
    getStub().putStringState(compositeKey, asset.toJsonString());
  }
}
