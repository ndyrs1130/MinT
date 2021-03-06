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
import MinTFramework.SystemScheduler.MinTthreadPools;
import MinTFramework.Util.Benchmarks.Performance;

/**
 * Send Adapter Thread Pool
 *  - A worker thread for Sending thread pool
 *  - 
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class SendAdapter extends Thread{
    private Transportation sender;
    
    private Performance bench = null;
    private MinT parent;
    
    
    public SendAdapter(Runnable r, String name) {
        super(r, name);
        sender = new Transportation(NetworkLayers.LAYER_DIRECTION.SEND);
        parent = MinT.getInstance();
        if(parent.getBenchmark().isMakeBench()){
            bench = new Performance("SendAdaptor");
            parent.getBenchmark().addPerformance(MinTthreadPools.NET_SEND.toString(), bench);
        }
    }
    
    public Transportation getTransportation(){
        return sender;
    }
    
    public Performance getBench(){
        return bench;
    }

//    @Override
//    public void run() {
//        if(bench != null)
//            bench.startPerform();
//        sender.EndPointSend(sendmsg);
//        if(bench != null)
//            bench.endPerform();
//    }
}
