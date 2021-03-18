package com.bentperception.instrumentrouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;


// Serial port methods "borrowed" from Arduino example here:
// http://playground.arduino.cc/Interfacing/Java#.UzDfua1dWDQ
// And then modified for my use.

@SuppressWarnings("restriction")
public class InstrumentRouter implements SerialPortEventListener {

	SerialPort serialPort;
	//static InstrumentRouter arduino;
	static InstrumentRouter listener;
	static ValueBean readings;

	
/**
* A BufferedReader which will be fed by a InputStreamReader 
* converting the bytes into characters 
* making the displayed results codepage independent
*/
private BufferedReader input;
/** The output stream to the port */
private OutputStream output;
/** Milliseconds to block while waiting for port open */
private static final int TIME_OUT = 2000;
/** Default bits per second for COM port. */
private static final int DATA_RATE = 9600;

public void initialize(String port, boolean eListen) {
	CommPortIdentifier portId = null;
	Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

	//First, Find an instance of serial port as set in PORT_NAMES.
	while (portEnum.hasMoreElements()) {
		CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();

		if (currPortId.getName().equals(port)) {
			portId = currPortId;
			break;
		}

	}
	if (portId == null) {
		System.out.println("Could not find the specified port: " + port);
		return;
	}

	try {
		// open serial port, and use class name for the appName.
		serialPort = (SerialPort) portId.open(this.getClass().getName(),
				TIME_OUT);

		// set port parameters
		serialPort.setSerialPortParams(DATA_RATE,
				SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		// open the streams
		input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
		output = serialPort.getOutputStream();

		// add event listeners
		if (eListen == true) {
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		}
	} catch (Exception e) {
		System.err.println(e.toString());
	}
}

/**
 * This should be called when you stop using the port.
 * This will prevent port locking on platforms like Linux.
 */
public synchronized void close() {
	if (serialPort != null) {
		serialPort.removeEventListener();
		serialPort.close();
	}
}

// Send characters out to this serial port.
public void send(byte[] a) {
	try {
		output.write(a);
	} catch (IOException e) {
		e.printStackTrace();
	}
}

/**
 * Handle an event on the serial port. Read the data and print it.
 */
public synchronized void serialEvent(SerialPortEvent oEvent) {
	if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
		try {
			String inputLine=input.readLine();
			this.sendCommand(inputLine);
			
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	// Ignore all the other eventTypes, but you should consider the other ones.
}


public void sendCommand(String command) {
	// Interprets and sends the command to the right place.
	
	String cmd = command.substring(0,1).toUpperCase();
	byte[] value = command.getBytes();
	String beanValue = command.substring(1);
	
	if (cmd.equals("S")) {
		// Speed
		readings.setSpeed(Integer.parseInt(beanValue));
	} else if (cmd.equals("F")) {
		// Fuel
		readings.setFuel(Integer.parseInt(beanValue));
	} else if (cmd.equals("T")) {
		// Tach
		readings.setTach(Integer.parseInt(beanValue));
	} else if (cmd.equals("O")) {
		// Oil
		readings.setOil(Integer.parseInt(beanValue));
	} else if (cmd.equals("V")) {
		// Volts
		readings.setVolts(Integer.parseInt(beanValue));
	} else if (cmd.equals("B")) {
		// ABS
		readings.setAbs(Integer.parseInt(beanValue));
	} else if (cmd.equals("C")) {
		// VDC Off
		readings.setVdcOff(Integer.parseInt(beanValue));
	} else if (cmd.equals("E")) {
		// Seat belt
		readings.setBelt(Integer.parseInt(beanValue));
	} else if (cmd.equals("L")) {
		// Low Oil
		readings.setLowOil(Integer.parseInt(beanValue));
	} else if (cmd.equals("R")) {
		// Cruise
		readings.setCruise(Integer.parseInt(beanValue));
	} else if (cmd.equals("W")) {
		// AWD
		readings.setAwd(Integer.parseInt(beanValue));
	} else if (cmd.equals("I")) {
		// AT OIL TEMP
		readings.setAtOil(Integer.parseInt(beanValue));
	} else if (cmd.equals("M")) {
		// MIL/CHECK ENGINE
		readings.setMil(Integer.parseInt(beanValue));
	} else if (cmd.equals("G")) {
		// Gear
		String tmpString = beanValue.toLowerCase();
		char y = tmpString.charAt(0);
		readings.setGear(y);
	} else if (cmd.equals("P")) {
		// Temp
		String tmpString = beanValue.toLowerCase();
		char y = tmpString.charAt(0);
		readings.setTemp(y);
		
	}

	
	
}

public static void main(String[] args) throws Exception {
	
	String listenPort = new String();
	String cmdArg =  new String();
	String cmdValue = new String();
	String canSendPath = new String();;
	
	// Gauge Readings Bean
	readings = new ValueBean();
	
	for (String a: args) {
		cmdArg = a.substring(0,a.indexOf(":") );
		cmdValue = a.substring(a.indexOf(":") + 1);

		if (cmdArg.equals("-listenerPort")) {
			listenPort=cmdValue;
		}
		if (cmdArg.equals("-cansend")) { 
			canSendPath=cmdValue;
		}
	}
	
	if ( listenPort.equals("") || canSendPath.equals("") ){
		throw new IllegalArgumentException();
	}
	
	final String canPath = canSendPath;
	
	listener = new InstrumentRouter();
	listener.initialize(listenPort,true);

	Thread canStream=new Thread() {
		public void run() {
			// Run the CAN stream here.
			try {
				
				ProcessBuilder p = new ProcessBuilder();
				
				// List of IDs that need to have messages put out onto the bus.
				// 188,0D3,0D1,0D2,370,372,360,361,148,141,0D3,0D1,144,368,374,284, 15A
				String[] canIds = { "144", "188", "0D3", "0D2", "370", "372", "360", "361", "148",
				"141", "0D3", "368", "374","284", "15A" };
	
				String speedVal = "0000";
				String tachVal = "0000";
				String maxJobs = "00";
				int maxJobCount = 0;
				
				while(true) {
				// See 2015/6th Gen cluster notes for CAN IDs
					
				// Loop through the CAN IDs, and assemble and send the frames
				for (String id: canIds){
					// 360 361 148 -  oil(15A) volts(use VDC) belt "low oil" abs vdc-off 
					// cruise (and cr speed), awd, at oil temp, gear  temp (now fuel)
					
					// Speed
					//if ( id.equals("0D1") ) {
						String spdhex = Integer.toHexString((int) (readings.returnSpeed()/.5625)).toUpperCase();
						if (spdhex.length() == 1) {
							spdhex = "0" + spdhex;
						}
						//System.out.println(hex + "," + "0D1#" + speedVal + "0704");
						speedVal = spdhex.charAt(1) + "00" + spdhex.charAt(0);
						p.command(canPath,"can0","0D1#" + speedVal + "0704");
						Process prs = p.start();
						int rcs = prs.waitFor();
					//}
					
					// Tach
					if ( id.equals("141") ) {
						String scale = "0";
						String hex = Integer.toHexString((int) ((readings.returnTach()*100)/16)).toUpperCase();
						//System.out.println(hex)
						
						if (hex.length() == 1) {
							hex = "00" + hex;
						} else if (hex.length() == 2) {
							hex = "0" + hex;
						}
						tachVal = hex.charAt(2) + "0" + hex.charAt(0) + hex.charAt(1);
						p.command(canPath,"can0","141#00000000" + tachVal + "0000");
					}
					
					// Oil light
					if ( id.equals("15A") ) {
						p.command(canPath,"can0","15A#00000000" + readings.returnOil() + "0000000");
					}
					
					// Gear Display
					//148#8B20000000000000
					if ( id.equals("148") ) {
						p.command(canPath,"can0","148#" + readings.returnGear() + "000000000000");
					}
					
					if ( id.equals("188") ) {
						p.command(canPath,"can0","188#01000C0001070000");
					}
					
					// VDC (replaces VOLTS) and VDC OFF
					if ( id.equals("0D3") ) {
						p.command(canPath,"can0","0D3#00" + readings.returnVdcOff() + "00" + readings.returnVolts() + "0000000");
					}
					
					if ( id.equals("0D2")) {
						p.command(canPath,"can0","0D2#0000FFFF00002E2F");
					}
					
					// MIL and Low Oil
					if ( id.equals("361")) {
						p.command(canPath,"can0","361#" + readings.returnLowOil() + readings.returnMil() + "000000000000");
					}
					
					// Coolant Temp and Cruise - Coolant represents fuel level for now.
					// Temp scale runs 59 to FF (89 to 255, 166 positions)
					if ( id.equals("360")) {
						
						if ( maxJobCount < readings.returnTach()) {	
							maxJobCount = readings.returnTach();
							
							maxJobs = Integer.toHexString(maxJobCount);
							
							if (maxJobCount < 16) {
								maxJobs = "0" + maxJobs;
							}
						}
						float fuelLevel = 100-readings.returnFuel();
						String fuelHex = Integer.toHexString((int) (89 + fuelLevel));
						//System.out.println(fuelHex);
						p.command(canPath,"can0","360#000000" + fuelHex + "00" + readings.returnCruise() + "00" + maxJobs);
					}
					
					if ( id.equals("370")) {
						p.command(canPath,"can0","370#0000100000000000");
					}
					
					// Seat belt
					if ( id.equals("372")) {
						p.command(canPath,"can0","372#00000000" + readings.returnBelt() + "000000");
					}
					
					if ( id.equals("144")) {
						p.command(canPath,"can0", "144#0001533716A80800");
					}
					
					// AWD & AT OIL TEMP
					if ( id.equals("368")) {
						p.command(canPath,"can0", "368#01" + readings.returnAtOil() + readings.returnAwd() + "000000000000");
					}
					
					if ( id.equals("284")) {
						p.command(canPath,"can0", "284#0000000020000000");
					}
					
					if ( id.equals("374")) {
						p.command(canPath,"can0", "374#4800000000000000");
					}

					Process pr = p.start();
					int rc = pr.waitFor();
					
				}
			
			// Ends the while(true) loop...
			}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	canStream.start();

	System.out.println("Started");
}
}
	
