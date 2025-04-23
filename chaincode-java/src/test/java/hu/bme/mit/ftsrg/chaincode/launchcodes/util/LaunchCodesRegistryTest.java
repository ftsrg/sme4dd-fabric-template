/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.AssetBase;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
final class LaunchCodesRegistryTest {

  // UTILS

  private CompositeKey createCompositeKey(AssetBase asset) {
    return new CompositeKey(asset.getTypeForCompositeKey(), asset.getAttributesForCompositeKey());
  }

  private String toJson(Object obj) {
    return serialize(obj);
  }

  private byte[] toBytes(Object obj) {
    return toJson(obj).getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  // SETTERS
  private void setExpectedStringState(AssetBase asset, String expectedStringState) {
    given(stub.getStringState(createCompositeKey(asset).toString())).willReturn(expectedStringState);
  }

  private String setAssetOnLedger(AssetBase asset) {
    var expectedStringState = toJson(asset);
    setExpectedStringState(asset, expectedStringState);
    return expectedStringState;
  }

  private String unsetAssetOnLedger(AssetBase asset) {
    var expectedStringState = "";
    setExpectedStringState(asset, expectedStringState);
    return expectedStringState;
  }

  private CompositeKey setCompositeKeyForAsset(AssetBase asset) {
    var key = createCompositeKey(asset);
    given(
            stub.createCompositeKey(
                asset.getTypeForCompositeKey(), asset.getAttributesForCompositeKey()))
        .willReturn(key);
    return key;
  }

  // VERIFIERS

  private void verifyReadAtLeastOnce(AssetBase asset) {
    verify(stub, atLeastOnce())
        .getStringState(
            eq(
                createCompositeKey(asset)
                    .toString())); // allow multiple reads, since no strict caching
  }

  private void verifyCreatedOnce(AssetBase newAsset) {
    verify(stub, times(1))
        .putStringState(eq(createCompositeKey(newAsset).toString()), eq(toJson(newAsset)));
  }

  private void verifyCloseDoorEventFiredOnce(CloseDoorEvent firedEvent) {
    verify(stub, times(1)).setEvent(eq(CloseDoorEvent.class.getName()), eq(toBytes(firedEvent)));
  }

  private void verifyOpenDoorEventFiredOnce(OpenDoorEvent firedEvent) {
    verify(stub, times(1)).setEvent(eq(OpenDoorEvent.class.getName()), eq(toBytes(firedEvent)));
  }

  // TESTS

  @Mock private ChaincodeStub stub;
  LaunchCodesRegistry registry;

  @BeforeEach
  void setUp() {
    registry = new LaunchCodesRegistry(stub);
  }

  @Nested
  class constructor_tests {

    @Test
    void constructor_should_throw_exception_when_stub_is_null() {
      assertThrows(ChaincodeException.class, () -> new LaunchCodesRegistry(null));
    }

    @Test
    void constructor_should_not_throw_exception_when_stub_is_not_null() {
      assertNotNull(new LaunchCodesRegistry(stub));
    }
  }

  @Nested
  class getStub_tests {

    @Test
    void getStub_should_return_the_stub() {
      assertEquals(stub, registry.getStub());
    }
  }

  @Nested
  class closeDoor_tests {

    @Test
    void closeDoor_should_throw_exception_when_event_is_null() {
      assertThrows(ChaincodeException.class, () -> registry.closeDoor(null));
    }

    @Test
    void closeDoor_should_fire_event() {
      // Arrange
      CloseDoorEvent event = new CloseDoorEvent("ID");

      // Act
      registry.closeDoor(event);

      // Assert
      verifyCloseDoorEventFiredOnce(event);
    }
  }

  @Nested
  class openDoor_tests {

    @Test
    void openDoor_should_throw_exception_when_event_is_null() {
      assertThrows(ChaincodeException.class, () -> registry.openDoor(null));
    }

    @Test
    void openDoor_should_fire_event() {
      // Arrange
      OpenDoorEvent event = new OpenDoorEvent("ID");

      // Act
      registry.openDoor(event);

      // Assert
      verifyOpenDoorEventFiredOnce(event);
    }
  }

  @Nested
  class createCompositeKey_tests {

    @Mock private CompositeKey compositeKey;

    @Test
    void createCompositeKey_should_throw_exception_when_asset_is_null() {
      assertThrows(ChaincodeException.class, () -> registry.createCompositeKey(null));
    }

    @Test
    void createCompositeKey_should_create_composite_key_from_asset_attributes() {
      // Arrange
      AssetBase asset = new Card("ID", null, null, null);
      var expectedKeyString = setCompositeKeyForAsset(asset).toString();

      // Act
      var returnedKeyString = registry.createCompositeKey(asset);

      // Assert
      verify(stub, times(1))
          .createCompositeKey(asset.getTypeForCompositeKey(), asset.getAttributesForCompositeKey());
      assertEquals(expectedKeyString, returnedKeyString);
    }
  }

  @Nested
  class exists_tests {

    @Test
    void exists_should_throw_exception_when_asset_is_null() {
      assertThrows(ChaincodeException.class, () -> registry.exists(null));
    }

    @Test
    void exists_should_return_true_when_asset_exists() {
      // Arrange
      AssetBase asset = new Card("ID", null, null, null);
      setCompositeKeyForAsset(asset);
      setAssetOnLedger(asset);

      // Act
      boolean result = registry.exists(asset);

      // Assert
      assertEquals(true, result);
      verifyReadAtLeastOnce(asset);
    }

    @Test
    void exists_should_return_false_when_asset_does_not_exist() {
      // Arrange
      AssetBase asset = new Card("ID", null, null, null);
      setCompositeKeyForAsset(asset);
      unsetAssetOnLedger(asset);

      // Act
      boolean result = registry.exists(asset);

      // Assert
      assertEquals(false, result);
      verifyReadAtLeastOnce(asset);
    }
  }
}
