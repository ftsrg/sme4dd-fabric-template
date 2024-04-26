/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.contract.annotation.Transaction.TYPE;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.tinylog.Logger;

@Contract(
    name = "thesis-portal",
    info =
        @Info(
            title = "Thesis Portal",
            description = "Simple chaincode for a thesis portal",
            version = "0.1.0",
            license = @License(name = "Apache-2.0"),
            contact =
                @Contact(
                    email = "john.doe@example.com",
                    name = "John Doe",
                    url = "http://example.com")))
@Default
public final class ThesisPortal implements ContractInterface {

  private static final Gson GSON = new Gson();

  @Transaction(name = "InitLedger")
  public void initLedger(Context ctx) {
    var assets =
        List.of(
            DemoAsset.builder()
                .ID("asset1")
                .Color("blue")
                .Size(5)
                .Owner("Tomoko")
                .AppraisedValue(300)
                .build(),
            DemoAsset.builder()
                .ID("asset2")
                .Color("red")
                .Size(5)
                .Owner("Brad")
                .AppraisedValue(400)
                .build(),
            DemoAsset.builder()
                .ID("asset3")
                .Color("green")
                .Size(10)
                .Owner("Jin Soo")
                .AppraisedValue(500)
                .build(),
            DemoAsset.builder()
                .ID("asset4")
                .Color("yellow")
                .Size(10)
                .Owner("Max")
                .AppraisedValue(600)
                .build(),
            DemoAsset.builder()
                .ID("asset5")
                .Color("black")
                .Size(15)
                .Owner("Adriana")
                .AppraisedValue(700)
                .build(),
            DemoAsset.builder()
                .ID("asset6")
                .Color("white")
                .Size(15)
                .Owner("Michel")
                .AppraisedValue(800)
                .build());

    for (var asset : assets) {
      ctx.getStub().putStringState(asset.ID(), serialize(asset));
      Logger.info("Asset {} initialized", asset.ID());
    }
  }

  @Transaction(name = "CreateAsset")
  public DemoAsset createAsset(
      Context ctx, String id, String color, int size, String owner, int appraisedValue) {
    assertNotExists(ctx, id);

    var asset =
        DemoAsset.builder()
            .ID(id)
            .Color(color)
            .Size(size)
            .Owner(owner)
            .AppraisedValue(appraisedValue)
            .build();
    ctx.getStub().putStringState(asset.ID(), serialize(asset));

    return asset;
  }

  @Transaction(name = "ReadAsset", intent = TYPE.EVALUATE)
  public DemoAsset readAsset(Context ctx, String id) {
    assertExists(ctx, id);

    return deserialize(ctx.getStub().getStringState(id));
  }

  @Transaction(name = "UpdateAsset")
  public DemoAsset updateAsset(
      Context ctx, String id, String color, int size, String owner, int appraisedValue) {
    assertExists(ctx, id);

    var updatedAsset =
        DemoAsset.builder()
            .ID(id)
            .Color(color)
            .Size(size)
            .Owner(owner)
            .AppraisedValue(appraisedValue)
            .build();
    ctx.getStub().putStringState(id, serialize(updatedAsset));

    return updatedAsset;
  }

  @Transaction(name = "DeleteAsset")
  public String deleteAsset(Context ctx, String id) {
    assertExists(ctx, id);

    ctx.getStub().delState(id);

    return id;
  }

  @Transaction(name = "TransferAsset")
  public String transferAsset(Context ctx, String id, String newOwner) {
    assertExists(ctx, id);

    DemoAsset asset = readAsset(ctx, id);
    final String oldOwner = asset.Owner();
    asset = asset.toBuilder().Owner(newOwner).build();

    ctx.getStub().putStringState(id, serialize(asset));
    return oldOwner;
  }

  @Transaction(name = "GetAllAssets", intent = TYPE.EVALUATE)
  public String getAllAssets(Context ctx) {
    var answer = new ArrayList<DemoAsset>();

    QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");
    for (KeyValue result : results) {
      DemoAsset asset = deserialize(result.getStringValue());
      Logger.info(asset);
      answer.add(asset);
    }

    return answer.toString();
  }

  @Transaction(name = "AssetExists", intent = TYPE.EVALUATE)
  public boolean assetExists(Context ctx, String id) {
    String assetJson = ctx.getStub().getStringState(id);
    return assetJson != null && !assetJson.isEmpty();
  }

  private void assertNotExists(Context ctx, String id) {
    if (assetExists(ctx, id)) {
      throw new ChaincodeException("The asset {} already exists", id);
    }
  }

  private void assertExists(Context ctx, String id) {
    if (!assetExists(ctx, id)) {
      throw new ChaincodeException("The asset {} does not exist", id);
    }
  }

  private String serialize(final DemoAsset asset) {
    return GSON.toJson(asset);
  }

  private DemoAsset deserialize(final String json) {
    return GSON.fromJson(json, DemoAsset.class);
  }
}
