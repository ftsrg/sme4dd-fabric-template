/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.contracts;

import static hu.bme.mit.ftsrg.chaincode.launchcodes.util.Serializer.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.AssetBase;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.Card;
import hu.bme.mit.ftsrg.chaincode.launchcodes.assets.CardType;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.CloseDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.events.OpenDoorEvent;
import hu.bme.mit.ftsrg.chaincode.launchcodes.util.LaunchCodeContext;
import hu.bme.mit.ftsrg.chaincode.launchcodes.util.LaunchCodesRegistry;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
@Disabled("Ignoring the entire test suite, intended for students who use stub directly")
final class LaunchCodesTest {

  LaunchCodes contract;
  @Mock private LaunchCodeContext ctx;
  @Mock private LaunchCodesRegistry registry;
  @Mock private ChaincodeStub stub;

  private String toJson(Object obj) {
    return serialize(obj);
  }

  private byte[] toBytes(Object obj) {
    return toJson(obj).getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  @BeforeEach
  void setup() {
    contract = new LaunchCodes();
    given(ctx.getRegistry()).willReturn(registry);
  }

  void createdExactlyOnceInRegistry(AssetBase newAsset) {
    verify(ctx.getRegistry(), times(1)).mustCreate(argThat(asset -> asset.equals(newAsset)));
  }

  void createdExactlyOnceInStub(AssetBase newAsset) {
    String keyString =
        new CompositeKey(newAsset.getTypeForCompositeKey(), newAsset.getAttributesForCompositeKey())
            .toString();
    verify(stub, atLeastOnce())
        .getStringState(eq(keyString)); // allow multiple reads, since no strict caching
    assertThat(stub.getStringState(keyString)).isEqualTo(""); // it didn't exist before
    verify(stub, times(1)).putStringState(eq(keyString), eq(toJson(newAsset)));
  }

  void closeDoorEventFiredOnceInRegistry(CloseDoorEvent firedEvent) {
    verify(ctx.getRegistry()).closeDoor(argThat(event -> event.equals(firedEvent)));
  }

  void closeDoorEventFiredOnceInStub(CloseDoorEvent firedEvent) {
    verify(stub, times(1)).setEvent(eq(CloseDoorEvent.class.getName()), eq(toBytes(firedEvent)));
  }

  void openDoorEventFiredOnceInRegistry(OpenDoorEvent firedEvent) {
    verify(ctx.getRegistry()).openDoor(argThat(event -> event.equals(firedEvent)));
    verify(stub, times(1)).setEvent(eq(OpenDoorEvent.class.getName()), eq(toBytes(firedEvent)));
  }

  void openDoorEventFiredOnceInStub(OpenDoorEvent firedEvent) {
    verify(stub, times(1)).setEvent(eq(OpenDoorEvent.class.getName()), eq(toBytes(firedEvent)));
  }

  @Test
  void registerStaffCard_creates_staff_card_successfully() {
    // Arrange

    String cardID = "staff123";
    String cardHolderName = "John Doe";

    // Act
    contract.registerStaffCard(ctx, cardID, cardHolderName);

    // Assert
    Card resultingCard =
        Card.builder()
            .cardID(cardID)
            .cardHolderName(cardHolderName)
            .cardType(CardType.STAFF)
            .build();

    createdExactlyOnceInRegistry(resultingCard);

    CloseDoorEvent resultingEvent = CloseDoorEvent.builder().build();
    closeDoorEventFiredOnceInRegistry(resultingEvent);

    verifyNoMoreInteractions(ctx.getRegistry());
  }
}
