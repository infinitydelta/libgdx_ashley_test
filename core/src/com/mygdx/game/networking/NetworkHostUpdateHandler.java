package com.mygdx.game.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

import com.badlogic.gdx.net.Socket;
import com.mygdx.game.GameScreen;
import com.mygdx.game.utility.Factory;

public class NetworkHostUpdateHandler extends Thread {
	NetworkHost networkHost;
	Socket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	public HashSet<HashMap<String, Object>> entities;
	
	public NetworkHostUpdateHandler(NetworkHost networkHost, Socket socket, ObjectOutputStream oos) {
		this.networkHost = networkHost;
		this.socket = socket;
		try {
			this.ois = new ObjectInputStream(socket.getInputStream());
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		};
		this.oos = oos;
		
		start();
	}
	
	public void run()
	{	
		//FOREVER
		while(true)
		{
			try
			{
				Object o = ois.readObject();
				if (o.getClass() == CopyOnWriteArraySet.class) {
					//System.out.println("Receiving hashset (" + ((CopyOnWriteArraySet<HashMap<String, Object>>)o).size() + "):" + o.toString());
					
					for (HashMap<String, Object> entity : (CopyOnWriteArraySet<HashMap<String, Object>>)o) {
						boolean entityExists = false;
						
						for (HashMap<String, Object> entity2 : GameScreen.allEntities) {
		            		if (entity2.get("playerNum").equals(entity.get("playerNum")) && entity2.get("ownerID").equals(entity.get("ownerID"))) {
		            			GameScreen.allEntities.remove(entity2);
		            			entityExists = true;
		            		}
		            	}
						if (entityExists) {
							//Update the entity
						}
						else {
							//Create the entity
							if (entity.get("type").equals("player")) {
								//Factory.createPlayer((Integer) entity.get("xPos"), (Integer) entity.get("yPos"));
								Factory.createBullet((Float) entity.get("xPos"), (Float) entity.get("yPos"), 0f, 0f, (Integer) entity.get("playerNum"));
							}
						}
						synchronized(GameScreen.allEntities) {
							GameScreen.allEntities.add(entity);
						}
					}
					synchronized(GameScreen.allEntities) {
						oos.writeObject(GameScreen.allEntities);
					}
					//oos.writeObject(GameScreen.allEntities);
					oos.flush();
					oos.reset();
					//System.out.println("Receiving string from " + socket.getRemoteAddress() + ":" + (String)o);
				}
				else if (o.getClass() == String.class) {
					if (((String)o).equals("Ready")) {
						//System.out.println("Sending data to " + socket.getRemoteAddress());
						synchronized(GameScreen.allEntities) {
							oos.writeObject(GameScreen.allEntities);
						}
						//oos.writeObject(GameScreen.allEntities);
						oos.flush();
						oos.reset();
					}
					//System.out.println("Receiving string from " + socket.getRemoteAddress() + ":" + (String)o);
				}
				else {
					//System.out.println("Receiving other datatype from " + socket.getRemoteAddress());
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
