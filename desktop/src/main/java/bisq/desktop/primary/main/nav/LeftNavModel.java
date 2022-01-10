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

package bisq.desktop.primary.main.nav;

import bisq.application.DefaultServiceProvider;
import bisq.desktop.NavigationTarget;
import bisq.desktop.common.threading.UIThread;
import bisq.desktop.common.view.Controller;
import bisq.desktop.common.view.Model;
import bisq.desktop.common.view.View;
import bisq.network.NetworkService;
import bisq.network.p2p.message.Message;
import bisq.network.p2p.node.CloseReason;
import bisq.network.p2p.node.Connection;
import bisq.network.p2p.node.Node;
import bisq.network.p2p.node.transport.Transport;
import bisq.network.p2p.services.peergroup.PeerGroup;
import javafx.beans.property.*;
import javafx.scene.Parent;
import lombok.Getter;

@Getter
public class LeftNavModel implements Model {
    private final NetworkService networkService;
    @Getter
    protected final ObjectProperty<View<? extends Parent, ? extends Model, ? extends Controller>> view = new SimpleObjectProperty<>();

    @Getter
    protected NavigationTarget navigationTarget;

    private final StringProperty clearNetNumConnections = new SimpleStringProperty("0");
    private final StringProperty clearNetNumTargetConnections = new SimpleStringProperty("0");
    private final BooleanProperty clearNetIsVisible = new SimpleBooleanProperty(false);
    private final StringProperty torNumConnections = new SimpleStringProperty("0");
    private final StringProperty torNumTargetConnections = new SimpleStringProperty("0");
    private final BooleanProperty torIsVisible = new SimpleBooleanProperty(false);
    private final StringProperty i2pNumConnections = new SimpleStringProperty("0");
    private final StringProperty i2pNumTargetConnections = new SimpleStringProperty("0");
    private final BooleanProperty i2pIsVisible = new SimpleBooleanProperty(false);


    public LeftNavModel(DefaultServiceProvider serviceProvider) {
        networkService = serviceProvider.getNetworkService();

        clearNetIsVisible.set(networkService.isTransportTypeSupported(Transport.Type.CLEAR));
        torIsVisible.set(networkService.isTransportTypeSupported(Transport.Type.TOR));
        i2pIsVisible.set(networkService.isTransportTypeSupported(Transport.Type.I2P));

        networkService.getSupportedTransportTypes().forEach(type ->
                networkService.getServiceNodesByTransport().findServiceNode(type).ifPresent(serviceNode -> {
                    serviceNode.getPeerGroupService().ifPresent(peerGroupService -> {
                        PeerGroup peerGroup = peerGroupService.getPeerGroup();
                        switch (type) {
                            case TOR -> torNumTargetConnections.set(String.valueOf(peerGroup.getTargetNumConnectedPeers()));
                            case I2P -> i2pNumTargetConnections.set(String.valueOf(peerGroup.getTargetNumConnectedPeers()));
                            case CLEAR -> clearNetNumTargetConnections.set(String.valueOf(peerGroup.getTargetNumConnectedPeers()));
                        }

                        Node defaultNode = serviceNode.getDefaultNode();
                        defaultNode.addListener(new Node.Listener() {
                            @Override
                            public void onMessage(Message message, Connection connection, String nodeId) {
                            }

                            @Override
                            public void onConnection(Connection connection) {
                                onNumConnectionsChanged(type, peerGroup);
                            }

                            @Override
                            public void onDisconnect(Connection connection, CloseReason closeReason) {
                                onNumConnectionsChanged(type, peerGroup);
                            }
                        });
                    });
                })
        );
    }

    public void select(NavigationTarget navigationTarget, View<? extends Parent, ? extends Model, ? extends Controller> view) {
        this.navigationTarget = navigationTarget;
        this.view.set(view);
    }

    private void onNumConnectionsChanged(Transport.Type type, PeerGroup peerGroup) {
        UIThread.run(() -> {
            switch (type) {
                case TOR -> torNumConnections.set(String.valueOf(peerGroup.getNumConnections()));
                case I2P -> i2pNumConnections.set(String.valueOf(peerGroup.getNumConnections()));
                case CLEAR -> clearNetNumConnections.set(String.valueOf(peerGroup.getNumConnections()));
            }
        });
    }
}