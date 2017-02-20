# Multiscreen Interaction Framework

A framework to enable Multiscreen Interactions on Android devices, triggered by gestures. This framework was developed by Hochschule Mannheim as part of BMBF-project "SysPlace".

# Gestures

Gestures can be either *simple gestures* (performed on one device) or *synchronous gestures* (performed on two or more devices simultaneously).

Examples for simple gestures:
- Swipe
- Pinch / Spread
- Tap
- Double Tap
- Shake

Examples for synchronous gestures:
- Bump (bump two devices together)
- Stitch (a Swipe accross two screens)
- Shake (Shake two devices in one hand)
- Approach (approach one device with the other)
- Pinch (perform a pinch gesture spanning two screens)

# App Lifecycle

The framework follows a Multiscreen Interaction applicaton lifecycle that distinguishes four lifecycle events and state changes:

1. **Connect**: Connect two devices (usually by using a synchronous gesture such as a *Bump*)
2. **Select**: Select a file to be transfered (e.g. by performing a *DoubleTap* on the screen)
3. **Transfer**: Start transfer of a selected file (can be a simple or a synchronous gesture)
4. **Disconnect**: Disconnect devices

Each of those events can be triggered by registering a gesture for it (see App Setup).

For more information about the lifecycle, check [the project page](http://multiscreen-patterns.uxid.de/entwickler/)

# App Setup
## Manifest
Make your application an `hs_mannheim.gestureframework.model.InteractionApplication` application and register a `BluetoothParingService and a `BluetoothBroadcastReceiver` (needed for synchronous gesture detection) like this:

```Android
  <application
        android:name="hs_mannheim.gestureframework.model.InteractionApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <activity android:name=".ConnectedActivity" />

        <service
            android:name="hs_mannheim.gestureframework.connection.BluetoothPairingService"
            android:exported="false"
            android:label="Bluetooth Pairing Service">
        </service>

        <receiver
            android:name="hs_mannheim.gestureframework.connection.bluetooth.BluetoothBroadcastReceiver"
            android:enabled="true">
            <intent-filter>
                <action android:name="android.bluetooth.device.action.FOUND" />
            </intent-filter>
        </receiver>
</application>
```

The application needs to be an `="hs_mannheim.gestureframework.model.InteractionApplication` to enable global state tracking of lifecycle events changes. Also make sure to grant Bluetooth permissions:

```Android
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

Some `GestureDetectors` might need additional permissions.

## Build SysplaceContext in MainActivity
In `onCreate` of your MainActivity, register gesture detection for lifecycle events like this: 

```Android
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		
        ConfigurationBuilder builder = new ConfigurationBuilder(getApplicationContext(), this);
        builder
                .withBluetooth()
                .toConnect(builder.swipeLeftRight())
                .toSelect(builder.doubleTap())
                .toTransfer(builder.swipeUpDown())
                .toDisconnect(builder.syncBump())
                .select(Selection.Empty)
                .registerForLifecycleEvents(new ToastLifecycleListener(this))
                .registerPacketReceiver(this)
                .buildAndRegister();

        mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();
        mSysplaceContext.activate(this);
}
```

This will set up a new configuration that has the following mappings (needs to be done in two separate devices to work for synchronous gestures):

1. *Connect* is triggered by a [Stitch To Connect](http://multiscreen-patterns.uxid.de/patterns/stitch_to_connect.html)
2. *Select* is triggered by a **DoubleTap**
3. *Transfer* is triggered by a [Swipe To Give](http://multiscreen-patterns.uxid.de/patterns/swipe_to_give.html)
4. *Disconnect* is triggered by a [Bump](http://multiscreen-patterns.uxid.de/patterns/swipe_to_connect.html)

To check if permissions are set, an additional call to `SysplaceContext.activate(Activity)` is performed.

## Stop and resume activity
When an activity is stopped or resumed, the SysplaceContext should be informed:
```Android
@Override
protected void onResume() {
	super.onResume();
	Log.d(TAG, "onResume called");
	mSysplaceContext.applicationResumed();
	View revealView = findViewById(R.id.reveal_view);
	if(revealView.getVisibility() == View.VISIBLE) {
		enterReveal();
	}
}

@Override
protected void onStop() {
	super.onStop();
	Log.d(TAG, "onStop called");
	mSysplaceContext.applicationPaused();
}
```

## Subscribe to lifecycle events
To be notified when one of the lifecycle events was triggered by the configured gesture, an activity must implement the `ILifeCycleListener` interface and subscribe for events like this:
```Android
public class SomeActivity extends AppCompatActivity implements IViewContext, ILifecycleListener 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connected);

		mViewWrapper = new ViewWrapper(findViewById(R.id.imgView));

		mSysplaceContext = ((InteractionApplication) getApplicationContext()).getSysplaceContext();

		mSysplaceContext.registerForLifecycleEvents(this);
		mSysplaceContext.updateViewContext(LifecycleEvent.SELECT, this);
		mSysplaceContext.updateViewContext(LifecycleEvent.TRANSFER, this);
	}

	@Override
	public void onConnect() {
		// connect gesture fired
		// e.g. show Toast "connected"
	}

	@Override
	public void onSelect() {
		// select gesture fired
		// e.g. open file chooser
		mSysplaceContext.select(/* selected file*/);
	}

	@Override
	public void onTransfer() {
		// transfer gesture fired
		// e.g. show progress bar
	}

	@Override
	public void onDisconnect() {
		// disconnect gesture fired
		// e.g. show Toast "disconnected"
	}
}
```

The `ViewContext` must also be updated so that gesture detectors now which elements to monitor (e.g. the `SwipeDetector` has to monitor input events on elements in a view).

