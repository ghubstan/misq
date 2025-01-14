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

package bisq.desktop.primary.main.content.trade.bisqEasy.chat;

import bisq.desktop.common.threading.UIThread;
import bisq.desktop.common.utils.Transitions;
import bisq.desktop.components.controls.Switch;
import bisq.desktop.primary.main.content.chat.BaseChatView;
import bisq.desktop.primary.main.content.trade.bisqEasy.chat.guide.TradeGuideView;
import bisq.i18n.Res;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BisqEasyChatView extends BaseChatView {
    private final BisqEasyChatController bisqEasyChatController;
    private final Switch toggleOffersButton;
    private final BisqEasyChatModel bisqEasyChatModel;
    private final Button createOfferButton;

    // Trade helpers
    private final Button completeTradeButton, openDisputeButton;
    private final VBox tradeHelpers, completeTradeButtonWrapper;
    private final Tooltip completeTradeTooltip;

    public BisqEasyChatView(BisqEasyChatModel model,
                            BisqEasyChatController controller,
                            Pane marketChannelSelection,
                            Pane privateChannelSelection,
                            Pane chatMessagesComponent,
                            Pane channelInfo) {
        super(model,
                controller,
                marketChannelSelection,
                privateChannelSelection,
                chatMessagesComponent,
                channelInfo);

        bisqEasyChatController = controller;
        bisqEasyChatModel = model;

        toggleOffersButton = new Switch();
        toggleOffersButton.setText(Res.get("bisqEasy.filter.offersOnly"));

        centerToolbar.getChildren().add(2, toggleOffersButton);

        createOfferButton = new Button();
        createOfferButton.setMaxWidth(Double.MAX_VALUE);
        createOfferButton.setMinHeight(37);
        createOfferButton.setDefaultButton(true);
        VBox.setMargin(createOfferButton, new Insets(-2, 25, 17, 25));
        left.getChildren().add(createOfferButton);

        completeTradeButton = new Button(Res.get("completeTrade"));
        completeTradeButtonWrapper = new VBox(completeTradeButton);
        openDisputeButton = new Button(Res.get("bisqEasy.openDispute"));
        decorateButton(completeTradeButton);
        decorateButton(openDisputeButton);
        tradeHelpers = new VBox(completeTradeButtonWrapper, openDisputeButton);
        left.getChildren().add(tradeHelpers);

        model.getView().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Region childRoot = newValue.getRoot();
                // chatMessagesComponent is VBox
                VBox.setMargin(childRoot, new Insets(25, 25, 25, 25));
                chatMessagesComponent.getChildren().add(0, childRoot);
                UIThread.runOnNextRenderFrame(() -> Transitions.transitContentViews(oldValue, newValue));
            } else if (oldValue instanceof TradeGuideView) {
                chatMessagesComponent.getChildren().remove(0);
            }
        });

        completeTradeTooltip = new Tooltip("");
        installTooltip();
    }

    private void decorateButton(Button button) {
        button.getStyleClass().add("default-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(37);
        VBox.setMargin(button, new Insets(-2, 25, 17, 25));
    }

    public void installTooltip() {
        Tooltip.install(completeTradeButtonWrapper, completeTradeTooltip);
        completeTradeButton.setTooltip(completeTradeTooltip);
        completeTradeButton.setOnAction(e -> bisqEasyChatController.onCompleteTrade());
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();

        createOfferButton.visibleProperty().bind(bisqEasyChatModel.getCreateOfferButtonVisible());
        createOfferButton.managedProperty().bind(bisqEasyChatModel.getCreateOfferButtonVisible());
        createOfferButton.textProperty().bind(bisqEasyChatModel.getActionButtonText());
        createOfferButton.setOnAction(e -> bisqEasyChatController.onCreateOfferButtonClicked());

        completeTradeButton.disableProperty().bind(bisqEasyChatModel.getCompleteTradeDisabled());
        openDisputeButton.disableProperty().bind(bisqEasyChatModel.getOpenDisputeDisabled());
        completeTradeButton.setOnAction(e -> bisqEasyChatController.onCompleteTrade());
        openDisputeButton.setOnAction(e -> bisqEasyChatController.onOpenMediation());
        tradeHelpers.visibleProperty().bind(bisqEasyChatModel.getTradeHelpersVisible());
        tradeHelpers.managedProperty().bind(bisqEasyChatModel.getTradeHelpersVisible());
        completeTradeTooltip.textProperty().bind(bisqEasyChatModel.getCompleteTradeTooltip());

        toggleOffersButton.selectedProperty().bindBidirectional(bisqEasyChatModel.getOfferOnly());
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();

        createOfferButton.visibleProperty().unbind();
        createOfferButton.managedProperty().unbind();
        createOfferButton.textProperty().unbind();
        createOfferButton.setOnAction(null);

        completeTradeButton.disableProperty().unbind();
        completeTradeButton.setOnAction(null);
        openDisputeButton.disableProperty().unbind();
        openDisputeButton.setOnAction(null);
        tradeHelpers.visibleProperty().unbind();
        tradeHelpers.managedProperty().unbind();
        completeTradeTooltip.textProperty().unbind();

        toggleOffersButton.selectedProperty().unbindBidirectional(bisqEasyChatModel.getOfferOnly());
    }
}
