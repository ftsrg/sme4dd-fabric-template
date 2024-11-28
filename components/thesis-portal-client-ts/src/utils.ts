import * as path from 'path';

function envOrDefault(key: string, defaultValue: string): string {
    return process.env[key] || defaultValue;
}

const cryptoPath = envOrDefault('CRYPTO_PATH', path.resolve(__dirname, '..', '..', '..', 'fablo-target', 'fabric-config', 'crypto-config', 'peerOrganizations'));

export { envOrDefault, cryptoPath };