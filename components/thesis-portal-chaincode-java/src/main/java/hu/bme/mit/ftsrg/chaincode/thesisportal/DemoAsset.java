/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hyperledger.fabric.contract.annotation.Property;

@Value
@Builder(toBuilder = true)
@Accessors(fluent = true)
public class DemoAsset {

  @Property String ID;
  @Property String Color;
  @Property int Size;
  @Property String Owner;
  @Property int AppraisedValue;
}
