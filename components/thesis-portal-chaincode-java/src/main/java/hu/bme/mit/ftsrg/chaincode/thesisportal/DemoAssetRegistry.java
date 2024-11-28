package hu.bme.mit.ftsrg.chaincode.thesisportal;

import java.util.HashMap;
import java.util.Map;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.google.gson.Gson;

public class DemoAssetRegistry {
    private static final Gson GSON = new Gson();

    private Map<String, DemoAsset> readCache = new HashMap<>();
    private Map<String, DemoAsset> writeCache = new HashMap<>();

    private ChaincodeStub stub;

    public DemoAssetRegistry(ChaincodeStub stub) {
        this.stub = stub;
    }

    private DemoAsset readFromLedger(String id) {
        String key = DemoAssetKey.from(id).toString();

        if (writeCache.containsKey(key)) {
            return writeCache.get(key);
        }

        if (readCache.containsKey(key)) {
            return readCache.get(key);
        }

        String assetJson = stub.getStringState(key);
        DemoAsset asset = null;

        if (assetJson != null && !assetJson.isEmpty()) {
            asset = deserialize(assetJson);
        }

        readCache.put(key, asset);
        return asset;
    }

    private void writeToLedger(DemoAsset asset) {
        String key = DemoAssetKey.from(asset).toString();
        writeCache.put(key, asset);
    }

    private void deleteFromLedger(DemoAsset asset) {
        String key = DemoAssetKey.from(asset).toString();
        writeCache.put(key, null);
    }

    public void flushUpdates() {
        for (Map.Entry<String, DemoAsset> entry : writeCache.entrySet()) {
            String key = entry.getKey();
            DemoAsset asset = entry.getValue();

            if (asset == null) {
                stub.delState(key);
            } else {
                stub.putStringState(key, serialize(asset));
            }
        }
    }

    public DemoAsset createAsset(DemoAsset asset) {
        assertNotExists(asset.ID);
        writeToLedger(asset);
        return asset;
    }

    public DemoAsset readAsset(String id) {
        assertExists(id);
        return readFromLedger(id);
    }

    public DemoAsset updateAsset(DemoAsset updatedAsset) {
        assertExists(updatedAsset.ID);
        writeToLedger(updatedAsset);
        return updatedAsset;
    }

    public String deleteAsset(String id) {
        DemoAsset asset = readAsset(id);
        deleteFromLedger(asset);
        return id;
    }

    public boolean assetExists(String id) {
        return readFromLedger(id) != null;
    }

    public void assertNotExists(String id) {
        if (assetExists(id)) {
            throw new ChaincodeException(String.format("The asset %s already exists", id));
        }
    }

    public void assertExists(String id) {
        if (!assetExists(id)) {
            throw new ChaincodeException(String.format("The asset %s does not exist", id));
        }
    }

    public String serialize(final DemoAsset asset) {
        return GSON.toJson(asset);
    }

    public String serialize(final DemoAsset[] assetList) {
        return GSON.toJson(assetList);
    }

    public DemoAsset deserialize(final String json) {
        return GSON.fromJson(json, DemoAsset.class);
    }
}
