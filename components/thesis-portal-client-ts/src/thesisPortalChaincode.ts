import { Contract } from '@hyperledger/fabric-gateway';
import { DemoAssetContract } from './demoAssetContract';

class ThesisPortalChaincode {
    constructor(contract: Contract) { 
        this.demoAssetContract = new DemoAssetContract(contract);
    }

    public demoAssetContract: DemoAssetContract;
}

export { ThesisPortalChaincode };