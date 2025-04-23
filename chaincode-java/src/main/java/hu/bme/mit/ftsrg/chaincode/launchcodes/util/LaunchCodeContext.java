/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.util;

import hu.bme.mit.ftsrg.chaincode.launchcodes.services.LaunchCodesService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LaunchCodeContext extends Context {

  LaunchCodesRegistry registry;
  LaunchCodesService service;

  public LaunchCodeContext(ChaincodeStub stub) {
    super(stub); // already fails for null stub with NullPointerException

    this.registry = new LaunchCodesRegistry(stub);
    this.service = new LaunchCodesService(registry);
  }
}
