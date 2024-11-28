package hu.bme.mit.ftsrg.chaincode.thesisportal;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

public class ThesisPortalContext extends Context {
    private final DemoAssetRegistry demoAssetRegistry;

    public ThesisPortalContext(final ChaincodeStub stub) {
        super(stub);
        this.demoAssetRegistry = new DemoAssetRegistry(stub);
    }

    public DemoAssetRegistry getDemoAssetRegistry() {
        return demoAssetRegistry;
    }
}
