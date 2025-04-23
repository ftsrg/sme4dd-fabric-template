/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
final class CardTest {

  @Nested
  class constructor_test {
    @Test
    void should_create_card_using_constructor() {
      // Arrange & Act
      Card card = new Card("ID", "Name", "FacilityID", CardType.SOLDIER);

      // Assert
      assertEquals("ID", card.cardID());
      assertEquals("Name", card.cardHolderName());
      assertEquals("FacilityID", card.secureFacilityID());
      assertEquals(CardType.SOLDIER, card.cardType());
    }

    @Test
    void should_create_card_using_builder() {
      // Arrange & Act
      Card card =
          Card.builder()
              .cardID("ID")
              .cardHolderName("Name")
              .secureFacilityID("FacilityID")
              .cardType(CardType.SOLDIER)
              .build();

      // Assert
      assertEquals("ID", card.cardID());
      assertEquals("Name", card.cardHolderName());
      assertEquals("FacilityID", card.secureFacilityID());
      assertEquals(CardType.SOLDIER, card.cardType());
    }

    @Test
    void should_throw_exception_when_id_is_null_using_builder() {
      // Act & Assert
      assertThrows(ChaincodeException.class, () -> Card.builder().build());
    }

    @Test
    void should_throw_exception_when_id_is_null_using_constructor() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class, () -> new Card(null, "Name", "FacilityID", CardType.SOLDIER));
    }

    @Test
    void should_throw_exception_when_id_is_empty_using_builder() {
      // Act & Assert
      assertThrows(ChaincodeException.class, () -> Card.builder().cardID("").build());
    }

    @Test
    void should_throw_exception_when_id_is_empty_using_constructor() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class, () -> new Card("", "Name", "FacilityID", CardType.SOLDIER));
    }
  }

  @Nested
  class getTypeForCompositeKey_tests {
    @Test
    void returns_correct_type_name() {
      // Arrange
      Card card = new Card();
      String expectedType = Card.class.getName();

      // Act
      String actualType = card.getTypeForCompositeKey();

      // Assert
      assertEquals(expectedType, actualType);
    }
  }

  @Nested
  class getAttributesForCompositeKey_tests {
    @Test
    void returns_correct_attributes() {
      // Arrange
      Card card = Card.builder().cardID("ID").build();
      String[] expectedAttributes = new String[] {card.cardID()};

      // Act
      String[] actualAttributes = card.getAttributesForCompositeKey();

      // Assert
      assertArrayEquals(expectedAttributes, actualAttributes);
    }
  }

  @Nested
  class isSoldier_tests {
    @Test
    void returns_true_for_soldier_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.SOLDIER).build();

      // Act
      boolean result = card.isSoldier();

      // Assert
      assertEquals(true, result);
    }

    @Test
    void returns_false_for_staff_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.STAFF).build();

      // Act
      boolean result = card.isSoldier();

      // Assert
      assertEquals(false, result);
    }
  }

  @Nested
  class isStaff_tests {
    @Test
    void returns_true_for_staff_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.STAFF).build();

      // Act
      boolean result = card.isStaff();

      // Assert
      assertEquals(true, result);
    }

    @Test
    void returns_false_for_soldier_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.SOLDIER).build();

      // Act
      boolean result = card.isStaff();

      // Assert
      assertEquals(false, result);
    }
  }

  @Nested
  class isDifferentCard_tests {
    @Test
    void returns_true_for_different_cards() {
      // Arrange
      Card card1 = Card.builder().cardID("ID1").build();
      Card card2 = Card.builder().cardID("ID2").build();

      // Act
      boolean result = card1.isDifferentCard(card2);

      // Assert
      assertEquals(true, result);
    }

    @Test
    void returns_false_for_cards_with_same_id() {
      // Arrange
      Card card1 = Card.builder().cardID("ID").build();
      Card card2 = Card.builder().cardID("ID").build();

      // Act
      boolean result = card1.isDifferentCard(card2);

      // Assert
      assertEquals(false, result);
    }
  }

  @Nested
  class isUnassigned_tests {
    @Test
    void returns_true_for_unassigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID(null).build();

      // Act
      boolean result = card.isUnassigned();

      // Assert
      assertEquals(true, result);
    }

    @Test
    void returns_false_for_assigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID("FacilityID").build();

      // Act
      boolean result = card.isUnassigned();

      // Assert
      assertEquals(false, result);
    }
  }

  @Nested
  class assertStaffCard_tests {
    @Test
    void throws_exception_for_non_staff_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.SOLDIER).build();

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> card.assertStaffCard());
    }

    @Test
    void does_not_throw_exception_for_staff_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.STAFF).build();

      // Act & Assert
      card.assertStaffCard();
    }
  }

  @Nested
  class assertSoldierCard_tests {
    @Test
    void throws_exception_for_non_soldier_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.STAFF).build();

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> card.assertSoldierCard());
    }

    @Test
    void does_not_throw_exception_for_soldier_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").cardType(CardType.SOLDIER).build();

      // Act & Assert
      card.assertSoldierCard();
    }
  }

  @Nested
  class assertDifferentCard_tests {
    @Test
    void throws_exception_for_same_card() {
      // Arrange
      Card card1 = Card.builder().cardID("ID").build();
      Card card2 = Card.builder().cardID("ID").build();

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> card1.assertDifferentCard(card2));
    }

    @Test
    void does_not_throw_exception_for_different_cards() {
      // Arrange
      Card card1 = Card.builder().cardID("ID1").build();
      Card card2 = Card.builder().cardID("ID2").build();

      // Act & Assert
      card1.assertDifferentCard(card2);
    }
  }

  @Nested
  class assertUnassignedCard_tests {
    @Test
    void throws_exception_for_assigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID("FacilityID").build();

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> card.assertUnassigned());
    }

    @Test
    void does_not_throw_exception_for_unassigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID(null).build();

      // Act & Assert
      card.assertUnassigned();
    }
  }

  @Nested
  class assertAssignedCard_tests {
    @Test
    void throws_exception_for_unassigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID(null).build();

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> card.assertAssigned());
    }

    @Test
    void does_not_throw_exception_for_assigned_card() {
      // Arrange
      Card card = Card.builder().cardID("ID").secureFacilityID("FacilityID").build();

      // Act & Assert
      card.assertAssigned();
    }
  }
}
