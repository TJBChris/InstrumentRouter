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
	private String abs = "0F";
	private String lowoil = "00";
	
	// Cruise: 0 = off, 1 = Cruise, 2 = Cruise + SET
	private String cruise = "00";
	
	// 0 off, 1 on, 2 slow flash, 3 fast flash (AT + MIL only)
	private String vdcoff = "06";
	private String awd = "00";
	private String atoil = "00";
	private String mil = "A0";
	
	// Fuel - 0-100 (a percentage).  100% = full, 0% = empty
	private int fuel = 0;
	
	// Gear - P, R, N, E, -, 1-4, D
	private String gear = "70";
	
	// Temp - C (cold), N (normal), H (hot)
	private String temp = "00";

	public void setVdcOff(int a) {
		
		switch (a) {
			case 0:
				vdcoff="06";
				break;
			case 1:
				vdcoff="0F";
				break;

			default:
				throw new IllegalArgumentException();
		}
	}

	public String returnVdcOff() {
		return vdcoff;
	}
	
	public void setOil(int a) {
		oil=a;
	}
	
	public int returnOil() {
		return oil;
	}
	
	public void setVolts(int a) {
		volts=a;
	}
	
	public int returnVolts() {
		return volts;
	}
	
	public void setBelt(int a) {
		if (a == 0) {
			belt="00";
		} else {
			belt="10";
		}
	}
	
	public String returnBelt(){
		return belt;
	}
	
	public void setAbs(int a) {
		if (a == 0) {
			abs="0F";
		} else {
			abs="FF";
		}
	}
	
	public String returnAbs() {
		return abs;
	}
	
	public void setCruise(int a) {
		
		switch (a) {
			case 0:
				cruise="00";
				break;
			case 1:
				cruise="EF";
				break;
			case 2:
				cruise="FA";
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
				atoil="00";
				break;
			case 1:
				atoil="01";
				break;
			case 2:
				atoil="02";
				break;
			case 3:
				atoil="03";
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
				awd="00";
				break;
			case 1:
				awd="02";
				break;
			case 2:
				awd="04";
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
				mil="90";
				break;
			case 1:
				mil="00";
				break;
			case 2:
				mil="B0";
				break;
			case 3:
				mil="A0";
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
		
		case 'p': 
			gear="70";
			break;
		case 'r': 
			gear="60";
			break;
		case 'n':	
			gear="50";
			break;
		case 'e': 
			gear="66";
			break;
		case 'd': 
			gear="40";
			break;
		case '1': 
			gear="61";
			break;
		case '2': 
			gear="62";
			break;
		case '3': 
			gear="63";
			break;
		case '4': 
			gear="64";
			break;
		case '5': 
			gear="65";
			break;
		case '6': 
			gear="59";
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
		if (a >= 0 && a <= 88){
			tach=a;
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public int returnTach(){
		return tach;
	}
}
