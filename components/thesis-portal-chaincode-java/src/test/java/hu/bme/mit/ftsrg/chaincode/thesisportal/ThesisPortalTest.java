/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

import org.assertj.core.api.BDDAssertions;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
final class ThesisPortalTest {

  private static final String TEST_ID = "asset1";
  private static final String TEST_COLOR = "blue";
  private static final int TEST_SIZE = 5;
  private static final String TEST_OWNER = "Tomoko";
  private static final int TEST_VALUE = 300;
  private static final String TEST_JSON =
      String.format(
          "{\"ID\": \"%s\", \"Color\": \"%s\", \"Size\": %d, \"Owner\": \"%s\", \"AppraisedValue\": %d}",
          TEST_ID, TEST_COLOR, TEST_SIZE, TEST_OWNER, TEST_VALUE);
  private static final DemoAsset TEST_ASSET =
      DemoAsset.builder()
          .ID(TEST_ID)
          .Color(TEST_COLOR)
          .Size(TEST_SIZE)
          .Owner(TEST_OWNER)
          .AppraisedValue(TEST_VALUE)
          .build();

  ThesisPortal contract;

  @Mock private Context ctx;

  @Mock private ChaincodeStub stub;

  @BeforeEach
  void setup() {
    contract = new ThesisPortal();
  }

  @Test
  void when_unknown_transaction_then_do_nothing() {
    assertThrows(ChaincodeException.class, () -> contract.unknownTransaction(ctx));

    BDDMockito.then(ctx).shouldHaveNoInteractions();
  }

  @Nested
  class given_empty_ledger {

    @BeforeEach
    void setup() {
      given(ctx.getStub()).willReturn(stub);
      given(stub.getStringState(TEST_ID)).willReturn("");
    }

    @Test
    void when_create_then_return_new_asset() {
      DemoAsset asset =
          contract.createAsset(ctx, TEST_ID, TEST_COLOR, TEST_SIZE, TEST_OWNER, TEST_VALUE);

      BDDMockito.then(stub).should().putStringState(eq(TEST_ID), anyString());
      BDDAssertions.then(asset).isEqualTo(TEST_ASSET);
    }

    @Test
    void when_read_then_throw() {
      assertThrows(ChaincodeException.class, () -> contract.readAsset(ctx, TEST_ID));
    }

    @Test
    void when_update_then_throw() {
      assertThrows(
          ChaincodeException.class,
          () -> contract.updateAsset(ctx, TEST_ID, TEST_COLOR, TEST_SIZE, TEST_OWNER, TEST_VALUE));

      BDDMockito.then(stub).should(never()).putStringState(eq(TEST_ID), anyString());
    }

    @Test
    void when_delete_then_throw() {
      assertThrows(ChaincodeException.class, () -> contract.deleteAsset(ctx, TEST_ID));

      BDDMockito.then(stub).should(never()).delState(TEST_ID);
    }
  }
}
