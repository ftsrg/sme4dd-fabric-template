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
final class ExitRequestTest {

  @Nested
  class constructor_tests {

    @Test
    void should_create_an_exit_request_using_constructor() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertEquals("secureFacilityID", exitRequest.secureFacilityID());
      assertEquals("requestTimestamp", exitRequest.requestTimestamp());
      assertEquals("requestBy", exitRequest.requestBy());
      assertEquals(null, exitRequest.authorizingSoldierOne());
      assertEquals(null, exitRequest.authorizingSoldierTwo());
      assertEquals(ExitRequestStatus.PENDING, exitRequest.status());
    }

    @Test
    void should_create_an_exit_request_using_builder() {
      ExitRequest exitRequest =
          ExitRequest.builder()
              .secureFacilityID("secureFacilityID")
              .requestTimestamp("requestTimestamp")
              .requestBy("requestBy")
              .authorizingSoldierOne(null)
              .authorizingSoldierTwo(null)
              .status(ExitRequestStatus.PENDING)
              .build();

      assertEquals("secureFacilityID", exitRequest.secureFacilityID());
      assertEquals("requestTimestamp", exitRequest.requestTimestamp());
      assertEquals("requestBy", exitRequest.requestBy());
      assertEquals(null, exitRequest.authorizingSoldierOne());
      assertEquals(null, exitRequest.authorizingSoldierTwo());
      assertEquals(ExitRequestStatus.PENDING, exitRequest.status());
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_null() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new ExitRequest(
                  null, "requestTimestamp", "requestBy", null, null, ExitRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_empty() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new ExitRequest(
                  "", "requestTimestamp", "requestBy", null, null, ExitRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_null() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new ExitRequest(
                  "secureFacilityID", null, "requestBy", null, null, ExitRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_empty() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new ExitRequest(
                  "secureFacilityID", "", "requestBy", null, null, ExitRequestStatus.PENDING));
    }
  }

  @Nested
  class getTypeForCompositeKey_tests {

    @Test
    void should_return_the_type_for_composite_key() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertEquals(ExitRequest.class.getName(), exitRequest.getTypeForCompositeKey());
    }
  }

  @Nested
  class getAttributesForCompositeKey_tests {

    @Test
    void should_return_the_attributes_for_composite_key() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertArrayEquals(
          new String[] {"secureFacilityID", "requestTimestamp"},
          exitRequest.getAttributesForCompositeKey());
    }
  }

  @Nested
  class addAuthorizingSoldier_tests {

    @Test
    void should_add_first_authorizing_soldier() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      exitRequest.addAuthorizingSoldier("soldierID");

      assertEquals("soldierID", exitRequest.authorizingSoldierOne());
    }

    @Test
    void should_add_second_authorizing_soldier() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              null,
              ExitRequestStatus.PENDING);

      exitRequest.addAuthorizingSoldier("soldierID2");

      assertEquals("soldierID2", exitRequest.authorizingSoldierTwo());
    }

    @Test
    void should_throw_exception_when_adding_third_authorizing_soldier() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              "soldierID2",
              ExitRequestStatus.PENDING);

      assertThrows(ChaincodeException.class, () -> exitRequest.addAuthorizingSoldier("soldierID3"));
    }
  }

  @Nested
  class isPending_tests {

    @Test
    void should_return_true_when_status_is_pending() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertEquals(true, exitRequest.isPending());
    }

    @Test
    void should_return_false_when_status_is_not_pending() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      assertEquals(false, exitRequest.isPending());
    }
  }

  @Nested
  class isApproved_tests {

    @Test
    void should_return_true_when_status_is_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      assertEquals(true, exitRequest.isApproved());
    }

    @Test
    void should_return_false_when_status_is_not_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertEquals(false, exitRequest.isApproved());
    }
  }

  @Nested
  class isApprovedByTwoSoldiers_tests {

    @Test
    void should_return_true_when_both_soldiers_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              "soldierID2",
              ExitRequestStatus.APPROVED);

      assertEquals(true, exitRequest.isApprovedByTwoSoldiers());
    }

    @Test
    void should_return_false_when_one_soldier_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              null,
              ExitRequestStatus.APPROVED);

      assertEquals(false, exitRequest.isApprovedByTwoSoldiers());
    }

    @Test
    void should_return_false_when_no_soldiers_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      assertEquals(false, exitRequest.isApprovedByTwoSoldiers());
    }
  }

  @Nested
  class isCompleted_tests {

    @Test
    void should_return_true_when_status_is_completed() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.EXITED);

      assertEquals(true, exitRequest.isCompleted());
    }

    @Test
    void should_return_false_when_status_is_not_completed() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      assertEquals(false, exitRequest.isCompleted());
    }
  }

  @Nested
  class assertApproved_tests {

    @Test
    void should_not_throw_exception_when_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      exitRequest.assertApproved();
    }

    @Test
    void should_throw_exception_when_not_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      assertThrows(ChaincodeException.class, () -> exitRequest.assertApproved());
    }
  }

  @Nested
  class assertNotCompleted_tests {

    @Test
    void should_not_throw_exception_when_not_completed() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      exitRequest.assertNotCompleted();
    }

    @Test
    void should_throw_exception_when_completed() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.EXITED);

      assertThrows(ChaincodeException.class, () -> exitRequest.assertNotCompleted());
    }
  }

  @Nested
  class assertNotApprovedByTwoSoldiers_tests {

    @Test
    void should_not_throw_exception_when_not_approved_by_two_soldiers() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      exitRequest.assertNotApprovedByTwoSoldiers();
    }

    @Test
    void should_not_throw_exception_when_one_soldier_approved() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              null,
              ExitRequestStatus.APPROVED);

      exitRequest.assertNotApprovedByTwoSoldiers();
    }

    @Test
    void should_throw_exception_when_approved_by_two_soldiers() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "soldierID1",
              "soldierID2",
              ExitRequestStatus.APPROVED);

      assertThrows(ChaincodeException.class, () -> exitRequest.assertNotApprovedByTwoSoldiers());
    }
  }

  @Nested
  class assertPending_tests {

    @Test
    void should_not_throw_exception_when_pending() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.PENDING);

      exitRequest.assertPending();
    }

    @Test
    void should_throw_exception_when_not_pending() {
      ExitRequest exitRequest =
          new ExitRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              ExitRequestStatus.APPROVED);

      assertThrows(ChaincodeException.class, () -> exitRequest.assertPending());
    }
  }
}
