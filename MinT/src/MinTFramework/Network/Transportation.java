/*
 * Copyright (C) 2015 Software&System Lab. Kangwon National University.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package MinTFramework.Network;

import MinTFramework.Network.MessageProtocol.coap.CoAPPacket;
import MinTFramework.MinT;
import MinTFramework.Network.Resource.ReceiveMessage;
import MinTFramework.Network.Resource.Request;
import MinTFramework.Network.Resource.ResponseData;
import MinTFramework.Network.sharing.Sharing;
import MinTFramework.Network.sharing.routingprotocol.RoutingProtocol;
import MinTFramework.SystemScheduler.SystemScheduler;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Transportation implements NetworkLayers {

    private MinT frame;
    private NetworkManager networkManager;
    private SystemHandler syshandle = null;
    private RoutingProtocol routing = null;
    private Sharing sharing = null;
    private SystemScheduler scheduler;
    private MatcherAndSerialization serialization = null;
    
    DebugLog dl = new DebugLog("Transportation");
//    private Performance bench_send = null;

    public Transportation(NetworkLayers.LAYER_DIRECTION layerDirection) {
        frame = MinT.getInstance();
        this.networkManager = frame.getNetworkManager();
        this.scheduler = frame.getSystemScheduler();
        
        if (layerDirection == NetworkLayers.LAYER_DIRECTION.RECEIVE) {
            syshandle = new SystemHandler();
            routing = networkManager.getRoutingProtocol();
            sharing = networkManager.getSharing();
        }

        if (layerDirection == NetworkLayers.LAYER_DIRECTION.SEND) {
            serialization = new MatcherAndSerialization(layerDirection);
//            if(frame.isBenchMode()){
//                bench_send = new PacketPerform("Trans-sender");
//                frame.addPerformance(MinT.PERFORM_METHOD.Trans_Sender, bench_send);
//            }
        }
    }

    @Override
    public void Receive(RecvMSG recvMsg) {
        CoAPPacket packet = recvMsg.getPacketDatagram();
//        System.out.print("Catched (recvMSG) by Transportation, " + packet.getSource().getProfile()+", "+packet.getMSGID());
//            System.out.println(", sender IP : "+packet.getSource().getAddress());
        if (recvMsg.isUDPMulticast() || isFinalDestination(packet.getDestinationNode())) {
            ReceiveMessage receivemsg = new ReceiveMessage(packet.getMsgData(), packet.getSource(), recvMsg);
//            System.out.println("PayLoad: "+packet.getMsgData());
            if (isRouting(receivemsg)) {
                routing.routingHandle(packet, receivemsg);
            } else if (isSharing(receivemsg)) {
                sharing.sharingHandle(packet, receivemsg);
            } else {
                syshandle.startHandle(packet, receivemsg);
            }
            
            if(packet.getHeader_Type().isACK()){
                networkManager.checkAck(packet.getMSGID());
            }
            
            //Run Response Handler for Response Mode
            if (packet.getHeader_Code().isResponse()) {
                //ResponseHandler reshandle = networkManager.getResponseDataMatchbyID(packet.getMSGID());
                ResponseHandler reshandle = networkManager.getResponseDataMatchbyID(packet.getToken());
                ResponseData resdata = new ResponseData(packet, receivemsg.getResourceData().getResource());
                if (reshandle != null) {
                    reshandle.Response(resdata);
                }
            }
        } else {
            stopOver(packet);
        }
    }

    private boolean isRouting(ReceiveMessage req) {
        if (req.getResourcebyName(Request.MSG_ATTR.Routing) != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSharing(ReceiveMessage req) {
        if (req.getResourcebyName(Request.MSG_ATTR.Sharing) != null) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFinalDestination(NetworkProfile destinationNode) {
        for (Network cn : this.networkManager.getNetworks().values()) {
            if (cn.getProfile().equals(destinationNode)) {
                return true;
            }
        }
        return false;//currnetProfile.equals(destinationNode);
    }

//    /**
//     * is Multicast Packet with CoAP or UDP
//     *
//     * @param destinationNode
//     * @return
//     */
//    private boolean isMulticast(NetworkProfile destinationNode) {
//        if (destinationNode.getAddress().equals("")) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    /**
     * Stop Over Method
     *
     * @param packet
     */
    private void stopOver(CoAPPacket packet) {

    }

    @Override
    public CoAPPacket EndPointSend(SendMSG sendmsg) {
        //Find Final Destination from Routing
        sendmsg.setFinalDestination(getFinalDestination(sendmsg.getDestination()));
        sendmsg.setNextNode(getNextNode(sendmsg.getDestination()));
        CoAPPacket npacket = null;
        if (sendmsg.isResponse()) {
        } else if (sendmsg.isRequest()) {
        } else if (sendmsg.isRequestGET()) {
            //check resend information
            if (sendmsg.getSendHit() == 0) {
                sendmsg.setResKey(networkManager.getIDMaker().makeToken());
                networkManager.putResponse(sendmsg.getResponseKey(), sendmsg);
            }            
        }

        npacket = serialization.EndPointSend(sendmsg);
        
        /**
         * Message Retransmit control
         */
        if (sendmsg.getHeader_Type().isCON()){
            if(sendmsg.getSendHit() == 0){
                networkManager.putCONMessage(sendmsg.getMessageID(), sendmsg);
            }
            //Start retransmit procedure
            networkManager.getCoAPRetransmit().activeRetransmission(sendmsg);
        }
        
        if (npacket != null) {
            //send packet
            Send(npacket);
        } else{
            System.out.println("TRANSPOTATION.JAVA: Send error, npacket is null");
        }

        return npacket;
    }
    
    /**
     * Routing Protocol
     *
     * @param fdst
     * @return
     */
    private NetworkProfile getNextNode(NetworkProfile fdst) {
        //Serch Routing Protocol
//        if(fdst == null)
//            System.out.println("next Node null");
        return fdst;
    }

    /**
     * get Final Destination using Routing Protocol
     *
     * @param dst
     * @return
     */
    private NetworkProfile getFinalDestination(NetworkProfile dst) {
        NetworkProfile fdst = null;
//        if (dst.isNameProfile()) {
//            //라우팅 스토어에서 검색
//            fdst = dst;
//        } else {
        fdst = dst;
//        }
        return fdst;
    }

    /**
     * send packet to each network module
     *
     * @param packet
     */
    @Override
    public void Send(CoAPPacket packet) {
        try {
            //For Group Message
            if (packet.getSendMSG().isUDPMulticastMode()) {
                for (Network sendNetwork : networkManager.getNetworks().values()) {
                    MakeSourceProfile(packet, sendNetwork);
                    sendNetwork.sendAllNodes(packet);
                }
            } //For private message
            else {
                NetworkType nnodetype = packet.getNextNode().getNetworkType();
                Network sendNetwork = networkManager.getNetworks().get(nnodetype);
                MakeSourceProfile(packet, sendNetwork);
                sendNetwork.send(packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void MakeSourceProfile(CoAPPacket packet, Network sendNetwork) {
        //Send Message
        if (sendNetwork != null) {
            //set Source Node
            if (packet.getSource() == null) {
                packet.setSource(sendNetwork.getProfile());
            }

            //set Previous Node
            if (packet.getPreviosNode() == null) {
                packet.setPrevNode(sendNetwork.getProfile());
            }
        } else {
            System.out.println("Error : There are no Networks");
        }
    }

    /**
     * @deprecated @param packet
     */
    @Override
    public void EndPointReceive(RecvMSG packet) {
    }

}
