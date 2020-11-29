# WAQI

A SmartThings virtual device type handler for retrieving air quality data from the [World Air Quality Index](https://waqi.info/). It is designed to be used with Automations in the new v3 app.

## Setup

1. You will need an API key (token) from the World Air Quality Index project.
    1. Fill the [token request form](https://aqicn.org/data-platform/token/)
    1. Follow the instructions in the email to confirm the request and get your token
    1. Your token should look something like `0851936473bf458ebc2a1b36acc8e3a2a850bf45`

1. In [SmartThings Groovy IDE](https://graph.api.smartthings.com/), create a device type handler in the **My Device Handlers** section, either via [GitHub integration](http://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html#setup), or by pasting the code into the **Create New Device Handler** > **From Code** window

1. Go to the **My Devices** section and click **New Device** to create a new device using the new device type handler:
    1. **Name** - enter a readable name such as `Air Quality Sensor`
    1. **Device Network Id** - enter any short string of characters not already in use by another device, such as `waqi-virtual-sensor`
    1. **Type** - pick **WAQI Virtual Sensor** from the dropdown
    1. **Version** - Published
    1. **Location** - Pick your location from the dropdown
    1. **Hub** - Pick your hub from the dropdown
    1. Click **Create**

1. In the **Preferences** section of the device page of the new device, click edit:
    1. Fill the WAQI token in the **API Key** field
    1. The device will use your hub's location by default, but you can override that by filling in the latitude and longitude manually in their respective fields
    1. Click **Save**

## Usage

The device type handler is designed for Automations in the new v3 app. Since Automations does not support air quality as a measurement, the device type handler divides the air quality index by 10, then sends it as temperature measurement (which has a range of -20 to 50).

1. In the SmartThings app, click **Automations** in the hamburger menu
1. Click <kbd>+</kbd> to add an automation, click **Add condition** in the **If** section, then select **Device status**
1. Select the Air Quality Sensor you created in the SmartThings IDE, then select **Temperature** as the condition
1. Fill in the target AQI in the **Temperature** field. Keep in mind that the AQI sends by the device has been divided by 10. So if your target AQI is 101, fill in `10.1`. Then select either **Equal or above** or **Equal or below** as needed
1. Add additional **If** conditions as needed, as add actions to trigger when the target AQI is met in the **Then** section
