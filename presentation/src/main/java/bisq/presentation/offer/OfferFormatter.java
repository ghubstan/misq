/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.presentation.offer;

import bisq.account.Transfer;
import bisq.common.monetary.Monetary;
import bisq.contract.AssetTransfer;
import bisq.contract.ProtocolType;
import bisq.offer.options.ReputationOption;
import bisq.offer.options.TransferOption;
import bisq.presentation.formatters.AmountFormatter;
import bisq.presentation.formatters.DateFormatter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
class OfferFormatter {
    static String formatAmountWithMinAmount(Monetary amount, Optional<Long> optionalMinAmount) {
        return AmountFormatter.formatMinAmount(optionalMinAmount, amount) +
                AmountFormatter.formatAmountWithCode(amount);
    }

    static String formatDate(long date) {
        return DateFormatter.formatDateTime(new Date(date));
    }

    static String formatProtocolTypes(List<? extends ProtocolType> protocolTypes) {
        return protocolTypes.toString();
    }

    static String formatReputationOptions(Optional<ReputationOption> reputationOptions) {
        return reputationOptions.toString();
    }

    static String formatTransferOptions(TransferOption transferOption) {
        return transferOption.bankName() + " / " + transferOption.countyCodeOfBank();
    }

    static String formatTransferTypes(List<Transfer> transfers) {
        return transfers.toString();
    }

    static String formatAssetTransferType(AssetTransfer.Type assetTransferType) {
        return assetTransferType.toString();
    }
}