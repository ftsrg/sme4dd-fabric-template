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
final class ShiftChangeRequestTest {

  @Nested
  class constructor_tests {
    @Test
    void should_create_shift_change_request_using_constructor() {
      // Arrange & Act
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.PENDING);

      // Assert
      assertEquals("SecureFacilityID", shiftChangeRequest.secureFacilityID());
      assertEquals("RequestTimestamp", shiftChangeRequest.requestTimestamp());
      assertEquals("NewSoldiersID", shiftChangeRequest.newSoldiersID());
      assertEquals("OldSoldiersID", shiftChangeRequest.oldSoldiersID());
      assertEquals(ShiftChangeRequestStatus.PENDING, shiftChangeRequest.status());
    }

    @Test
    void should_create_shift_change_request_using_builder() {
      // Arrange & Act
      ShiftChangeRequest shiftChangeRequest =
          ShiftChangeRequest.builder()
              .secureFacilityID("SecureFacilityID")
              .requestTimestamp("RequestTimestamp")
              .newSoldiersID("NewSoldiersID")
              .oldSoldiersID("OldSoldiersID")
              .status(ShiftChangeRequestStatus.PENDING)
              .build();

      // Assert
      assertEquals("SecureFacilityID", shiftChangeRequest.secureFacilityID());
      assertEquals("RequestTimestamp", shiftChangeRequest.requestTimestamp());
      assertEquals("NewSoldiersID", shiftChangeRequest.newSoldiersID());
      assertEquals("OldSoldiersID", shiftChangeRequest.oldSoldiersID());
      assertEquals(ShiftChangeRequestStatus.PENDING, shiftChangeRequest.status());
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_null() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new ShiftChangeRequest(
                  null,
                  "RequestTimestamp",
                  "NewSoldiersID",
                  "OldSoldiersID",
                  ShiftChangeRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_empty() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new ShiftChangeRequest(
                  "",
                  "RequestTimestamp",
                  "NewSoldiersID",
                  "OldSoldiersID",
                  ShiftChangeRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_null() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new ShiftChangeRequest(
                  "SecureFacilityID",
                  null,
                  "NewSoldiersID",
                  "OldSoldiersID",
                  ShiftChangeRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_empty() {
      // Act & Assert
      assertThrows(
          ChaincodeException.class,
          () ->
              new ShiftChangeRequest(
                  "SecureFacilityID",
                  "",
                  "NewSoldiersID",
                  "OldSoldiersID",
                  ShiftChangeRequestStatus.PENDING));
    }
  }

  @Nested
  class getTypeForCompositeKey_tests {
    @Test
    void should_return_type_for_composite_key() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.PENDING);

      // Act
      String type = shiftChangeRequest.getTypeForCompositeKey();

      // Assert
      assertEquals(ShiftChangeRequest.class.getName(), type);
    }
  }

  @Nested
  class getAttributesForCompositeKey_tests {
    @Test
    void should_return_attributes_for_composite_key() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.PENDING);

      // Act
      String[] attributes = shiftChangeRequest.getAttributesForCompositeKey();

      // Assert
      assertArrayEquals(new String[] {"SecureFacilityID", "RequestTimestamp"}, attributes);
    }
  }

  @Nested
  class isPending_tests {
    @Test
    void should_return_true_when_status_is_pending() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.PENDING);

      // Act
      boolean isPending = shiftChangeRequest.isPending();

      // Assert
      assertEquals(true, isPending);
    }

    @Test
    void should_return_false_when_status_is_not_pending() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.APPROVED);

      // Act
      boolean isPending = shiftChangeRequest.isPending();

      // Assert
      assertEquals(false, isPending);
    }
  }

  @Nested
  class assertPending_tests {
    @Test
    void should_not_throw_exception_when_status_is_pending() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.PENDING);

      // Act & Assert
      shiftChangeRequest.assertPending();
    }

    @Test
    void should_throw_exception_when_status_is_not_pending() {
      // Arrange
      ShiftChangeRequest shiftChangeRequest =
          new ShiftChangeRequest(
              "SecureFacilityID",
              "RequestTimestamp",
              "NewSoldiersID",
              "OldSoldiersID",
              ShiftChangeRequestStatus.APPROVED);

      // Act & Assert
      assertThrows(ChaincodeException.class, () -> shiftChangeRequest.assertPending());
    }
  }
}
