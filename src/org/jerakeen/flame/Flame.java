package org.jerakeen.flame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import javax.jmdns.*;

import android.util.Log;


public class Flame extends ListActivity implements ServiceTypeListener, ServiceListener {
	
	ArrayAdapter<String> arrayAdapter;
	Vector<JmDNS> resolvers;
	ArrayList<String> names;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
    	Log.v("flame", "started!");
    	
    	names = new ArrayList<String>();
	    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
	    setListAdapter(arrayAdapter);
	    getListView().setTextFilterEnabled(true);
	    
	    resolvers = new Vector<JmDNS>();
	    
		try {

			JmDNS jmdns = JmDNS.create( InetAddress.getByAddress( "android-phone", new byte[]{0,0,0,0} ) );
	        jmdns.addServiceTypeListener(this);
            Log.v("flame", "created servicelistener on " + jmdns.getInterface().getHostAddress());

	        ServiceInfo flameService = ServiceInfo.create("_flame._tcp.", "Flame", 0, "flame-android");
	        jmdns.registerService( flameService );

	        resolvers.addElement(jmdns);
		} catch (IOException e) {
	    	Log.e("flame", "IOException: " + e);
		}

		/*
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
	        while (interfaces.hasMoreElements()) {
	            NetworkInterface interf = interfaces.nextElement();
	            Log.v("flame", "listening on "+ interf.getDisplayName() );
	            Enumeration<InetAddress> addresses = interf.getInetAddresses();
	            if (!interf.getName().equals("lo") && addresses.hasMoreElements()) {
	                InetAddress address = addresses.nextElement();
	                Log.v("flame", "Listening on address " + address.toString());
	                JmDNS jmdns = JmDNS.create( address );
	                jmdns.addServiceTypeListener(this);

	    	        ServiceInfo flameService = ServiceInfo.create("_flame._tcp.", "Flame", 0, "flame-android");
	    	        jmdns.registerService( flameService );

	    	        resolvers.add( jmdns );
	                Log.v("flame", "added resolver "+jmdns);
	            }
	        }
		} catch (SocketException e) {
	    	Log.e("flame", "SocketException: " + e);
	    } catch (IOException e) {
	    	Log.e("flame", "IOException: " + e);
		}
		*/

    }

    public void serviceTypeAdded(ServiceEvent event) {
    	Log.v("flame", "new type: " +event.getType());
        final String type = event.getType();
        event.getDNS().addServiceListener( type, this );
    }

    public void serviceAdded(ServiceEvent event) {
    	Log.v("flame", "serviceAdded: "+event.getName());
        event.getDNS().requestServiceInfo(event.getType(), event.getName(), 0);
        String string = event.getType() + " - " + event.getName();
        arrayAdapter.add( string );
        arrayAdapter.notifyDataSetChanged();
	}

	public void serviceRemoved(ServiceEvent event) {
    	Log.v("flame", "serviceRemoved: "+event.getName());
	}

	public void serviceResolved(ServiceEvent event) {
    	Log.v("flame", "serviceResolved: "+event.getName());
	}


}

