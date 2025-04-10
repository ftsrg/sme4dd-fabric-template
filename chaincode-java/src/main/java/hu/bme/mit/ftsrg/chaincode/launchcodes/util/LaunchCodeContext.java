/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LaunchCodeContext extends Context {

  LaunchCodeRegistry registry;

  public LaunchCodeContext(ChaincodeStub stub) {
    super(stub);
    this.registry = new LaunchCodeRegistry(stub);
  }
}
