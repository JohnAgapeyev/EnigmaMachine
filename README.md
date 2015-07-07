Enigma Machine
John Agapeyev

This is an enigma machine, written in Java, and coded using the swing framework.

In its current state it is finished, no further documentation or additions are planned.
	
It reads from config.ini and loads user settings if available, otherwise it will either randomly generate keys or load the default ones.
Altering the second and third line allows the program to randomly generate keys for the rotor and reflector.
If the user wants to enter custom keys, that can also be done by altering the config file.

When user settings are loaded from file, they are defaulted in the order 1-2-3.
If the user decides to alter the rotors chosen or their order, the first three rotors will be the ones loaded from file, and the 4th and 5th are the default ones.
