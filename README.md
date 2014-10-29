socket.io-client.java
=====================

An Android project with a library to manage socket connections, emits with ack and callbacks for views.

Example
========

First import the library:

  import com.unvilsa.socket.*;

Second on your MainActivity, instance the socket:

  com.unvilsa.socket.Adapter socket = new com.unvilsa.socket.Adapter();

And Last on your OnCreate or when you needed, make an emit with your Callback function:

  Callback test = new Callback() {
      @Override
      public void OnFinished(JSONObject data) {
      	Log.i("DATA",data.toString());
      }
      
      @Override
      public void OnError(Object... args) {
      	Log.i("DATA",args.toString());
      }
  };
  
  socket.Emit("test", new JSONObject(), test);

That is All.
