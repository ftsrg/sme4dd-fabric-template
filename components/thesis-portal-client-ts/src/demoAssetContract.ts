import { Contract } from '@hyperledger/fabric-gateway';
import { TextDecoder } from 'util';

const utf8Decoder = new TextDecoder();

class DemoAsset {
    ID!: string;
    Color?: string;
    Size?: number;
    Owner?: string;
    AppraisedValue?: number;
}

class DemoAssetContract {
    private contract: Contract;
    private contractName: string = 'ThesisPortalDemmoAssetContract';

    constructor(contract: Contract) { 
        this.contract = contract;
    }

    private deconstructAsset(asset: DemoAsset): Array<string> {
        return [
            asset.ID,
            asset.Color || '',
            (asset.Size ?? 0).toString(),
            asset.Owner || '',
            (asset.AppraisedValue ?? 0).toString()
        ];
    }

    private txName(name: string): string {
        return `${this.contractName}:${name}`;
    }

    public async initLedger(): Promise<void> {    
        await this.contract.submitTransaction(this.txName('InitLedger'));
    }
    
    public async getAllAssets(): Promise<DemoAsset[]> {    
        const resultBytes = await this.contract.evaluateTransaction(this.txName('GetAllAssets'));
        const resultJson = utf8Decoder.decode(resultBytes);
        return JSON.parse(resultJson);
    }
    
    public async createAsset(asset: DemoAsset): Promise<DemoAsset> {
        const resultBytes = await this.contract.submitTransaction(this.txName('CreateAsset'), ...this.deconstructAsset(asset));
        const resultJson = utf8Decoder.decode(resultBytes);
        return JSON.parse(resultJson);
    }
    
    public async updateAsset(asset: DemoAsset): Promise<DemoAsset>{    
        const resultBytes = await this.contract.submitTransaction(this.txName('UpdateAsset'), ...this.deconstructAsset(asset));
        const resultJson = utf8Decoder.decode(resultBytes);
        return JSON.parse(resultJson);
    }

    public async transferAsset(assetId: string, newOwner: string): Promise<string> {    
        const resultBytes = await this.contract.submitTransaction(this.txName('TransferAsset'), assetId, newOwner);
        return utf8Decoder.decode(resultBytes);
    }
    
    public async readAssetByID(assetId: string): Promise<DemoAsset> {    
        const resultBytes = await this.contract.evaluateTransaction(this.txName('ReadAsset'), assetId);
        const resultJson = utf8Decoder.decode(resultBytes);
        return JSON.parse(resultJson);
    }
}

export { DemoAssetContract, DemoAsset };