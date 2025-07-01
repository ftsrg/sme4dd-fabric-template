/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.AssetBase;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import java.util.ArrayList;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

public class LaunchCodesRegistry {

  private ChaincodeStub stub;

  public LaunchCodesRegistry(ChaincodeStub stub) {
    if (stub == null) {
      throw new ChaincodeException("ChaincodeStub cannot be null");
    }

    this.stub = stub;
  }

  public ChaincodeStub getStub() {
    return stub;
  }

  public void closeDoor(CloseDoorEvent event) {
    if (event == null) {
      throw new ChaincodeException("Close door event cannot be null");
    }

    getStub()
        .setEvent(
            CloseDoorEvent.class.getName(),
            serialize(event).getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  public void openDoor(OpenDoorEvent event) {
    if (event == null) {
      throw new ChaincodeException("Open door event cannot be null");
    }

    getStub()
        .setEvent(
            OpenDoorEvent.class.getName(),
            serialize(event).getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }

  public String createCompositeKey(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    return getStub()
        .createCompositeKey(asset.getTypeForCompositeKey(), asset.getAttributesForCompositeKey())
        .toString();
  }

  public boolean exists(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    String compositeKey = createCompositeKey(asset);
    var assetString = getStub().getStringState(compositeKey);
    return assetString != null && !assetString.isEmpty();
  }

  public void mustNotExist(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    if (exists(asset)) {
      throw new ChaincodeException("Asset already exists: " + asset.toJsonString());
    }
  }

  public void mustExist(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    if (!exists(asset)) {
      throw new ChaincodeException("Asset not found: " + asset.toJsonString());
    }
  }

  public void mustCreate(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    mustNotExist(asset);
    String compositeKey = createCompositeKey(asset);
    getStub().putStringState(compositeKey, asset.toJsonString());
  }

  public <T> T tryRead(AssetBase asset, Class<T> clazz) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    if (clazz == null) {
      throw new ChaincodeException("Class type cannot be null");
    }

    String compositeKey = createCompositeKey(asset);
    var assetString = getStub().getStringState(compositeKey);
    if (assetString == null || assetString.isEmpty()) {
      return null;
    }
    return deserialize(assetString, clazz);
  }

  public <T> T mustRead(AssetBase asset, Class<T> clazz) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    if (clazz == null) {
      throw new ChaincodeException("Class type cannot be null");
    }

    T result = tryRead(asset, clazz);
    if (result == null) {
      throw new ChaincodeException("Asset not found: " + asset.toJsonString());
    }
    return result;
  }

  public void mustUpdate(AssetBase asset) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    mustExist(asset);
    String compositeKey = createCompositeKey(asset);
    getStub().putStringState(compositeKey, asset.toJsonString());
  }

  public <T> ArrayList<T> readAllAssetOfType(AssetBase asset, Class<T> clazz) {
    if (asset == null) {
      throw new ChaincodeException("Asset cannot be null");
    }

    if (clazz == null) {
      throw new ChaincodeException("Class type cannot be null");
    }

    CompositeKey partialKey = getStub().createCompositeKey(asset.getTypeForCompositeKey());
    var iterator = getStub().getStateByPartialCompositeKey(partialKey);
    var result = new ArrayList<T>();

    try (var iteratorClosable = iterator) {
      iterator.forEach(
          entry -> {
            var assetString = entry.getStringValue();
            if (assetString == null || assetString.isEmpty()) {
              return;
            }
            var deserializedAsset = deserialize(assetString, clazz);
            result.add(deserializedAsset);
          });
    } catch (Exception e) {
      throw new ChaincodeException(
          String.format("Error reading assets of type %s", asset.getTypeForCompositeKey()), e);
    }

    return result;
  }

  public String getTransactionTimestamp() {
    return getStub().getTxTimestamp().toString();
  }
}
