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

package bisq.chat.trade;

import bisq.chat.channel.Channel;
import bisq.chat.channel.ChannelSelectionStore;
import bisq.chat.message.ChatMessage;
import bisq.chat.trade.priv.PrivateTradeChannel;
import bisq.chat.trade.priv.PrivateTradeChannelService;
import bisq.chat.trade.pub.PublicTradeChannelService;
import bisq.common.currency.MarketRepository;
import bisq.common.observable.Observable;
import bisq.persistence.Persistence;
import bisq.persistence.PersistenceClient;
import bisq.persistence.PersistenceService;
import bisq.user.profile.UserProfile;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@Getter
public class TradeChannelSelectionService implements PersistenceClient<ChannelSelectionStore> {
    private final ChannelSelectionStore persistableStore = new ChannelSelectionStore();
    private final Persistence<ChannelSelectionStore> persistence;
    private final PrivateTradeChannelService privateTradeChannelService;
    private final PublicTradeChannelService publicTradeChannelService;
    private final Observable<Channel<? extends ChatMessage>> selectedChannel = new Observable<>();

    public TradeChannelSelectionService(PersistenceService persistenceService,
                                        PrivateTradeChannelService privateTradeChannelService,
                                        PublicTradeChannelService publicTradeChannelService) {
        this.privateTradeChannelService = privateTradeChannelService;
        this.publicTradeChannelService = publicTradeChannelService;
        persistence = persistenceService.getOrCreatePersistence(this, persistableStore);
    }

    public CompletableFuture<Boolean> initialize() {
        log.info("initialize");
        maybeSelectDefaultChannel();
        return CompletableFuture.completedFuture(true);
    }

    public CompletableFuture<Boolean> shutdown() {
        log.info("shutdown");
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void onPersistedApplied(ChannelSelectionStore persisted) {
        applySelectedChannel();
    }

    public void selectChannel(Channel<? extends ChatMessage> channel) {
        if (channel instanceof PrivateTradeChannel) {
            privateTradeChannelService.removeExpiredMessages((PrivateTradeChannel) channel);
        }

        persistableStore.setSelectedChannelId(channel != null ? channel.getId() : null);
        persist();

        applySelectedChannel();
    }

    private void applySelectedChannel() {
        Stream<Channel<?>> stream = Stream.concat(publicTradeChannelService.getChannels().stream(),
                privateTradeChannelService.getChannels().stream());
        selectedChannel.set(stream
                .filter(channel -> channel.getId().equals(persistableStore.getSelectedChannelId()))
                .findAny()
                .orElse(null));
    }

    public void reportUserProfile(UserProfile userProfile, String reason) {
        //todo report user to admin and moderators, add reason
        log.info("called reportChatUser {} {}", userProfile, reason);
    }

    private void maybeSelectDefaultChannel() {
        if (getSelectedChannel().get() == null) {
            publicTradeChannelService.getChannels().stream()
                    .filter(publicTradeChannel -> MarketRepository.getDefault().equals(publicTradeChannel.getMarket()))
                    .findAny()
                    .ifPresent(channel -> {
                        selectChannel(channel);
                        publicTradeChannelService.showChannel(channel);
                    });
        }
        persist();
    }
}