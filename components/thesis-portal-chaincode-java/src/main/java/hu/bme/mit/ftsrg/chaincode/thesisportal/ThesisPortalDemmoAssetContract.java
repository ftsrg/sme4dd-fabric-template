/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.thesisportal;

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
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.tinylog.Logger;

@Contract(
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
public final class ThesisPortalDemmoAssetContract implements ContractInterface {
  
  @Override
  public Context createContext(ChaincodeStub stub) {
      return new ThesisPortalContext(stub);
  }

  @Override
  public void beforeTransaction(Context ctx) {
    Logger.info("Transaction {} started with parameters {}", 
      ctx.getStub().getTxId().substring(0, 8), String.join(", " ,ctx.getStub().getStringArgs()));
  }

  @Override
  public void afterTransaction(Context ctx, Object result) {
    ThesisPortalContext context = (ThesisPortalContext) ctx;
    context.getDemoAssetRegistry().flushUpdates();

    Logger.info("Transaction {} finished", context.getStub().getTxId().substring(0, 8));
  }
  
  @Transaction(name = "InitLedger")
  public void initLedger(ThesisPortalContext ctx) {
    var reg = ctx.getDemoAssetRegistry();
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
      reg.createAsset(asset);
      Logger.info("Asset {} initialized", asset.ID());
    }
  }

  @Transaction(name = "CreateAsset")
  public String createAsset(
    ThesisPortalContext ctx, String id, String color, int size, String owner, int appraisedValue) {
    
    DemoAssetRegistry reg = ctx.getDemoAssetRegistry();
    reg.assertNotExists(id);

    DemoAsset asset =
        DemoAsset.builder()
            .ID(id)
            .Color(color)
            .Size(size)
            .Owner(owner)
            .AppraisedValue(appraisedValue)
            .build();
    
    return reg.serialize(reg.createAsset(asset));
  }

  @Transaction(name = "ReadAsset", intent = TYPE.EVALUATE)
  public String readAsset(ThesisPortalContext ctx, String id) {
    DemoAssetRegistry reg = ctx.getDemoAssetRegistry();
    return reg.serialize(reg.readAsset(id));
  }

  @Transaction(name = "UpdateAsset")
  public String updateAsset(
    ThesisPortalContext ctx, String id, String color, int size, String owner, int appraisedValue) {
    
    DemoAssetRegistry reg = ctx.getDemoAssetRegistry();

    DemoAsset updatedAsset =
        DemoAsset.builder()
            .ID(id)
            .Color(color)
            .Size(size)
            .Owner(owner)
            .AppraisedValue(appraisedValue)
            .build();

    return reg.serialize(reg.updateAsset(updatedAsset));
  }

  @Transaction(name = "DeleteAsset")
  public String deleteAsset(ThesisPortalContext ctx, String id) {
    return ctx.getDemoAssetRegistry().deleteAsset(id);
  }

  @Transaction(name = "TransferAsset")
  public String transferAsset(ThesisPortalContext ctx, String id, String newOwner) {
    DemoAssetRegistry reg = ctx.getDemoAssetRegistry();
    
    DemoAsset asset = reg.readAsset(id);
    final String oldOwner = asset.Owner();
    asset.Owner(newOwner);

    reg.updateAsset(asset);
    return oldOwner;
  }

  @Transaction(name = "GetAllAssets", intent = TYPE.EVALUATE)
  public String getAllAssets(ThesisPortalContext ctx) {
    DemoAssetRegistry reg = ctx.getDemoAssetRegistry();
    ArrayList<DemoAsset> answer = new ArrayList<DemoAsset>();

    QueryResultsIterator<KeyValue> results = ctx.getStub().getStateByRange("", "");
    for (KeyValue result : results) {
      DemoAsset asset = reg.deserialize(result.getStringValue());
      Logger.info(asset);
      answer.add(asset);
    }

    return reg.serialize(answer.toArray(new DemoAsset[0]));
  }

  @Transaction(name = "AssetExists", intent = TYPE.EVALUATE)
  public boolean assetExists(ThesisPortalContext ctx, String id) {
    return ctx.getDemoAssetRegistry().assetExists(id);
  }
}
