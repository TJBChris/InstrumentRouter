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
	static InstrumentRouter arduino;
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
		arduino.send(value);
		readings.setSpeed(Integer.parseInt(beanValue));
		readArdReturn(cmd);
	} else if (cmd.equals("F")) {
		// Fuel
		readings.setFuel(Integer.parseInt(beanValue));
		

	} else if (cmd.equals("T")) {
		// Tach
		arduino.send(value);
		readings.setTach(Integer.parseInt(beanValue));
		readArdReturn(cmd);
	} else if (cmd.equals("O")) {
		// Oil
		arduino.send(value);
		readings.setOil(Integer.parseInt(beanValue));
		readArdReturn(cmd);
	} else if (cmd.equals("V")) {
		// Volts
		arduino.send(value);
		readings.setVolts(Integer.parseInt(beanValue));
		readArdReturn(cmd);
	} else if (cmd.equals("A")) {
		// Airbag
		arduino.send(value);
		//readings.setAirBag(Integer.parseInt(beanValue));
		readArdReturn(cmd);
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

private void readArdReturn(String command) {
	try {
		Thread.sleep(20);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	char c = '1';
	try {
		c = (char)arduino.input.read();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if ( c == '0'){
		System.out.println("Command " + command + " OK.");
	} else {
		System.out.println("Command " + command + " FAILED.");
	}
}

public static void main(String[] args) throws Exception {
	String ardPort = new String();
	String listenPort = new String();
	String cmdArg =  new String();
	String cmdValue = new String();
	String canSendPath = new String();;
	
	// Gauge Readings Bean
	readings = new ValueBean();
	
	for (String a: args) {
		cmdArg = a.substring(0,a.indexOf(":") );
		cmdValue = a.substring(a.indexOf(":") + 1);
		
		if (cmdArg.equals("-arduinoPort")) {
			ardPort=cmdValue;
		}
		if (cmdArg.equals("-listenerPort")) {
			listenPort=cmdValue;
		}
		if (cmdArg.equals("-cansend")) { 
			canSendPath=cmdValue;
		}
	}
	
	if ( ardPort.equals("") || listenPort.equals("") || canSendPath.equals("") ){
		throw new IllegalArgumentException();
	}
	
	final String canPath = canSendPath;
	
	arduino = new InstrumentRouter();
	listener = new InstrumentRouter();
	
	arduino.initialize(ardPort,false);
	listener.initialize(listenPort,true);

	Thread canStream=new Thread() {
		public void run() {
			// Run the CAN stream here.
			try {
				
				ProcessBuilder p = new ProcessBuilder();
				
				// List of IDs that need to have messages put out onto the bus.
				String[] canIds = {"002", "231", "251", "253", "291", "333", "391", "431", "432",
				"451", "452", "491", "4B1", "706" };
				
				String[] fuelReading = new String[2];
				int fuelByteCtr = 0;
				int fuelValue = 0;
				char fullBit = '0';
				
				String[] fuelMap = new String[7];
				fuelMap[0]="F3:F3"; // 1/4
				fuelMap[1]="F2:F2"; // 1/2 
				fuelMap[2]="F1:F1"; // 3/4
				fuelMap[3]="F0:F0"; // F
				fuelMap[4]="F2:F2"; // 3/8
				fuelMap[5]="F0:F0"; // 7/8
				fuelMap[6]="F1:F1"; // 5/8
				
				while(true) {
				// 333 = ABS, VDC OFF, 253 = AT OIL, PRNDL, 251 = AWD
				// 432 = Seatbelt, Fuel Gauge, 452=MIL, 451=Temp, Low Oil (first byte to 70)
				// 231 = Cruise
				
				fullBit = '0';	
					
				// Loop through the CAN IDs, and assemble and send the frames
				for (String id: canIds){
					if ( id.equals("706") ) {
						p.command(canPath,"can0","706#B700000000001235");
					}
					
					if ( id.equals("4B1") ) {
						p.command(canPath,"can0","4B1#0100030001060000");
					}
					
					if ( id.equals("491")) {
						p.command(canPath,"can0","491#5D6320800097B905");
					}
					if ( id.equals("452")) {
						p.command(canPath,"can0","452#" + readings.returnMil() + "00000000000000");
					}
					
					if ( id.equals("451")) {
						p.command(canPath,"can0","451#880000" + readings.returnTemp() + "00" + readings.returnLowOil() + "0000");
					}
					
					if ( id.equals("432")) {
						// Determine gauge reading
						fuelValue = readings.returnFuel();
						fullBit = 0;
						if (fuelValue == 0){
							// Empty 
							fuelReading[0] = "04";
							fuelReading[1] = "04";
						} else if (fuelValue > 0 && fuelValue < 32){
							// 1/4 Tank
							fuelReading[0]=fuelMap[0].substring(0,2);
							fuelReading[1]=fuelMap[0].substring(3);
							
						} else if (fuelValue >=32 && fuelValue < 40) {
							// 3/8 Tank
							fuelReading[0]=fuelMap[4].substring(0,2);
							fuelReading[1]=fuelMap[4].substring(3);
							
						} else if (fuelValue >= 40 && fuelValue < 62 ){
							// 1/2 Tank
							fuelReading[0]=fuelMap[1].substring(0,2);
							fuelReading[1]=fuelMap[1].substring(3);
							
						} else if (fuelValue >=62 & fuelValue < 68) {
							// 5/8 Tank
							fuelReading[0]=fuelMap[6].substring(0,2);
							fuelReading[1]=fuelMap[6].substring(3);
						} else if (fuelValue >=68 && fuelValue < 74 ) {
							// 3/4 Tank
							fuelReading[0]=fuelMap[2].substring(0,2);
							fuelReading[1]=fuelMap[2].substring(3);
						} else if (fuelValue >= 74 && fuelValue < 86) {
							// 7/8 Tank
							fuelReading[0]=fuelMap[5].substring(0,2);
							fuelReading[1]=fuelMap[5].substring(3);
						} else if (fuelValue >= 86 ){
							// Full
							fullBit = '1';
							fuelReading[0]=fuelMap[3].substring(0,2);
							fuelReading[1]=fuelMap[3].substring(3);
						}
						for (int i=0; i<2; i++) {
							p.command(canPath,"can0", "432#00" + readings.returnBelt() + "AA0000" + fullBit + "0" + fuelReading[fuelByteCtr] + "00");
							//System.out.println("432#00" + readings.returnBelt() + "AA000000" + fuelReading[fuelByteCtr] + "00");
						}

						
						
					}
					
					if ( id.equals("431")) {
						p.command(canPath,"can0", "431#0000000000420200");
					}
					
					if ( id.equals("391")) {
						p.command(canPath,"can0", "391#0000000000852100");
					}
					
					if ( id.equals("333")) {
						p.command(canPath,"can0", "333#0000000047" + readings.returnVdcOff() + "D0" + readings.returnAbs());
					}
					
					if ( id.equals("002")) {
						p.command(canPath,"can0", "002#FFF0700160000000");
					}
					if ( id.equals("231")) {
						p.command(canPath,"can0", "231#04062B292B7329" + readings.returnCruise());
					}
					if ( id.equals("251")) {
						p.command(canPath,"can0", "251#00000000" + readings.returnAwd() + "000000");
					}
					if ( id.equals("253")) {
						p.command(canPath,"can0", "253#AEC101" + readings.returnGear() + "003900" + readings.returnAtOil());
					}
					if ( id.equals("291")) {
						p.command(canPath,"can0", "291#06001A2000000B00");
					}
					Process pr = p.start();
					int rc = pr.waitFor();
					Thread.sleep(20);
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
	
