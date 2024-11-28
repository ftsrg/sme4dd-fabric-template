import * as grpc from '@grpc/grpc-js';
import { promises as fs } from 'fs';
import * as path from 'path';
import { cryptoPath } from './utils';

class PeerConnectionInfo {
    orgDomain: string;
    peerName: string;
    peerAddress: string;

    constructor(orgDomain: string, peerName: string, peerAddress: string) {
        this.orgDomain = orgDomain;
        this.peerName = peerName;
        this.peerAddress = peerAddress;
    }
}

class PeerConnection {
    private grpcClient: grpc.Client;

    constructor(grpcClient: grpc.Client) {
        this.grpcClient = grpcClient;
    }

    getGrpcClientConnection(): grpc.Client {
        return this.grpcClient;
    }

    static async createFor(peerInfo: PeerConnectionInfo): Promise<PeerConnection> {
        const tlsCertPath = path.resolve(cryptoPath, peerInfo.orgDomain, 'peers', peerInfo.peerName, 'tls', 'ca.crt');
        const tlsRootCert = await fs.readFile(tlsCertPath);
        const tlsCredentials = grpc.credentials.createSsl(tlsRootCert);
        const grpcClient = new grpc.Client(peerInfo.peerAddress, tlsCredentials, {
            'grpc.ssl_target_name_override': peerInfo.peerName,
        });
        return new PeerConnection(grpcClient);
    }
}

export { PeerConnectionInfo, PeerConnection };