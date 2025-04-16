/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
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
}
