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

package bisq.desktop.primary.main.content.events;

import bisq.application.DefaultApplicationService;
import bisq.chat.channel.ChannelDomain;
import bisq.chat.channel.ChannelSelectionService;
import bisq.chat.channel.PublicChatChannelService;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.NavigationTarget;
import bisq.desktop.primary.main.content.chat.ChatController;
import bisq.desktop.primary.main.content.chat.channels.PublicChannelSelection;
import bisq.desktop.primary.main.content.chat.channels.PublicEventsChannelSelection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventsController extends ChatController<EventsView, EventsModel> implements Controller {
    public EventsController(DefaultApplicationService applicationService) {
        super(applicationService, ChannelDomain.EVENTS, NavigationTarget.NONE);
    }

    @Override
    public ChannelSelectionService getChannelSelectionService() {
        return chatService.getEventsChannelSelectionService();
    }

    @Override
    public PublicChatChannelService getPublicChannelService() {
        return chatService.getPublicEventsChannelService();
    }

    @Override
    public PublicChannelSelection getPublicChannelSelection() {
        return new PublicEventsChannelSelection(applicationService);
    }


    @Override
    public EventsModel getChatModel(ChannelDomain channelDomain) {
        return new EventsModel(channelDomain);
    }

    @Override
    public EventsView getChatView() {
        return new EventsView(model,
                this,
                publicChannelSelection.getRoot(),
                privateChannelSelection.getRoot(),
                chatMessagesComponent.getRoot(),
                channelSidebar.getRoot());
    }
}
