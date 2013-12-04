/*
* A class for easy handling of the OSCthulhu client through SC.
*/
OSCthulhu {
	classvar <>clientPort = 32243;
	classvar <>userName;
	// classvar latencyRoutine;
	/*
	*initClass {
	var name;
	Class.initClassTree(OSCdef);
	Class.initClassTree(NetAddr);

	name = OSCthulhu.onUserName(\OSCthulhuUserName,{|msg| OSCthulhu.userName = msg[1];});
	name.permanent_(true);
	OSCthulhu.login();
	}
	*/

	//////////////// tell the OSCthulhu to do things ////////////////

	/*
	* Tell the client to send to the following port(s).
	*/
	*changePorts {|portList|
		var msg;
		if(portList.isKindOf(Collection),{
			msg = Array.new(portList.size+1);
			msg.add("/changePorts");
			portList.do{|port| msg.add(port);};
			},{
				msg = Array.new(2);
				msg.add("/changePorts");
				msg.add(portList);
		});

		NetAddr("127.0.0.1",clientPort).sendRaw(msg.asRawOSC);
	}

	/*
	* ask for login sync
	*/
	*login {|piece|
		OSCFunc({|msg| userName = msg[1];}, '/userName', nil).oneShot;
		if(piece.isNil,{
			NetAddr("127.0.0.1",clientPort).sendMsg("/login");
			},{
				NetAddr("127.0.0.1",clientPort).sendMsg("/login",piece);
		});
	}

	/*
	* name, group, and subgroup are individual arguments, all object args must be in an array.
	*/
	*addSyncObject {|objName,objGroup,objSubGroup,argArray|
		var msg;

		msg = Array.new((argArray.size*2)+4);
		msg.add("/addSyncObject");
		msg.add(objName);
		msg.add(objGroup);
		msg.add(objSubGroup);
		if( argArray.size != 0,{
			msg.addAll(argArray);
		});

		NetAddr("127.0.0.1",clientPort).sendRaw(msg.asRawOSC);
	}

	/*
	* set an arg for a sync object
	*/
	*setSyncArg {|objName,objArgNumber,objArgValue|
		NetAddr("127.0.0.1",clientPort).sendMsg("/setSyncArg",objName,objArgNumber,objArgValue);
	}

	/*
	* remove a sync object from the server
	*/
	*removeSyncObject {|objName|
		NetAddr("127.0.0.1",clientPort).sendMsg("/removeSyncObject",objName);
	}

	/*
	* send a chat
	*/
	*chat {|message|
		NetAddr("127.0.0.1",clientPort).sendMsg("/chat",message);
	}

	/*
	* force remove all sync objects
	*/
	*flush {
		NetAddr("127.0.0.1",clientPort).sendMsg("/flush");
	}

	/*
	* ask the server to remove all objects for a piece if noone is still in it
	*/
	*cleanup {|piece|
		NetAddr("127.0.0.1",clientPort).sendMsg("/cleanup",piece);
	}

	/*
	* get the body of the chat window from the client (useful at login)
	*/
	*getChat
	{
		NetAddr("127.0.0.1",clientPort).sendMsg("/getChat");
	}

	//////////////// make OSCdefs to listen to the OSCthulhu ////////////////

	/*
	* return a new OSCdef for /addSyncObject with the supplied function
	*/
	*onAddSyncObject {|key, function|
		^OSCdef.new(key, function, '/addSyncObject');
	}

	/*
	* return a new OSCdef for /setSyncArg with the supplied function
	*/
	*onSetSyncArg {|key, function|
		^OSCdef.new(key, function, '/setSyncArg');
	}

	/*
	* return a new OSCdef for /removeSyncObject with the supplied function
	*/
	*onRemoveSyncObject {|key, function|
		^OSCdef.new(key, function, '/removeSyncObject');
	}

	/*
	* return a new OSCdef for /addPeer with the supplied function
	*/
	*onAddPeer {|key, function|
		^OSCdef.new(key, function, '/addPeer');
	}

	/*
	* return a new OSCdef for /removePeer with the supplied function
	*/
	*onRemovePeer {|key, function|
		^OSCdef.new(key, function, '/removePeer');
	}

	/*
	* return a new OSCdef for /chat with the supplied function
	*/
	*onChat {|key, function|
		^OSCdef.new(key, function, '/chat');
	}

	/*
	* return a new OSCdef for /getChat with the supplied function
	*/
	*onGetChat {|key, function|
		^OSCdef.new(key, function, '/getChat');
	}

	/*
	* return a new OSCdef for /userName with the supplied function
	*/
	*onUserName {|key, function|
		^OSCdef.new(key, function, '/userName');
	}

	/*
	* return a new OSCdef for /ports with the supplied function
	*/
	*onPorts {|key, function|
		^OSCdef.new(key, function, '/ports');
	}

/*	*printLatency { |doPrintLatency|
		if(doPrintLatency, {

			OSCthulhu.addSyncObject("LatencyTest", "Testing", "Latency", 0);

			OSCthulhu.onSetSyncArg(\testLatency, {
				|msg, time, addr, recvPortg|
				var localTime = Date.localtime.secStamp;
				("CurrentTime: " ++ localTime ++ " PacketTime: " ++ time ++ " difference: ").postln;
			});

			latencyRoutine = Routine.new({

				loop {
					OSCthulhu.setSyncArg("LatencyTest", 0, 1.0.rand);
					Date.localtime.secStamp.postln;
					0.5.wait;
				}

			}).play;

			},{
				if(latencyRoutine != nil, {
					latencyRoutine.stop;
					OSCthulhu.removeSyncObject("LatencyTest");
				});
		});
	}*/

}

/*
* A class for persisting the state of OSCthulhu
*/
OSCthulhuWorld
{
	var syncObjects, responders;

	*new
	{
		^super.newCopyArgs().init;
	}

	init
	{
		syncObjects = Dictionary();
		responders = List();


	}
}

/*
* A representation of an OSCthulhu SyncObject
*/
OSCthulhuSyncObject
{
	var objName,objGroup,objSubGroup,argArray;

	*new
	{|objName,objGroup,objSubGroup,argArray|
		^super.newCopyArgs(objName,objGroup,objSubGroup,argArray).init;
	}

	init
	{

	}

	setSyncArg
	{|objArgNumber,objArgValue|

	}
}

/*

// start OSCthulhu first!

// make the client send to a different port
OSCthulhu.changePorts(32244);

// make the client send to two ports
OSCthulhu.changePorts([57120,32244]);

// login to a specific piece
OSCthulhu.login("test");

// once you are logged in, you can get your user name from here
OSCthulhu.userName;

// make a new OSCdef for the various OSCthulhu message types (created and returned)
o = OSCthulhu.onAddSyncObject(\aNewAwesomeObject,{|m| m.postln;});

o.free;	// remove it when you are done.

// add a sync object
OSCthulhu.addSyncObject("awesomeObject", "test", "subgroup", [1,2.0,"three"]);

// remove it
OSCthulhu.removeSyncObject("awesomeObject");

// log out of a piece. if no one is logged into a piece, all objects for that piece are removed
OSCthulhu.cleanup("test");

// flush the server
OSCthulhu.flush;

*/