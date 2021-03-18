InstrumentRouter
By: Christopher Hyzer

Originally written: 07/2016
File updated: 03/2021

-------------
 DESCRIPTION
-------------

This code drove my HadoopGauges project (https://www.bentperception.com/blog/?p=102).  It works in combination with AmbariGauges to get Hortonworks HDP 2.x cluster status from HDFS, YARN, and other serivces, and display that status via a Subaru combination meter via a CAN bus connection.  

InstrumentRouter accepts commands from AmbariGauges via a serial connection and pushes the data out to the CAN bus for the Subaru gauges.  This side runs on the Raspberry Pi.

There are two branches: master supports 2010-2014 (5th gen) combination meters, gen6cluster supports 2015-2019 units.

You may ask why serial was chosen when it could be done over a network much more elegantly.  This is due to the fact that my employer at the time was not going to allow me to plug a Franken-Project like this into the corporate network; however I did get the Secuirty org to sign off on using a low-speed serial connection to the Pi as long as the Pi was not connected to any other network (cellular, WiFi, etc.).  This code is very prototypical; once it worked I never cleaned it up.

This project is very old and has been uploaded to GitHub for reference.  It suppored now-unsupported released of HDP and has not been tested on HDP > 2.5.  See the support section for important information before you reach out to me with questions.

------------
 COMPONENTS
------------

To build this project, you'll need basic circuit design and build skills and the following:

* A Raspberry Pi 2 or higher.  This acts as the brain of the project.  The Pi must be running Linux
* A Pi-CAN 2 CAN bus hat for your Pi to interface with the Subaru Combination Meter.
* A USB-Serial adapter with a supported chipset.  
* A combination meter assembly from a 5th or 6th generation Subaru Legacy (InstrumentRouter will vary based on your choice).  A 5th gen is 2010-2014, and 6th is 2015-2019.
* The wiring harness for the combination meter (a 5th Generation 2010-2014 harness will work)
* Pinout information for the combination meter (can be found on Subaru's technical documentation site for a fee)
* A protoboard or other platform onto which you can build your interface circuits between the Pi and combination meter for 5th gen units (be careful, most cars run on 12V, so you'll need to create voltage diving circuitry to avoid blowing up the other components).
* For 6th generation combination meters, only a CAN bus connection is required.  No external circuitry is needed beyond power.

The code outlines how inputs are taken from GPIO pins, and for my implementation, which pins are associated with a particular function.

---------
 SUPPORT
---------

There is absolutely no support for this project.  Questions about this project will likely not be answered.

-------------
 LIMITATIONS
-------------

I have not tested with with Ambari >= 2.7 or HDP > 2.5.  It likely won't work on anything later than those.  USB-serial adapters are finicky beasts, and your mileage may vary.  Prolific devices (real ones, not cheap knock-offs) were used for my implemantation.

---------
 LICENSE
---------
See LICENSE.TXT for details about the license for this software.

InstrumentRouter - Reads commands from the serial port and puts the results out to the CAN bus (formatted for Subaru Legacy combination meter assemblies).
Copyright (C) 2016 Christopher Hyzer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
