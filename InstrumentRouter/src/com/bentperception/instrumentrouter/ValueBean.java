package com.bentperception.instrumentrouter;

public class ValueBean {

	// Speed - 000-150
	private int speed = 0;
	
	// Tach - 000 - 080
	private int tach = 0;
	
	// The idiot lights - 0 off, all others (1-255) on
	private int oil = 0;
	private int volts = 0;
	private String belt = "00";
	private int abs = 0;
	private String lowoil = "00";
	
	// Cruise: 0 = off, 1 = Cruise, 2 = Cruise + SET
	private String cruise = "00";
	
	// 0 off, 1 on, 2 slow flash, 3 fast flash (AT + MIL only)
	private String vdcoff = "00";
	private String awd = "00";
	private String atoil = "00";
	private String mil = "40";
	
	// Fuel - 0-100 (a percentage).  100% = full, 0% = empty
	private int fuel = 100;
	
	// Gear - P, R, N, E, -, 1-4, D
	private String gear = "0E10";
	
	// Temp - C (cold), N (normal), H (hot)
	private String temp = "00";

	public void setVdcOff(int a) {
		
		switch (a) {
			case 0:
				vdcoff="00";
				break;
			case 1:
				vdcoff="0A";
				break;

			default:
				throw new IllegalArgumentException();
		}
	}

	public String returnVdcOff() {
		return vdcoff;
	}
	
	public void setOil(int a) {
		if (oil != 0) {
			oil = 2;
		} else {
			oil = 0;
		}
	}
	
	public String returnOil() {
		return Integer.toString(oil);
	}
	
	public void setVolts(int a) {
		if (a == 1) {
			volts = 4;
		} else {
			volts = 0;
		}
	}
	
	public String returnVolts() {
		return Integer.toString(volts + abs);
	}
	
	public void setBelt(int a) {
		if (a == 0) {
			belt="00";
		} else {
			belt="40";
		}
	}
	
	public String returnBelt(){
		return belt;
	}
	
	// In Gen6, VDC and ABS uses the same byte.  Using volts placeholder for VDC
	// as conversion from Gen5 combination meters.
	public void setAbs(int a) {
		if (a == 0) {
			abs = 0;
		} else {
			abs = 2;
		}
	}
	
	public String returnAbs() {
		return Integer.toString(volts + abs);
	}
	
	public void setCruise(int a) {
		
		switch (a) {
			case 0:
				cruise="00";
				break;
			case 1:
				cruise="10";
				break;
			case 2:
				cruise="30";
				break;
				
			default:
				throw new IllegalArgumentException();
		}
		
	}
	
	public String returnCruise(){
		return cruise;
	}
	
	public void setLowOil(int a) {
		if (a == 0) {
			lowoil="00";
		} else {
			lowoil="10";
		}
	}
	
	public String returnLowOil(){
		return lowoil;
	}
	
	public void setAtOil(int a) {
		
		switch (a) {
			case 0:
				atoil="0";
				break;
			case 1:
				atoil="4";
				break;
			case 2:
				atoil="8";
				break;
			case 3:
				atoil="C";
				break;
				
			default:
				throw new IllegalArgumentException();

		}
		
	}
	
	public String returnAtOil(){
		return atoil;
	}
	
	public void setAwd(int a) {
		
		switch (a) {
			case 0:
				awd="0";
				break;
			case 1:
				awd="4";
				break;
			case 2:
				awd="8";
				break;
				
			default:
				throw new IllegalArgumentException();
		}
		
	}
	
	public String returnAwd(){
		return awd;
	}
	
	public void setMil(int a) {
		
		switch (a) {
			case 0:
				mil="20";
				break;
			case 1:
				mil="10";
				break;
			case 2:
				mil="80";
				break;
			case 3:
				mil="40";
				break;
				
			default:
				throw new IllegalArgumentException();
		}
		
	}
	
	public String returnMil(){
		return mil;
	}
	
	public void setFuel(int a) {
		if (a >= 0 && a <= 100){
			fuel=a;
			System.out.println("Fuel: " + fuel);
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public int returnFuel(){
		return fuel;
	}
	
	public void setTemp(char a) {
		switch (a){
		case 'c': 
			temp="00";
			break;
		case 'n': 
			temp="5C";
			break;
		case 'h':
			temp="F0";
			break;
			
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public String returnTemp(){
		return temp;
	}
	
	public void setGear(char a) {
		switch (a){
		//148#8B20000000000020
		case 'p': 
			gear="0E10";
			break;
		case 'r': 
			gear="0D10";
			break;
		case 'n':	
			gear="0C10";
			break;
		case 'l': 
			gear="0A10";
			break;
		case 'd': 
			gear="0B10";
			break;
		case '1': 
			gear="1B20";
			break;
		case '2': 
			gear="2B20";
			break;
		case '3': 
			gear="3B20";
			break;
		case '4': 
			gear="4B20";
			break;
		case '5': 
			gear="6B20";
			break;
		case '6': 
			gear="6B20";
			break;
		case '7': 
			gear="7B20";
			break;
		case '8': 
			gear="8B20";
			break;	
		
		default:
			throw new IllegalArgumentException();
		}
	}
	
	public String returnGear(){
		return gear;
	}
	
	public void setSpeed(int a) {
		if (a >= 0 && a <= 150){
			speed=a;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public int returnSpeed(){
		return speed;
	}
	
	public void setTach(int a) {
		if (a >= 0 && a <= 80){
			tach=a;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public int returnTach(){
		return tach;
	}
}
