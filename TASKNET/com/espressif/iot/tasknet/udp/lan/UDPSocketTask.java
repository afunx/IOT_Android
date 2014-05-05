package com.espressif.iot.tasknet.udp.lan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.espressif.iot.model.device.IOTAddress;
import com.espressif.iot.model.device.IOTDevice.TYPE;
import com.espressif.iot.thread.AbsTaskSyn;


import android.util.Log;

/*
 * it is used to get the response of 255.255.255.255:1025 
 *     "Are You Espressif IOT Smart Plug?"
 */
//public class UDPSocketTask extends Task<List<InetAddress>> {
public class UDPSocketTask extends AbsTaskSyn<List<IOTAddress>> {

	private static final String TAG = "UDPSocketTask";

	private DatagramSocket socket;

	private int hostPort;
	
	private boolean isMulticast;
	
	private InetAddress inetAddress;
	
	private String data;
	
	private byte buf_receive[] = new byte[RECEIVE_LEN];
	
	private static final int IOT_PORT = 1025;
	
	private static final int RECEIVE_LEN = 64;
	
	private static final String PLUG = "Plug";
	private static final String TEMHUM = "Humiture";
	
	private static final int PLUG_RESPONSE_IP_RUBBISH_LEN = ("I'm "+PLUG+".98:fe:34:77:ce:00 ").length();
	
	private static final int PLUG_RESPONSE_BSSID_RUBBISH_LEN = ("I'm "+PLUG+".").length();
	
	private static final int TEMHUM_RESPONSE_IP_RUBBISH_LEN = ("I'm "+TEMHUM+".98:fe:34:77:ce:00 ").length();
	
	private static final int TEMHUM_RESPONSE_BSSID_RUBBISH_LEN = ("I'm "+TEMHUM+".").length();
	
	private static final int RESPONSE_BSSID_LEN = "98:fe:34:77:ce:00".length();
	
	private List<IOTAddress> responseList = new ArrayList<IOTAddress>();
	
	/**
	 * 
	 * @param taskName			task name
	 * @param port				the host port
	 * @param isMulticast		is multicast or not
	 * @param inetAddress		target inetAddress
	 * @param data				the data to be transfered
	 * @see						allocPort(int port)
	 * 
	 */
	public UDPSocketTask(String taskName, int hostPort, boolean isMulticast
			,InetAddress inetAddress, String data) {
		// TODO Auto-generated constructor stub
		super(taskName);
		this.hostPort = hostPort;
		this.isMulticast = isMulticast;
		this.inetAddress = inetAddress;
		this.data = data;
	}

	/**
	 * alloc the port number
	 * 
	 * @param hostPort
	 *            if -1<port<65536, it will allocate a random port, which could be used
	 *            else it will allocate the specified port first, 
	 *                 after that it will allocate a random one
	 */
	private void allocPort(int hostPort) {

		Log.d(TAG+":"+taskName, "allocPort() entrance");

		boolean success = false;

		// try to allocate the specified port
		if (-1 < hostPort && hostPort < 65536) {
			try {
				socket = new DatagramSocket(hostPort);
				success = true;
				Log.d(TAG+":"+taskName, "port is : " + hostPort);
				return;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				Log.w(TAG+":"+taskName, "allocPort(): the hostPort:" + hostPort + " is used");
				e.printStackTrace();
			}
		}
		// allocate a random port
		do {
			try {
				// [1024,65535] is the dynamic port range
				hostPort = 1024 + new Random().nextInt(65536 - 1024);
				socket = new DatagramSocket(hostPort);
				success = true;
				Log.d(TAG+":"+taskName, "hostPort is : " + hostPort);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				Log.e(TAG+":"+taskName, "allocPort(): the hostPort:" + hostPort + " is occupied");
				e.printStackTrace();
			}
		} while (!success);
	}

	@Override
	public Boolean call() {
		// TODO Auto-generated method stub
		allocPort(hostPort);
		socket();
		/*
		 * check()? is it need check ...???
		 */
		return true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return taskName;
	}

	// filter the ip address from the response
	// "I'm Plug.98:fe:34:77:ce:00 192.168.4.1"
	// "I'm Temperature.98:fe:34:77:ce:00 192.168.4.1"
	private String filterIpAddress(byte[] data,TYPE type){
		int i = 0;
		int rubbish = 0;
		switch(type){
		case LIGHT:
			break;
		case PLUG:
			i = PLUG_RESPONSE_IP_RUBBISH_LEN;
			rubbish = PLUG_RESPONSE_IP_RUBBISH_LEN;
			break;
		case TEMPERATURE:
			i = TEMHUM_RESPONSE_IP_RUBBISH_LEN;
			rubbish = TEMHUM_RESPONSE_IP_RUBBISH_LEN;
			break;
		default:
			break;
		}
//		int i = RESPONSE_IP_RUBBISH_LEN;
		int j = i;
		while('0'<=data[i]&&data[i]<='9'||data[i]=='.'){
			i++;
		}
//		return new String(data,j,i-RESPONSE_IP_RUBBISH_LEN);
		return new String(data,j,i-rubbish);
	}
	
	// filter BSSID from the response
	// "I'm Plug.98:fe:34:77:ce:00 192.168.4.1"
	private String filterBSSID(byte[] data,TYPE type){
		int rubbish = 0;
		switch(type){
		case LIGHT:
			break;
		case PLUG:
			rubbish = PLUG_RESPONSE_BSSID_RUBBISH_LEN;
			break;
		case TEMPERATURE:
			rubbish = TEMHUM_RESPONSE_BSSID_RUBBISH_LEN;
			break;
		default:
			break;
		}
//		return new String(data,RESPONSE_BSSID_RUBBISH_LEN,RESPONSE_BSSID_LEN);
		return new String(data,rubbish,RESPONSE_BSSID_LEN);
	}
	
	private TYPE filterType(byte[] data){
		String dataStr = new String(data);
		// try Plug
		if(dataStr.substring(4, 4+PLUG.length()).equals(PLUG)){
			return TYPE.PLUG;
		}
		// try TemHum
		if(dataStr.substring(4, 4+TEMHUM.length()).equals(TEMHUM)){
			return TYPE.TEMPERATURE;
		}
		return null;
	}
	
	private synchronized void socket() {

		// clear the response list
		responseList = new ArrayList<IOTAddress>();
		
		try {
			Log.d(TAG+":"+taskName, "socket() entrance:");
			
			DatagramPacket pack = new DatagramPacket(data.getBytes(), data.length(),
					inetAddress, IOT_PORT);
			Log.d(TAG+":"+taskName, "send socket");
			socket.send(pack);
			pack.setData(buf_receive);
			Log.d(TAG+":"+taskName, "socket receive...");
			do {
				socket.receive(pack);
				Log.d(TAG+":"+taskName, "one socket received");
				Log.d(TAG+":"+taskName, new String(pack.getData(), pack.getOffset(),
						pack.getLength()));
				
				TYPE type = filterType(pack.getData());
				if(type==null){
					Log.e(TAG, "type is null, we don't support the device type.");
					continue;
				}
				
				InetAddress responseAddr = InetAddress.getByName(filterIpAddress(pack.getData(),type));
				Log.d(TAG,pack.getData().toString());
				String responseBSSID = filterBSSID(pack.getData(),type);
				Log.d(TAG+":"+taskName, "responseAddr = " + responseAddr +",responseBSSID = " + responseBSSID);
				// add one response to the response list
//				responseList.add(response);
				IOTAddress iotAddress = new IOTAddress(responseBSSID,responseAddr);
				iotAddress.setType(type);
				responseList.add(iotAddress);
			} while (isMulticast);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.d(TAG+":"+taskName, "SocketException");
//			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			Log.d(TAG+":"+taskName, "UnknownHostException");
//			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG+":"+taskName, "IOException");
//			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
				Log.d(TAG+":"+taskName, "socket closed in finally");
			} else {
				Log.e(TAG+":"+taskName, "socket is null");
			}
		}
	}

	@Override
	protected void doAfterFailed() {
		// TODO Auto-generated method stub
		Log.d(TAG+":"+taskName, " doAfterFailed(): the failed reason is: " + reason);
		while (socket != null&&!socket.isClosed())
			socket.close();
		Log.d(TAG+":"+taskName, "the socket is closed in doAfterFailed().");
	}

	@Override
	public synchronized List<IOTAddress> getResult() {
		// TODO Auto-generated method stub
		return responseList;
	}
}
