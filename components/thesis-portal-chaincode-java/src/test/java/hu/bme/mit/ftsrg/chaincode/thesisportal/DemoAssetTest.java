/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DemoAssetTest {
  @Nested
  final class given_asset {

    DemoAsset asset;

    @BeforeEach
    void setup() {
      asset =
          DemoAsset.builder()
              .ID("asset1")
              .Color("blue")
              .Size(20)
              .Owner("Guy")
              .AppraisedValue(100)
              .build();
    }

    @Test
    void then_is_reflexive() {
      then(asset).isEqualTo(asset);
    }

    @Test
    void then_is_symmetric() {
      var asset2 =
          DemoAsset.builder()
              .ID("asset1")
              .Color("blue")
              .Size(20)
              .Owner("Guy")
              .AppraisedValue(100)
              .build();

      then(asset).isEqualTo(asset2);
      then(asset2).isEqualTo(asset);
    }

    @Test
    void then_is_transitive() {
      var asset2 =
          DemoAsset.builder()
              .ID("asset1")
              .Color("blue")
              .Size(20)
              .Owner("Guy")
              .AppraisedValue(100)
              .build();
      var asset3 =
          DemoAsset.builder()
              .ID("asset1")
              .Color("blue")
              .Size(20)
              .Owner("Guy")
              .AppraisedValue(100)
              .build();

      then(asset).isEqualTo(asset2);
      then(asset2).isEqualTo(asset3);
      then(asset3).isEqualTo(asset);
    }

    @Test
    void then_handles_inequality() {
      var asset2 =
          DemoAsset.builder()
              .ID("asset2")
              .Color("red")
              .Size(40)
              .Owner("Lady")
              .AppraisedValue(200)
              .build();

      then(asset).isNotEqualTo(asset2);
    }

    @Test
    void then_handles_other_objects() {
      then(asset).isNotEqualTo("random string");
    }

    @Test
    void then_handles_null() {
      then(asset).isNotEqualTo(null);
    }

    @Test
    void then_toString_identifies_asset() {
      then(asset.toString())
          .isEqualTo("DemoAsset(ID=asset1, Color=blue, Size=20, Owner=Guy, AppraisedValue=100)");
    }
  }
}
