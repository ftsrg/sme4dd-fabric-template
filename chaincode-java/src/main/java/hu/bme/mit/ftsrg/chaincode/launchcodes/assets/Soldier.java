/* SPDX-License-Identifier: Apache-2.0 */
package hu.bme.mit.ftsrg.chaincode.launchcodes.assets;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
/// This class represents a soldier's card.
public class Soldier {
  String cardID; // ID of the card
  String cardHolderName; // Name of the card holder
  String secureFacilityID; // ID of the secure facility the guard is assigned to, if any, otherwise null
}
