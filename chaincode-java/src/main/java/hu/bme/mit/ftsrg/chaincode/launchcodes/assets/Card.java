/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hyperledger.fabric.shim.ChaincodeException;

@Data
@NoArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents a card's information.
public class Card implements AssetBase {
  public static final Card TypeQueryInstance = new Card("TypeQueryInstance", null, null, null);

  String cardID; // ID of the card
  String cardHolderName; // Name of the card holder
  String secureFacilityID; // ID of the secure facility the card is at, or null
  CardType cardType; // Type of the card (Staff or Soldier)

  public Card(String cardID, String cardHolderName, String secureFacilityID, CardType cardType) {
    if (cardID == null || cardID.isEmpty()) {
      throw new ChaincodeException("Card ID cannot be null or empty");
    }

    this.cardID = cardID;
    this.cardHolderName = cardHolderName;
    this.secureFacilityID = secureFacilityID;
    this.cardType = cardType;
  }

  @Override
  public String getTypeForCompositeKey() {
    return Card.class.getName();
  }

  @Override
  public String[] getAttributesForCompositeKey() {
    return new String[] {cardID};
  }

  // checks
  public boolean isSoldier() {
    return cardType == CardType.SOLDIER;
  }

  public boolean isStaff() {
    return cardType == CardType.STAFF;
  }

  public boolean isDifferentCard(Card otherCard) {
    return !this.cardID.equals(otherCard.cardID);
  }

  public boolean isUnassigned() {
    return secureFacilityID == null;
  }

  // assertions
  public void assertStaffCard() {
    if (!isStaff()) {
      throw new ChaincodeException(
          String.format("Card %s does not belong to a staff member", cardID));
    }
  }

  public void assertSoldierCard() {
    if (!isSoldier()) {
      throw new ChaincodeException(String.format("Card %s does not belong to a soldier", cardID));
    }
  }

  public void assertDifferentCard(Card otherCard) {
    if (!isDifferentCard(otherCard)) {
      throw new ChaincodeException(
          String.format("Cards %s and %s must be different", this.cardID, otherCard.cardID));
    }
  }

  public void assertUnassigned() {
    if (!isUnassigned()) {
      throw new ChaincodeException(
          String.format("Card %s is already assigned to a secure facility", cardID));
    }
  }

  public void assertAssigned() {
    if (isUnassigned()) {
      throw new ChaincodeException(
          String.format("Card %s is not assigned to a secure facility", cardID));
    }
  }
}
