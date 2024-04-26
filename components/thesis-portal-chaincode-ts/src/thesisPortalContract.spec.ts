import chai, { expect } from 'chai';
import chaiAsPromised from 'chai-as-promised';
import sinon, { SinonStubbedInstance } from 'sinon';

import { Context } from 'fabric-contract-api';
import { ChaincodeStub, ClientIdentity,  } from 'fabric-shim';
import stringify from 'json-stringify-deterministic';
import sortKeysRecursive from 'sort-keys-recursive';

import { ThesisPortalContract } from './thesisPortalContract';

chai.use(chaiAsPromised);

class ContextMock implements Context {
    stub: SinonStubbedInstance<ChaincodeStub>;
    clientIdentity: SinonStubbedInstance<ClientIdentity>;
    logging: {
        setLevel: (level: string) => void;
        getLogger: (name?: string) => any;
    };

    constructor() {
        this.stub = sinon.createStubInstance(ChaincodeStub);
        this.clientIdentity = sinon.createStubInstance(ClientIdentity);
        this.logging = {
            setLevel: sinon.stub(),
            getLogger: sinon.stub(),
        };
    }
}

describe('ThesisPortalContract', () => {
    let contract: ThesisPortalContract;
    let ctxMock: ContextMock;
    
    beforeEach(() => {
        contract = new ThesisPortalContract();
        ctxMock = new ContextMock();
    });

    describe('CreateAsset', () => {
        it('should create a new asset', async () => {
            const newAsset = {
                ID: 'asset1',
                Color: 'red',
                Size: 10,
                Owner: 'John',
                AppraisedValue: 1000,
            };
    
          await contract.CreateAsset(ctxMock, newAsset.ID, newAsset.Color, newAsset.Size, newAsset.Owner, newAsset.AppraisedValue);
    
          expect(ctxMock.stub.putState.calledOnceWithExactly(newAsset.ID, Buffer.from(stringify(sortKeysRecursive(newAsset))))).to.be.true;
        });

        it('should throw an error if the asset already exists', async () => {
            const newAsset = {
                ID: 'asset1',
                Color: 'red',
                Size: 10,
                Owner: 'John',
                AppraisedValue: 1000,
            };

            // Mock the AssetExists method to return true
            //contract.AssetExists = sinon.stub().resolves(true);
            ctxMock.stub.getState.callsFake(async (id: string) => Promise.resolve(Buffer.from(stringify(sortKeysRecursive(newAsset)))));
            const fail = () => contract.CreateAsset(ctxMock, newAsset.ID, newAsset.Color, newAsset.Size, newAsset.Owner, newAsset.AppraisedValue);

           await expect(fail()).to.eventually.be.rejectedWith('The asset asset1 already exists');
            
        });
      });
});