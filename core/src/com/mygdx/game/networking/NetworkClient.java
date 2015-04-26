package com.mygdx.game.networking;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.mygdx.game.GameScreen;
import com.mygdx.game.components.PlayerComponent;
import com.mygdx.game.dungeon.DungeonGenerator;
import com.mygdx.game.utility.Factory;
import com.mygdx.game.utility.RandomInt;

public class NetworkClient extends Thread {
	public GameScreen gScreen;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public NetworkClient(GameScreen gScreen) {
		this.gScreen = gScreen;
		
		System.out.println("CLIENT");
		SocketHints socketHints = new SocketHints();
		socketHints.connectTimeout = 10000; //10s?

		Socket socket = Gdx.net.newClientSocket(Protocol.TCP, gScreen.ip, gScreen.port, socketHints);
		
		System.out.println("CONNECTED");
		try 
		{
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
		} 
		catch (Exception e) 
		{
			System.out.println("Exception in client code:" + e.getMessage());
			e.printStackTrace();
		}
		
		initialize();
		
		start();
	}
	
	public void initialize() {
		try {
			Object o = null;
			o = ois.readObject();
			if (o.getClass() == HashMap.class) {
				System.out.println("Receiving Hashmap");
				
				gScreen.networkPlayerNum = (Integer)((HashMap)o).get("playerNum");
				System.out.println(gScreen.networkPlayerNum);
				
				long mapSeed = (Long)((HashMap)o).get("mapSeed");
				RandomInt.setSeed(mapSeed);
				DungeonGenerator.generateDungeon(gScreen);
				
				oos.writeObject(GameScreen.myEntities);
				oos.flush();
				oos.reset();
				
				gScreen.initialized = true;
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void run() {
		while (true) {
			try {
				Object o = null;
				try {
					o = ois.readObject();
				}
				catch (Exception e) {System.out.println(e.getMessage());}

				if (o.getClass() == CopyOnWriteArraySet.class) {
					//System.out.println("Receiving HashSet (" + ((CopyOnWriteArraySet<HashMap<String, Object>>)o).size() + "):" + o.toString());
					
					for (HashMap<String, Object> entity : (CopyOnWriteArraySet<HashMap<String, Object>>)o) {
						boolean entityExists = false;
						for (HashMap<String, Object> entity2 : GameScreen.allEntities) {
		            		if (entity2.get("playerNum").equals(entity.get("playerNum")) && entity2.get("ownerID").equals(entity.get("ownerID"))) {
		            			//Entity exists, so replace its values
		            			//GameScreen.allEntities.remove(entity2);
		            			entity2 = entity;
		            			//
		            			entityExists = true;
		            		}
		            	}
						
						if (entityExists) {
							//Update the entity
						}
						else {
							//Create the entity
							if (entity.get("type").equals("player")) {
								Factory.createPlayer((Float) entity.get("xPos"), (Float) entity.get("yPos"), (Integer)entity.get("playerNum"));
							}
							else if (entity.get("type").equals("bullet")) {
								Factory.createBullet((Float) entity.get("xPos"), (Float) entity.get("yPos"), (Float) entity.get("xVel"), (Float) entity.get("yVel"), (Integer)entity.get("playerNum"));
							}
							GameScreen.allEntities.add(entity);
						}
					}
					oos.writeObject(GameScreen.myEntities);
					oos.flush();
					oos.reset();
				}
				else if (o.getClass() == String.class) {
					System.out.println("Receiving string: " + (String)o);
				}
				else {
					System.out.println("Receiving other datatype:" + o.toString());
				}
			}
			catch (Exception e) {
				System.out.println("Exception in client code:" + e.getMessage());
				e.printStackTrace();
			};
		}
	}
}
