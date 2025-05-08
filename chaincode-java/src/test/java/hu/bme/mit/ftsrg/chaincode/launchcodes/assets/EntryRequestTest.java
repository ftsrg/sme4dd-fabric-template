/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

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
final class EntryRequestTest {

  @Nested
  class constructor_tests {
    @Test
    void should_create_entry_request_using_constructor() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertEquals("secureFacilityID", entryRequest.secureFacilityID());
      assertEquals("requestTimestamp", entryRequest.requestTimestamp());
      assertEquals("requestBy", entryRequest.requestBy());
      assertEquals("authorizingSoldierOne", entryRequest.authorizingSoldierOne());
      assertEquals("authorizingSoldierTwo", entryRequest.authorizingSoldierTwo());
      assertEquals(EntryRequestStatus.PENDING, entryRequest.status());
    }

    @Test
    void should_create_entry_request_using_builder() {
      EntryRequest entryRequest =
          EntryRequest.builder()
              .secureFacilityID("secureFacilityID")
              .requestTimestamp("requestTimestamp")
              .requestBy("requestBy")
              .authorizingSoldierOne("authorizingSoldierOne")
              .authorizingSoldierTwo("authorizingSoldierTwo")
              .status(EntryRequestStatus.PENDING)
              .build();

      assertEquals("secureFacilityID", entryRequest.secureFacilityID());
      assertEquals("requestTimestamp", entryRequest.requestTimestamp());
      assertEquals("requestBy", entryRequest.requestBy());
      assertEquals("authorizingSoldierOne", entryRequest.authorizingSoldierOne());
      assertEquals("authorizingSoldierTwo", entryRequest.authorizingSoldierTwo());
      assertEquals(EntryRequestStatus.PENDING, entryRequest.status());
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_null() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new EntryRequest(
                  null,
                  "requestTimestamp",
                  "requestBy",
                  "authorizingSoldierOne",
                  "authorizingSoldierTwo",
                  EntryRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_secure_facility_id_is_empty() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new EntryRequest(
                  "",
                  "requestTimestamp",
                  "requestBy",
                  "authorizingSoldierOne",
                  "authorizingSoldierTwo",
                  EntryRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_null() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new EntryRequest(
                  "secureFacilityID",
                  null,
                  "requestBy",
                  "authorizingSoldierOne",
                  "authorizingSoldierTwo",
                  EntryRequestStatus.PENDING));
    }

    @Test
    void should_throw_exception_when_request_timestamp_is_empty() {
      assertThrows(
          ChaincodeException.class,
          () ->
              new EntryRequest(
                  "secureFacilityID",
                  "",
                  "requestBy",
                  "authorizingSoldierOne",
                  "authorizingSoldierTwo",
                  EntryRequestStatus.PENDING));
    }
  }

  @Nested
  class getTypeForCompositeKey_tests {
    @Test
    void should_return_type_for_composite_key() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertEquals(EntryRequest.class.getName(), entryRequest.getTypeForCompositeKey());
    }
  }

  @Nested
  class getAttributesForCompositeKey_tests {
    @Test
    void should_return_attributes_for_composite_key() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertArrayEquals(
          new String[] {"secureFacilityID", "requestTimestamp"},
          entryRequest.getAttributesForCompositeKey());
    }
  }

  @Nested
  class addAuthorizingSoldier_tests {
    @Test
    void should_add_first_authorizing_soldier() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              EntryRequestStatus.PENDING);

      entryRequest.addAuthorizingSoldier("authorizingSoldierOne");

      assertEquals("authorizingSoldierOne", entryRequest.authorizingSoldierOne());
    }

    @Test
    void should_add_second_authorizing_soldier() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              null,
              EntryRequestStatus.PENDING);

      entryRequest.addAuthorizingSoldier("authorizingSoldierTwo");

      assertEquals("authorizingSoldierTwo", entryRequest.authorizingSoldierTwo());
    }

    @Test
    void should_throw_exception_when_adding_third_authorizing_soldier() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertThrows(
          ChaincodeException.class,
          () -> entryRequest.addAuthorizingSoldier("thirdAuthorizingSoldier"));
    }
  }

  @Nested
  class isPending_tests {
    @Test
    void should_return_true_when_status_is_pending() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertEquals(true, entryRequest.isPending());
    }

    @Test
    void should_return_false_when_status_is_not_pending() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.ENTERED);

      assertEquals(false, entryRequest.isPending());
    }
  }

  @Nested
  class isCompleted_tests {
    @Test
    void should_return_true_when_status_is_completed() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.ENTERED);

      assertEquals(true, entryRequest.isCompleted());
    }

    @Test
    void should_return_false_when_status_is_not_completed() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertEquals(false, entryRequest.isCompleted());
    }
  }

  @Nested
  class isApproved_tests {
    @Test
    void should_return_true_when_status_is_approved() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.APPROVED);

      assertEquals(true, entryRequest.isApproved());
    }

    @Test
    void should_return_false_when_status_is_not_approved() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertEquals(false, entryRequest.isApproved());
    }
  }

  @Nested
  class isApprovedByTwoSoldiers_tests {
    @Test
    void should_return_true_when_approved_by_two_soldiers() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.APPROVED);

      assertEquals(true, entryRequest.isApprovedByTwoSoldiers());
    }

    @Test
    void should_return_false_when_approved_by_one_soldier() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              null,
              EntryRequestStatus.APPROVED);

      assertEquals(false, entryRequest.isApprovedByTwoSoldiers());
    }

    @Test
    void should_return_false_when_not_approved_by_two_soldiers() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              null,
              null,
              EntryRequestStatus.PENDING);

      assertEquals(false, entryRequest.isApprovedByTwoSoldiers());
    }
  }

  @Nested
  class assertPending_tests {
    @Test
    void should_not_throw_exception_when_pending() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      entryRequest.assertPending();
    }

    @Test
    void should_throw_exception_when_not_pending() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.APPROVED);

      assertThrows(ChaincodeException.class, () -> entryRequest.assertPending());
    }
  }

  @Nested
  class assertNotCompleted_tests {
    @Test
    void should_not_throw_exception_when_not_completed() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      entryRequest.assertNotCompleted();
    }

    @Test
    void should_throw_exception_when_completed() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.ENTERED);

      assertThrows(ChaincodeException.class, () -> entryRequest.assertNotCompleted());
    }
  }

  @Nested
  class assertApproved_tests {
    @Test
    void should_not_throw_exception_when_approved() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.APPROVED);

      entryRequest.assertApproved();
    }

    @Test
    void should_throw_exception_when_not_approved() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.PENDING);

      assertThrows(ChaincodeException.class, () -> entryRequest.assertApproved());
    }
  }

  @Nested
  class assertNotApprovedByTwoSoldiers_tests {
    @Test
    void should_not_throw_exception_when_not_approved_by_two_soldiers() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              null,
              EntryRequestStatus.APPROVED);

      entryRequest.assertNotApprovedByTwoSoldiers();
    }

    @Test
    void should_throw_exception_when_approved_by_two_soldiers() {
      EntryRequest entryRequest =
          new EntryRequest(
              "secureFacilityID",
              "requestTimestamp",
              "requestBy",
              "authorizingSoldierOne",
              "authorizingSoldierTwo",
              EntryRequestStatus.APPROVED);

      assertThrows(ChaincodeException.class, () -> entryRequest.assertNotApprovedByTwoSoldiers());
    }
  }
}
