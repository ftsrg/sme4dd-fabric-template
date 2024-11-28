import { Identity, Signer, signers } from '@hyperledger/fabric-gateway';
import * as crypto from 'crypto';
import { promises as fs } from 'fs';
import * as path from 'path';
import { cryptoPath } from './utils';

async function getFirstDirFileName(dirPath: string): Promise<string> {
    const files = await fs.readdir(dirPath);
    return path.join(dirPath, files[0]);
}

function getKeyDirPath(orgDomain: string, user: string): string {
    return path.join(cryptoPath, orgDomain, 'users', user, 'msp', 'keystore');
}

function getCertDirPath(orgDomain: string, user: string): string { 
    return path.join(cryptoPath, orgDomain, 'users', user, 'msp', 'signcerts');
}

async function newIdentity(orgDomain: string, mspId: string, user: string): Promise<Identity> {
    const certPath = await getFirstDirFileName(getCertDirPath(orgDomain, user));
    const credentials = await fs.readFile(certPath);
    return { mspId, credentials };
}

async function newSigner(orgName: string, user: string): Promise<Signer> {
    const keyPath = await getFirstDirFileName(getKeyDirPath(orgName, user));
    const privateKeyPem = await fs.readFile(keyPath);
    const privateKey = crypto.createPrivateKey(privateKeyPem);
    return signers.newPrivateKeySigner(privateKey);
}

class FabricUserInfo {
    orgDomain: string;
    orgMspId: string;
    user: string;

    constructor(orgDomain: string, orgMspId: string, user: string) {
        this.orgDomain = orgDomain;
        this.orgMspId = orgMspId;
        this.user = user;
    }
}

class FabricUser {
    private fabricIdentity: Identity;
    private fabricSigner: Signer;

    private constructor(fabricIdentity: Identity, fabricSigner: Signer) { 
        this.fabricIdentity = fabricIdentity;
        this.fabricSigner = fabricSigner;
    }

    getFabricIdentity() : Identity {
        return this.fabricIdentity;
    }

    getFabricSigner() : Signer {
        return this.fabricSigner;
    }

    static async createFor(userInfo: FabricUserInfo): Promise<FabricUser> {
        const identity: Identity = await newIdentity(userInfo.orgDomain, userInfo.orgMspId, userInfo.user);
        const signer: Signer = await newSigner(userInfo.orgDomain, userInfo.user);

        return new FabricUser(identity, signer);
    }
}

export { FabricUser, FabricUserInfo };