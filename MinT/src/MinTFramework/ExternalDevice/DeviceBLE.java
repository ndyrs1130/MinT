/*
 * Copyright (C) 2015 HanYoungTak
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
package MinTFramework.ExternalDevice;

/**
 *
 * @author HanYoungTak
 */
public abstract class DeviceBLE extends Device{

    public int uartNumber;
    
    public DeviceBLE(String _LibName) {
        super(_LibName);
    }
    
    public DeviceBLE(String _LibName, int uartNumber) {
        super(_LibName);
        this.uartNumber = uartNumber;
    }

    abstract public String initBLE(int mode);        //Ready for work
    abstract public String discovery();                //Discovery BLE device
    abstract public String setName(String name);     //Set name
    abstract public String getAddress();               //Get address
    abstract public String setRole(int mode);        //Set role
    abstract public String connect(String address);  //Connect to other device
    abstract public String disconnect();               //Disconnect
    abstract public void writeUART(String command);
    abstract public String readUARTString();
    abstract public Byte[] readUARTBytes(int length);
    
    
}
