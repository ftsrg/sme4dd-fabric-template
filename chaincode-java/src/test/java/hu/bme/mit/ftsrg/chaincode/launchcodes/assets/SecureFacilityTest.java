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
final class SecureFacilityTest {

  @Nested
  class constructor_tests {
    @Test
    void should_create_secure_facility_using_constructor() {
      // Arrange & Act
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              "VisitorID",
              "OngoingEntryRequestTimestamp",
              "OngoingExitRequestTimestamp",
              "OngoingShiftChangeRequestTimestamp");

      // Assert
      assertEquals("ID", secureFacility.facilityID());
      assertEquals("Name", secureFacility.facilityName());
      assertEquals("SoldierOneID", secureFacility.soldierOneID());
      assertEquals("SoldierTwoID", secureFacility.soldierTwoID());
      assertEquals("VisitorID", secureFacility.visitorID());
      assertEquals("OngoingEntryRequestTimestamp", secureFacility.ongoingEntryRequestTimestamp());
      assertEquals("OngoingExitRequestTimestamp", secureFacility.ongoingExitRequestTimestamp());
      assertEquals(
          "OngoingShiftChangeRequestTimestamp",
          secureFacility.ongoingShiftChangeRequestTimestamp());
    }

    @Test
    void should_create_secure_facility_using_builder() {
      // Arrange & Act
      SecureFacility secureFacility =
          SecureFacility.builder()
              .facilityID("ID")
              .facilityName("Name")
              .soldierOneID("SoldierOneID")
              .soldierTwoID("SoldierTwoID")
              .visitorID("VisitorID")
              .ongoingEntryRequestTimestamp("OngoingEntryRequestTimestamp")
              .ongoingExitRequestTimestamp("OngoingExitRequestTimestamp")
              .ongoingShiftChangeRequestTimestamp("OngoingShiftChangeRequestTimestamp")
              .build();

      // Assert
      assertEquals("ID", secureFacility.facilityID());
      assertEquals("Name", secureFacility.facilityName());
      assertEquals("SoldierOneID", secureFacility.soldierOneID());
      assertEquals("SoldierTwoID", secureFacility.soldierTwoID());
      assertEquals("VisitorID", secureFacility.visitorID());
      assertEquals("OngoingEntryRequestTimestamp", secureFacility.ongoingEntryRequestTimestamp());
      assertEquals("OngoingExitRequestTimestamp", secureFacility.ongoingExitRequestTimestamp());
      assertEquals(
          "OngoingShiftChangeRequestTimestamp",
          secureFacility.ongoingShiftChangeRequestTimestamp());
    }

    @Test
    void should_throw_exception_when_id_is_null() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new SecureFacility(
                  null,
                  "Name",
                  "SoldierOneID",
                  "SoldierTwoID",
                  "VisitorID",
                  "OngoingEntryRequestTimestamp",
                  "OngoingExitRequestTimestamp",
                  "OngoingShiftChangeRequestTimestamp"));
    }

    @Test
    void should_throw_exception_when_id_is_empty() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new SecureFacility(
                  "",
                  "Name",
                  "SoldierOneID",
                  "SoldierTwoID",
                  "VisitorID",
                  "OngoingEntryRequestTimestamp",
                  "OngoingExitRequestTimestamp",
                  "OngoingShiftChangeRequestTimestamp"));
    }
  }

  @Nested
  class getTypeForCompositeKey_tests {
    @Test
    void should_return_type_for_composite_key() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              "VisitorID",
              "OngoingEntryRequestTimestamp",
              "OngoingExitRequestTimestamp",
              "OngoingShiftChangeRequestTimestamp");

      // Act
      String type = secureFacility.getTypeForCompositeKey();

      // Assert
      assertEquals(SecureFacility.class.getName(), type);
    }
  }

  @Nested
  class getAttributesForCompositeKey_tests {
    @Test
    void should_return_attributes_for_composite_key() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              "VisitorID",
              "OngoingEntryRequestTimestamp",
              "OngoingExitRequestTimestamp",
              "OngoingShiftChangeRequestTimestamp");

      // Act
      String[] attributes = secureFacility.getAttributesForCompositeKey();

      // Assert
      assertArrayEquals(new String[] {"ID"}, attributes);
    }
  }

  @Nested
  class isFree_tests {
    @Test
    void should_return_true_when_visitor_id_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              "OngoingEntryRequestTimestamp",
              "OngoingExitRequestTimestamp",
              "OngoingShiftChangeRequestTimestamp");

      // Act
      boolean isFree = secureFacility.isFree();

      // Assert
      assertEquals(true, isFree);
    }

    @Test
    void should_return_false_when_visitor_id_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              "VisitorID",
              "OngoingEntryRequestTimestamp",
              "OngoingExitRequestTimestamp",
              "OngoingShiftChangeRequestTimestamp");

      // Act
      boolean isFree = secureFacility.isFree();

      // Assert
      assertEquals(false, isFree);
    }
  }

  @Nested
  class hasOngoingEntryRequest_tests {
    @Test
    void should_return_true_when_ongoing_entry_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              "OngoingEntryRequestTimestamp",
              null,
              null);

      // Act
      boolean hasOngoingEntryRequest = secureFacility.hasOngoingEntryRequest();

      // Assert
      assertEquals(true, hasOngoingEntryRequest);
    }

    @Test
    void should_return_false_when_ongoing_entry_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act
      boolean hasOngoingEntryRequest = secureFacility.hasOngoingEntryRequest();

      // Assert
      assertEquals(false, hasOngoingEntryRequest);
    }
  }

  @Nested
  class hasOngoingExitRequest_tests {
    @Test
    void should_return_true_when_ongoing_exit_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              "OngoingExitRequestTimestamp",
              null);

      // Act
      boolean hasOngoingExitRequest = secureFacility.hasOngoingExitRequest();

      // Assert
      assertEquals(true, hasOngoingExitRequest);
    }

    @Test
    void should_return_false_when_ongoing_exit_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act
      boolean hasOngoingExitRequest = secureFacility.hasOngoingExitRequest();

      // Assert
      assertEquals(false, hasOngoingExitRequest);
    }
  }

  @Nested
  class hasOngoingShiftChangeRequest_tests {
    @Test
    void should_return_true_when_ongoing_shift_change_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              null,
              "OngoingShiftChangeRequestTimestamp");

      // Act
      boolean hasOngoingShiftChangeRequest = secureFacility.hasOngoingShiftChangeRequest();

      // Assert
      assertEquals(true, hasOngoingShiftChangeRequest);
    }

    @Test
    void should_return_false_when_ongoing_shift_change_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act
      boolean hasOngoingShiftChangeRequest = secureFacility.hasOngoingShiftChangeRequest();

      // Assert
      assertEquals(false, hasOngoingShiftChangeRequest);
    }
  }

  @Nested
  class assertFree_tests {
    @Test
    void should_not_throw_exception_when_visitor_id_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      secureFacility.assertFree();
    }

    @Test
    void should_throw_exception_when_visitor_id_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID", "Name", "SoldierOneID", "SoldierTwoID", "VisitorID", null, null, null);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> secureFacility.assertFree());
    }
  }

  @Nested
  class assertNoOngoingEntryRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_entry_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      secureFacility.assertNoOngoingEntryRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_entry_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              "OngoingEntryRequestTimestamp",
              null,
              null);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> secureFacility.assertNoOngoingEntryRequest());
    }
  }

  @Nested
  class assertOngoingEntryRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_entry_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              "OngoingEntryRequestTimestamp",
              null,
              null);

      // Act & Assert
      secureFacility.assertOngoingEntryRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_entry_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> secureFacility.assertOngoingEntryRequest());
    }
  }

  @Nested
  class assertOngoingExitRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_exit_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              "OngoingExitRequestTimestamp",
              null);

      // Act & Assert
      secureFacility.assertOngoingExitRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_exit_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> secureFacility.assertOngoingExitRequest());
    }
  }

  @Nested
  class assertNoOngoingExitRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_exit_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      secureFacility.assertNoOngoingExitRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_exit_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              "OngoingExitRequestTimestamp",
              null);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> secureFacility.assertNoOngoingExitRequest());
    }
  }

  @Nested
  class assertNoOngoingShiftChangeRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_shift_change_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      secureFacility.assertNoOngoingShiftChangeRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_shift_change_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              null,
              "OngoingShiftChangeRequestTimestamp");

      // Act & Assert
      assertThrows(
          ChaincodeException.class, () -> secureFacility.assertNoOngoingShiftChangeRequest());
    }
  }

  @Nested
  class assertOngoingShiftChangeRequest_tests {
    @Test
    void should_not_throw_exception_when_ongoing_shift_change_request_timestamp_is_not_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility(
              "ID",
              "Name",
              "SoldierOneID",
              "SoldierTwoID",
              null,
              null,
              null,
              "OngoingShiftChangeRequestTimestamp");

      // Act & Assert
      secureFacility.assertOngoingShiftChangeRequest();
    }

    @Test
    void should_throw_exception_when_ongoing_shift_change_request_timestamp_is_null() {
      // Arrange
      SecureFacility secureFacility =
          new SecureFacility("ID", "Name", "SoldierOneID", "SoldierTwoID", null, null, null, null);

      // Act & Assert
      assertThrows(
          ChaincodeException.class, () -> secureFacility.assertOngoingShiftChangeRequest());
    }
  }
}
