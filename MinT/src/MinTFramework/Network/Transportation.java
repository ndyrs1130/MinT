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

import MinTFramework.MinT;
import MinTFramework.Util.DebugLog;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class Transportation implements NetworkLayers{
    private MinT frame;
    private NetworkManager networkManager;
    private SystemHandler syshandle = null;
    private MatcherAndSerialization serialization = null;
    DebugLog dl = new DebugLog("Transportation");
    
    public Transportation(NetworkManager.LAYER_DIRECTION layerDirection){
        frame = MinT.getInstance();
        this.networkManager = frame.getNetworkManager();        
        
        if(layerDirection == NetworkManager.LAYER_DIRECTION.RECEIVE)
            syshandle = new SystemHandler();
        
        if(layerDirection == NetworkManager.LAYER_DIRECTION.SEND)
            serialization = new MatcherAndSerialization(layerDirection);
    }

    @Override
    public void Receive(PacketDatagram packet) {
        if(isFinalDestination(packet.getDestinationNode())){
            syshandle.startHandle(packet);
            networkManager.setHandlerCount();
        }
        else{
            stopOver(packet);
        }
    }

    private boolean isFinalDestination(NetworkProfile destinationNode) {
        for(Network cn : this.networkManager.getNetworks().values()){
            if(cn.getProfile().equals(destinationNode))
                return true;
        }
        return false;//currnetProfile.equals(destinationNode);
    }

    /**
     * Stop Over Method
     * @param packet 
     */
    private void stopOver(PacketDatagram packet) {
        
    }
    
    @Override
    public void EndPointSend(SendMSG sendmsg) {
        //Find Final Destination from Routing
        NetworkProfile fdst = getFinalDestination(sendmsg.getDestination());
        
        PacketDatagram npacket = null;
        if(sendmsg.isResponse()){
            npacket = new PacketDatagram(sendmsg.getResponseKey(), sendmsg.getHeader_Direction()
                    ,sendmsg.getHeader_Instruction(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }else if(sendmsg.isRequest()){
            npacket = new PacketDatagram(sendmsg.getResponseKey(), sendmsg.getHeader_Direction()
                    ,sendmsg.getHeader_Instruction(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }
        else if(sendmsg.isRequestGET()){
            if(sendmsg.getSendHit() == 0){
                sendmsg.setResKey(networkManager.getIDMaker().makePacketID());
                networkManager.getResponseList().put(sendmsg.getResponseKey(), sendmsg);
            }
            
            npacket = new PacketDatagram(sendmsg.getResponseKey(), sendmsg.getHeader_Direction()
                    ,sendmsg.getHeader_Instruction(), null, null, getNextNode(sendmsg.getDestination())
                    ,fdst, sendmsg.Message());
        }
        //msg sending count
        sendmsg.Sended();
        
        //send packet
        Send(npacket);
    }
    
    /**
     * Routing Protocol
     *
     * @param fdst
     * @return
     */
    private NetworkProfile getNextNode(NetworkProfile fdst) {
        //Serch Routing Protocol
        return fdst;
    }
    
    /**
     * get Final Destination using Routing Protocol
     * @param dst
     * @return 
     */
    private NetworkProfile getFinalDestination(NetworkProfile dst) {
        NetworkProfile fdst = null;
        if (dst.isNameProfile()) {
            //라우팅 스토어에서 검색
            fdst = dst;
        } else {
            fdst = dst;
        }
        return fdst;
    }
    
    @Override
    public void Send(PacketDatagram packet) {
        serialization.Send(packet);
    }
    
    /**
     * @deprecated 
     * @param packet 
     */
    @Override
    public void EndPointReceive(byte[] packet) {
    }
}
