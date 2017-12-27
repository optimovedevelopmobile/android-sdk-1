# Optimove Android SDK Integration

Optimove's Android SDK for native apps is a general-purpose suite of tools that is built to support multiple Optimove products.<br>
The SDK is designed with careful consideration of the unpredicted behavior of the Mobile user and Mobile environment, taking into account limited to no networking and low battery or memory limitations.<br>
While supporting a constantly rising number of products, the SDK's API is guaranteed to be small, coherent and clear.

## Getting Started

### Prerequisites

A **_Tenant token_** is provided during the initial integration with Optimove. That token must be available to the application's developer before installing and integrating the SDK into the app.<br>
The tenant token contains the following information:
* `initEndPointUrl` - The URL where the **_tenant configurations_** reside
* `token` - The actual token, unique per tenant
* `config name` - The version of the desired configuration

### Installation

#### Adding the Optimove Repository

1. Open the **project's** `build.gradle` file (located under the application's _root folder_).
2. Under `allprojects`, locate the `repositories` object.
3. Add the **_optimove-sdk_** repository:
```javascript
maven {
  url  "https://mobiussolutionsltd.bintray.com/optimove-sdk"
}
```
___

#### Downloading the SDK
1. Open the **app's** `build.gradle` file (located under the application's _app module folder_).
2. Under `dependencies`, add the following:
```javascript
compile 'com.optimove.sdk:optimove-sdk:1.0.0'
```

### Running the SDK
> Skip this part if the application already has a working subclass of the `Application` object.

Create a new subclass of `Application` and override the `onCreate` method. Finally add the new object's name to the **_manifest_** under the `application` **_tag_** as the `name` **_attribute_**.
```java
public class MyApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
  }
}
```
```xml
<application
  android:name=".MyApplication">
  .
  .
  .
</application>
```
___

Using the provided **_Tenant token_**, a `Context` instance and a flag indicating whether the hosting application has its own **_Firebase SDK_** create a new `TenantInfo` object, initialize the `Optimove` singleton via `Optimove.configure`. The initialization must be called **as soon as possible**, preferably after the call to `super.onCreate()` in the `onCreate` callback.
```java
public class MyApplication extends Application {

  @Override
  public void onCreate() {

    super.onCreate();
    TenantInfo tenantInfo = new TenantInfo("https://optimove.mobile.demo/sdk-configs/", //The initEndPointUrl
                                              "abcdefg12345678", //The token
                                              "myapp.android.1.0.0", //The version
                                              false); //Has Firebase
    Optimove.configure(this, tenantInfo);
  }
}
```

## State Registration

The SDK initialization process occurs asynchronously, off the `Main UI Thread`.<br>
Before calling the Public API methods, make sure that the SDK has finished initialization by calling the `registerStateListener` method with an instance of `OptimoveStateListener`.<br>
>If the object implementing the `OptimoveStateListener` is an instance member of an `Activity` or the `Activity` itself, **_always_** unregister that object at the `onStop()` callback to prevent memory leaks.<br>

```java
public class MainActivity extends AppCompatActivity implements OptimoveStateListener {

  @Override
  protected void onStart() {

    super.onStart();
    Optimove.getInstance().registerStateListener(this);
  }

  @Override
  protected void onStop() {

    super.onStop();
    Optimove.getInstance().unregisterStateListener(this);
  }

  @Override
  public void onConfigurationStarted() {
    
  }

  @Override
  public void onConfigurationSucceed() {

    //Do any call to the Optimove SDK safely in here
  }

  @Override
  public void onConfigurationFailed(OptimoveStateListener.Error... errors) {

    Log.d("OptimoveSDK", Arrays.deepToString(errors));
  }
}
```

## Analytics
Using the Optimove's Android SDK, the hosting application can track events with analytical significance.<br>
These events can range from basic _**`Screen Visits`**_ to _**`Visitor Conversion`**_ and _**`Custom Events`**_ defined in the `Optimove Configuration`.<br>

### Set User ID
Once the user has downloaded the application and the SDK run for the first time, the user is considered a _Visitor_, an unknown user.<br>
At a certain point the user will authenticate and will become identified by a known `PublicCustomerId`, then the user is considered _Customer_.<br>
Pass that `CustomerId` to the `Optimove` singleton as soon as the authentication occurs.<br>
>Note: the `CustomerId` is usually delivered by the Server App that manages customers, and is integrated with Optimove Data Transfer.<br>
>
>Due to its high importance, `setUserId(String userId)` can be called at any given moment, regardless of the SDK's state.
```java
Optimove.getInstance().setUserId("a-unique-user-id");
```
### Report Screen Event
To target which screens the user has visited in the app, create a new `SetScreenVisitEvent` event with the appropriate `screenName` and pass it to the `Optimove` singleton.
```java
@Override
public void onConfigurationSucceed() {

  Optimove.getInstance().reportEvent(new SetScreenVisitEvent("Employees List"));
}
```
### Report Custom Event
To create a _**`Custom Event`**_ (not provided as a predefined event by Optimove) implement the `OptimoveEvent interface`.<br>
The interface defines 2 methods:
1. `String getName()` - Declares the custom event's name
2. `Map<String, Object> getParameters()` - Defines the custom event's parameters.
Then send that event through the `reportEvent` method of the `Optimove` singleton.
>Note: Any _**`Custom Event`**_ must be declared in the _Tenant Configurations_. <br>
_**`Custom Event`**_ reporting is only supported when OptiTrack Feature is enabled.

```java

public class MainActivity extends AppCompatActivity implements OptimoveStateListener {

  public void onClick(View view) {
    MyCustomEvent event = new MyCustomEvent(12, "diamond");
    Optimove.getInstance().reportEvent(event);
  }
}

class MyCustomEvent implements OptimoveEvent {

  private int prizeValue;
  private String itemOfInterest;

  public MyCustomEvent(int prizeValue, String itemOfInterest) {

    this.prizeValue = prizeValue;
    this.itemOfInterest = itemOfInterest;
  }

  @Override
  public String getName() {

    return "my_custom_event";
  }

  @Override
  public Map<String, Object> getParameters() {

    Map<String, Object> params = new HashMap<>();
    params.put("prize_value", prizeValue);
    params.put("item_of_interest", itemOfInterest);
    return params;
  }
}
```

## Optipush
Optipush is _Optimove_'s in-house push campaigns execution channel. The SDK is in charge of receiving **_push messages_**, presenting **_notification UI_** and tracking the user's responses all by itself, without a _single_ line of code.<br>
There are however, 2 use cases in which the developer needs to add some code: Deep Linking and Testing Optipush Templates.

### Optipush Deep Linking
Other than _UI attributes_, an **_Optipush Notification_** can contain metadata that can lead the user to a specific screen within the hosting application, alongside custom (screen specific) data.<br>
To support deep linking, update application's `manifest.xml` file to reflect which screen can be targeted. Each `Activity` the can be targeted must have the following _**`intent-filter`**_:

```xml
<intent-filter>
  <action android:name="android.intent.action.VIEW"/>

  <category android:name="android.intent.category.DEFAULT"/>
  <category android:name="android.intent.category.BROWSABLE"/>

  <data
    android:host="replace.with.the.app.package" 
    android:pathPrefix="/replace_with_a_custom_screen_name"
    android:scheme="http"/>
</intent-filter>
```

To support **_custom deep linking data_** pass a `LinkDataExtractedListener` to an instance of `DynamicLinkHandler` inside the targeted `Activity`.<br>
The `dynamicLinkHandler` calls either the _**`onDataExtracted(Map<String, String> data)`**_ with the deep linking data in case of successful extraction, or the _**`onErrorOccurred(LinkDataError error)`**_ if any error occurred. 

```java
public class MyTargetedActivity extends AppCompatActivity implements LinkDataExtractedListener {

  public static final String EMPLOYEE_EXTRA_KEY = "employee";

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_employee_page);
    new DynamicLinkHandler(this).extractLinkData(this);
  }

  @Override
  public void onDataExtracted(Map<String, String> data) {

    //Do any custom behavior according to the provided data Map
  }

  @Override
  public void onErrorOccurred(LinkDataError error) {

    //Handle errors in any way fitted
  }
}
```

### Test Optipush Templates
It might be desired to test an **_Optipush Template_** on an actual device before creating an **_Optipush Campaign_**. To enable _"test campaigns"_ on one or more devices, call the _**`Optimove.getInstance().startTestMode();`**_ method. To stop receiving _"test campaigns"_ call the _**`Optimove.getInstance().stopTestMode();`**_.
```java
public class MainActivity extends AppCompatActivity implements OptimoveStateListener {

  public void startTestModeClickListener(View view) {
    
    Optimove.getInstance().startTestMode();
  }

  @Override
  protected void onStop() {

    super.onStop();
    //Can be called even if test mode was not really started
    Optimove.getInstance().stopTestMode();
  }
}
```

## Special Use Cases

### The Hosting Application Uses Firebase

#### Installation

The _Optimove Android SDK_ is dependent upon the _Firebase Android SDK_.<br>
If the application into which the **_Optimove Android SDK_** is integrated already uses **_Firebase SDK_** or has a dependency with **_Firebase SDK_**, a build conflict might occur, and even **Runtime Exception**, due to backwards compatibility issues.<br>
Therefor, it is highly recommended to match the application's **_Firebase SDK version_** to Optimove's **_Firebase SDK version_** as detailed in the following table.

| Optimove SDK Version | Firebase SDK Version |
| -------------------- | -------------------- |
| 1.0.0                | 11.6.0               |
___

#### <br> Multiple FirebaseMessagingServices
When the hosting app also utilizes Firebase Cloud Messaging and implements the **_`FirebaseMessagingService`_** Android's **_Service Priority_** kicks in. Therefor, the app developer **must** call explicitly to the `OptipushMessagingHandler`.

```java
public class MyMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        new OptipushMessagingHandler(this).onMessageReceived(remoteMessage);
    }
}
```
___

#### <br> SDK Integration Note

Usually when using **_Firebase_** it takes care of its own initialization. However, there are cases in which it is desired to initialize the **_default FirebaseApp_** manually. <br>
In these special cases, be advised that calling the `Optimove.configure` before the `FirebaseApp.initializeApp` leads to a `RuntimeException` since the **_default FirebaseApp_** must be initialized before any other **_secondary FirebaseApp_**, which in this case would be triggered by the _Optimove Android SDK_.