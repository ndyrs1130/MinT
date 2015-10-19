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
package MinTFramework;

import MinTFramework.ExternalDevice.DeviceClassification;
import MinTFramework.ExternalDevice.DeviceType;
import MinTFramework.ExternalDevice.DeviceManager;
import MinTFramework.ExternalDevice.Device;
import MinTFramework.Network.ApplicationProtocol;
import MinTFramework.Network.Handler;
import MinTFramework.Network.MinTApplicationPacketProtocol;
import MinTFramework.Network.Network;
import MinTFramework.Network.NetworkManager;

/**
 *
 * @author soobin Jeon <j.soobin@gmail.com>, chungsan Lee <dj.zlee@gmail.com>,
 * youngtak Han <gksdudxkr@gmail.com>
 */
public class MinT {

    private DeviceManager devicemanager;
    private NetworkManager networkmanager;
    private Scheduler scheduler;
    private Handler networkHandler;
    private ApplicationProtocol ap;
    static final int DEFAULT_THREAD_NUM = 5;
    static final int DEFAULT_REQEUSTQUEUE_LENGTH = 5;
    DeviceClassification deviceClassification;
    DeviceType deviceType;

    /**
     * Framework Constructor
     * Default number of WorkerThread and Requestqueuelength : 5
     */
    public MinT() {
        devicemanager = new DeviceManager();
        networkmanager = new NetworkManager();
        scheduler = new Scheduler(DEFAULT_REQEUSTQUEUE_LENGTH, DEFAULT_THREAD_NUM);
        networkHandler = null;
        ap = new MinTApplicationPacketProtocol();
    }

    /**
     * 
     * @param requestQueueLength Maximym request queue length
     * @param numOfThread number of workerthread in framework
     */
    public MinT(int requestQueueLength, int numOfThread) {
        devicemanager = new DeviceManager();
        scheduler = new Scheduler(requestQueueLength, numOfThread);
    }
    
    /**
     * 
     * @param deviceClassification Class of deivce (Sensor or Network)
     * @param deviceType Type of device (Temperature, Humidity, etc)
     */
    public MinT(DeviceClassification deviceClassification, DeviceType deviceType) {
        devicemanager = new DeviceManager();
        this.deviceClassification = deviceClassification;
        this.deviceType = deviceType;
        scheduler = new Scheduler(DEFAULT_REQEUSTQUEUE_LENGTH, DEFAULT_THREAD_NUM);
    }
    
    /**
     * 
     * @param requestQueueLength Maximym request queue length
     * @param numOfThread number of workerthread in framework
     * @param deviceClassification Class of deivce (Sensor or Network)
     * @param deviceType Type of device (Temperature, Humidity, etc)
     */
    public MinT(int requestQueueLength, int numOfThread, 
            DeviceClassification deviceClassification, DeviceType deviceType) {
        devicemanager = new DeviceManager();
        this.deviceClassification = deviceClassification;
        this.deviceType = deviceType;
        scheduler = new Scheduler(requestQueueLength, numOfThread);
    }
    
    /**
     * Add device to device manager
     * @param device device that want to add
     */
    public void addDevice(Device device) {
        devicemanager.addDevice(device);
    }
    /**
     * Add device to device manager
     * @param device device that wnat to add
     * @param name name that want to name
     */
    public void addDevice(Device device, String name){
        devicemanager.addDevice(device, name);
    }
    
    /**
     * Return device for the ID.
     * @param DeviceID 
     * @return Device for ID
     */
    public Device getDevice(int DeviceID) {
        return devicemanager.getDevice(DeviceID);
    }
    /**
     * Return device for name
     * @param name name for device
     * @return Device for name
     */
    public Device getDevice(String name){
        return devicemanager.getDevice(name);
    }
    /**
     * Remove device for the ID.
     * @param deviceID 
     */
    public void removeDevice(int deviceID) {
        devicemanager.removeDevice(deviceID);
    }
    
    /**
     * Get Array of device IDs
     * @return Array of device IDs
     */
    public int[] getDeviceIDList() {
        return devicemanager.getDeviceList();
    }
    /**
     * Return all devices in device manager
     * @return array of device
     */
    public Device[] getAllDevices(){
        return devicemanager.getAllDevices();
    }
    /**
     * Check whether device manager has device for ID.
     * @param deviceID Device ID to check
     * @return 
     */
    public boolean hasDevice(int deviceID) {
        return devicemanager.hasDevice(deviceID);
    }
    /**
     * Remove all of the devices in the device manager
     */
    public void clearDeviceList() {
        devicemanager.clearDeviceList();
    }
    /**
     * Remove request in Scheduler
     * @param request Request object to remove from scheduler
     */
    public void stopRequest(Request request) {
        scheduler.stopRequest(request);
    }
    /**
     * Add request in Scheduler
     * @param request Request object to add to scheduler
     */
    public void putRequest(Request request) {
        scheduler.putRequest(request);
    }

    /**
     * Print request name and id in thread in scheduler
     */
    public void showWorkingThreads() {
        scheduler.showWorkingThreads();
    }
    
    
    /*************************
     * Network
     ************************/
    /**
     * Setting Network
     * @param network 
     */
    public void setNetwork(Network network){
        networkmanager.setNetwork(network);
        networkmanager.setApplicationProtocol(ap);
    }
    public void setNetworkHandler(Handler nhandler){
        this.networkHandler = nhandler;
    }
    public Handler getNetworkHandler(){
        return this.networkHandler;
    }
    public void setNodeName(String name){
        networkmanager.setNodeName(name);
    }
    public String getNodeName(){
        return networkmanager.getNodeName();
    }
    /**
     * Send Message
     * @param dst
     * @param msg 
     */
    public void sendMessage(String dst, String msg){
        networkmanager.sendMsg(dst, msg);
    }
    
    public void setApplicationProtocol(ApplicationProtocol ap){
        networkmanager.setApplicationProtocol(ap);
    }
    
    /**
     * Start framework
     */
    public void Start() {
        devicemanager.initAllDevice();
        SchedRun();
    }
    /**
     * Start scheduler
     * Initialize all devices in device manager
     */
    protected void SchedRun() {
        scheduler.SchedulerRunning();
    }
    
    
    
}
