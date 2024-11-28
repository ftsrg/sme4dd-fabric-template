/* eslint-disable @typescript-eslint/no-unused-vars */
/*
 * SPDX-License-Identifier: Apache-2.0
 */

import * as grpc from '@grpc/grpc-js';
import { connect, Contract, Gateway } from '@hyperledger/fabric-gateway';
import { FabricUser, FabricUserInfo } from './fabricUser';
import { PeerConnection, PeerConnectionInfo } from './peerConnection';
import { ThesisPortalChaincode } from './thesisPortalChaincode';
import { envOrDefault } from './utils';

const channelName = envOrDefault('CHANNEL_NAME', 'thesis-portal-channel');
const chaincodeName = envOrDefault('CHAINCODE_NAME', 'thesis-portal-chaincode-java');

const UniAUser = new FabricUserInfo('unia.com', 'UniAMSP', 'User1@unia.com');
const UniBUser = new FabricUserInfo('unib.com', 'UniBMSP', 'User1@unib.com');
const UniGovUser = new FabricUserInfo('unigov.com', 'UniGovMSP', 'User1@unigov.com');

const UniAPeer = new PeerConnectionInfo('unia.com', 'peer0.unia.com', 'localhost:7041');
const UniBPeer = new PeerConnectionInfo('unib.com', 'peer0.unib.com', 'localhost:7061');
const UniGovPeer = new PeerConnectionInfo('unigov.com', 'peer0.unigov.com', 'localhost:7021');

async function main(): Promise<void> {

    await displayInputParameters();
    const connections = await connectToContract(UniAUser, UniAPeer);

    try {
        const cc = new ThesisPortalChaincode(connections.contract);

        console.log('*** Initializing ledger...');
        await cc.demoAssetContract.initLedger();
        const allAssets = await cc.demoAssetContract.getAllAssets();
        console.log('*** All assets: ', JSON.stringify(allAssets));
        
        console.log('*** Creating new asset...');
        const newAsset = await cc.demoAssetContract.createAsset({ ID: 'assetNew', Color: 'blue', Size: 5, Owner: 'Tomoko', AppraisedValue: 300 });
        console.log('*** New asset: ', JSON.stringify(newAsset));
        
        console.log('*** Transferring new asset...');
        const oldOwner = await cc.demoAssetContract.transferAsset(newAsset.ID, 'Saptha');
        console.log('*** Transferred from old owner: ', oldOwner);
        
        console.log('*** Reading new asset...');
        const readAsset = await cc.demoAssetContract.readAssetByID(newAsset.ID);
        console.log('*** Read asset: ', JSON.stringify(readAsset));
        
        const updatedAsset = await cc.demoAssetContract.updateAsset({ ID: 'asset70' });
        console.log('*** Updated asset: ', JSON.stringify(updatedAsset));
    } catch (error) {
        console.error('******** ERROR:', error);
        console.error('******** ERROR JSON: ', JSON.stringify(error));
    } finally {
        connections.closeConnections();    
    }
}

main().catch(error => {
    console.error('******** FAILED to run the application:', error);
    process.exitCode = 1;
});

async function displayInputParameters(): Promise<void> {
    console.log(`dirname:       ${__dirname}`);
    console.log(`channelName:       ${channelName}`);
    console.log(`chaincodeName:     ${chaincodeName}`);
}

class Connections {
    private gateway: Gateway;
    public contract: Contract;
    private client: grpc.Client;

    constructor(gateway: Gateway, contract: Contract, client: grpc.Client) {
        this.gateway = gateway;
        this.contract = contract;
        this.client = client;
    }

    public closeConnections() {
        this.gateway.close();
        this.client.close();
    }
}

async function connectToContract(user: FabricUserInfo, peer: PeerConnectionInfo): Promise<Connections> {
    const peerConnection = await PeerConnection.createFor(peer);
    const fabricUser = await FabricUser.createFor(user);

    const gateway = connect({
        client: peerConnection.getGrpcClientConnection(),
        identity: fabricUser.getFabricIdentity(),
        signer: fabricUser.getFabricSigner(),
        evaluateOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        endorseOptions: () => {
            return { deadline: Date.now() + 15000 }; // 15 seconds
        },
        submitOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        commitStatusOptions: () => {
            return { deadline: Date.now() + 60000 }; // 1 minute
        }
    });

    const network = gateway.getNetwork(channelName);
    const contract = network.getContract(chaincodeName);

    return new Connections(gateway, contract, peerConnection.getGrpcClientConnection());
}