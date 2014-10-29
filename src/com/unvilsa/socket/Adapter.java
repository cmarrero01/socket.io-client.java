/**
 * Adapter to manage connection with socket
 * 
 * @class Adapter
 * @author Claudio A. Marrero
 */
package com.unvilsa.socket;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.emitter.Emitter.Listener;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.Options;
import com.github.nkzawa.socketio.client.Socket;

public class Adapter {

	/**
	 * Instance of socket connection
	 * 
	 * @property socket
	 */
	public Socket socket;
	
	/**
	 * Flag for check if socket is connect or not.
	 * 
	 * @property _isConnected
	 */
	public boolean _isConnected = false;
	
	/**
	 * Flag for check the reconnection of socket.
	 * 
	 * @property _isReConnected
	 */
	public boolean _isReConnected = false;
	
	/**
	 * When the event_message_error is fired, put this flag on true.
	 * 
	 * @property _isEventError
	 */
	public boolean _isEventError = false;
	
	/**
	 * The last verb that has been emited
	 * 
	 * @property lastVerb
	 * @private
	 */
	private String lastVerb = null;
	
	/**
	 * The last params that has been emited to the server.
	 * 
	 * @property lastParams
	 * @private
	 */
	private JSONObject lastParams = null;
	
	/**
	 * Last ack that has sendit to the server.
	 * 
	 * @property lastCallback
	 * @private
	 */
	private Callback lastCallback = null;
	
	/**
	 * Make the connection with the server
	 * 
	 * @method Connect
	 */
	public void Connect(){
		
		if(_isConnected){
			return;
		}
		
		try{
        	
			Options opts = new IO.Options();
        	opts.port = 9000;
        	
        	socket = IO.socket("http://192.168.0.2:9000",opts);
        	socket.connect();
        	
        	OffListener();
        	OnConnect();
        	
        } catch (Exception e) {
        	Log.e("SOCKET", "Conextion expection", e);
        }
	}
	
	/**
	 * Listener of event connect
	 * 
	 * @property EVENT_CONNECT
	 * @private
	 */
	private Listener EVENT_CONNECT = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = true;
			if(lastVerb != null){
				Emit(lastVerb, lastParams, lastCallback);
			}
		}
    };
    
    /**
	 * Listener of event connect error
	 * 
	 * @property EVENT_CONNECT_ERROR
	 * @private
	 */
    private Listener EVENT_CONNECT_ERROR = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;  	  
		}
    };
    
    /**
	 * Listener of event connect time out
	 * 
	 * @property EVENT_CONNECT_TIMEOUT
	 * @private
	 */
    private Listener EVENT_CONNECT_TIMEOUT = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;  	  
		}
    };
    
    /**
	 * Listener of event disconnect
	 * 
	 * @property EVENT_DISCONNECT
	 * @private
	 */
    private Listener EVENT_DISCONNECT = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;  	  
			_isReConnected = false;
		}
    };
    
    /**
	 * Listener of event error
	 * 
	 * @property EVENT_ERROR
	 * @private
	 */
    private Listener EVENT_ERROR = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isEventError = true;
		}
    };
    
    /**
	 * Listener of event message
	 * 
	 * @property EVENT_MESSAGE
	 * @private
	 */
    private Listener EVENT_MESSAGE = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isEventError = false;  	  
		}
    };
    
    /**
	 * Listener of event reconnected
	 * 
	 * @property EVENT_RECONNECT
	 * @private
	 */
    private Listener EVENT_RECONNECT = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			_isConnected = true;
			_isReConnected = true;  	  
		}
    };
    
    /**
	 * Listener of event reconnect time out
	 * 
	 * @property EVENT_RECONNECT_ATTEMPT
	 * @private
	 */
    private Listener EVENT_RECONNECT_ATTEMPT = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;
			_isReConnected = false; 	  
		}
    };
    
    /**
	 * Listener of event reconnect error
	 * 
	 * @property EVENT_RECONNECT_ERROR
	 * @private
	 */
    private Listener EVENT_RECONNECT_ERROR = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;
			_isReConnected = false; 
		}
    };
    
    /**
	 * Listener of event reconnect failed
	 * 
	 * @property EVENT_RECONNECT_FAILED
	 * @private
	 */
    private Listener EVENT_RECONNECT_FAILED = new Emitter.Listener() {
		@Override
		public void call(Object... args) {
			_isConnected = false;
			_isReConnected = false;  	
		}
    };
    
    /**
	 * Listener of event reconnecting
	 * 
	 * @property EVENT_RECONNECTING
	 * @private
	 */
    private Listener EVENT_RECONNECTING = new Emitter.Listener() {
		@Override
		public void call(Object... args) {       	  
			_isConnected = false;
			_isReConnected = false; 	  
		}
    };
    
	/**
	 * Listen connection, and events handlers
	 * 
	 * @method OnConnect
	 */
	public void OnConnect(){
		socket.on(Socket.EVENT_CONNECT, EVENT_CONNECT);	
		socket.on(Socket.EVENT_CONNECT_ERROR, EVENT_CONNECT_ERROR);
		socket.on(Socket.EVENT_CONNECT_TIMEOUT, EVENT_CONNECT_TIMEOUT);         
		socket.on(Socket.EVENT_DISCONNECT, EVENT_DISCONNECT);
		socket.on(Socket.EVENT_ERROR, EVENT_ERROR);	
		socket.on(Socket.EVENT_MESSAGE, EVENT_MESSAGE);
		socket.on(Socket.EVENT_RECONNECT, EVENT_RECONNECT);	         
		socket.on(Socket.EVENT_RECONNECT_ATTEMPT, EVENT_RECONNECT_ATTEMPT);
		socket.on(Socket.EVENT_RECONNECT_ERROR, EVENT_RECONNECT_ERROR);
		socket.on(Socket.EVENT_RECONNECT_FAILED, EVENT_RECONNECT_FAILED);
		socket.on(Socket.EVENT_RECONNECTING, EVENT_RECONNECTING);		 
	}
	
	/**
	 * Listen connection, and events handlers
	 * 
	 * @method OnConnect
	 */
	public void OffListener(){
		socket.off(Socket.EVENT_CONNECT, EVENT_CONNECT);	
		socket.off(Socket.EVENT_CONNECT_ERROR, EVENT_CONNECT_ERROR);
		socket.off(Socket.EVENT_CONNECT_TIMEOUT, EVENT_CONNECT_TIMEOUT);         
		socket.off(Socket.EVENT_DISCONNECT, EVENT_DISCONNECT);
		socket.off(Socket.EVENT_ERROR, EVENT_ERROR);	
		socket.off(Socket.EVENT_MESSAGE, EVENT_MESSAGE);
		socket.off(Socket.EVENT_RECONNECT, EVENT_RECONNECT);	         
		socket.off(Socket.EVENT_RECONNECT_ATTEMPT, EVENT_RECONNECT_ATTEMPT);
		socket.off(Socket.EVENT_RECONNECT_ERROR, EVENT_RECONNECT_ERROR);
		socket.off(Socket.EVENT_RECONNECT_FAILED, EVENT_RECONNECT_FAILED);
		socket.off(Socket.EVENT_RECONNECTING, EVENT_RECONNECTING);		 
	}
	
	/**
	 * Make a request to the server
	 * 
	 * @method Emit
	 */
	public void Emit(String verb, JSONObject params, Callback callback){
		
		if(!_isConnected){
			
			lastVerb = verb;
			lastParams = params;
			lastCallback = callback;

			Connect();
			return;
		}
		
		final Callback thisCallback = callback;
		
		Ack call = new Ack(){
			
			@Override
			public void call(Object... args) {
			 
				if(!CheckResults(args)){
					Log.e("SOCKET","Error");
					thisCallback.OnError(args);
					return;
				}
					  
				lastVerb = null;
				lastParams = null;
				lastCallback = null;
				
				thisCallback.OnFinished((JSONObject)args[0]);
			}   		
        };
        
		socket.emit(verb, params, call);
	}
	
	/**
	 * Check the results to manage problems.
	 * 
	 * @method CheckResults
	 */
	private boolean CheckResults(Object... args){
					
		if(args == null){
			return false;
		}
		
		if(args.length <= 0){
			return false;
		}
		
		JSONObject data = (JSONObject)args[0];
		
		if(data == null){
			return false;
		}
		
		try {
			if(data.getInt("code") != 200){
				return false;
			}
		} catch (JSONException e) {
			return false;
		}
		
		return true;
	}
}
