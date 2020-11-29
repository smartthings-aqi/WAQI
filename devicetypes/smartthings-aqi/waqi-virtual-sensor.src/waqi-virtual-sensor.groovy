/**
 *  World Air Quality Index Virtual Sensor
 *
 *  https://github.com/smartthings-aqi/waqi
 *  
 *  MIT License
 *
 *  Copyright (c) 2020 SmartThings AQI
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
 
metadata
{
    definition (name: "WAQI Virtual Sensor", namespace: "smartthings-aqi", author: "SmartThings AQI")
    {
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Polling"

        command "refresh"
    }

    preferences
    {
        input name: "apiKey", type: "text", title: "API Key", required: true, description: "API key for WAQI"
        input name: "latitude", type: "number", title: "Latitude", required: false
        input name: "longitude", type: "number", title: "Longitudes", required: false
        input name: "about", type: "paragraph", element: "paragraph", title: "WAQI Virtual Sensor", description: "World Air Quality Index Virtual Sensor"
    }
}

def installed()
{
    runEvery1Minute(poll)
    poll()
}

def updated()
{
    poll()
}

def uninstalled()
{
    unschedule()
}

def poll()
{
    log.debug("Polling for air quality data for ${location.name} (${location.latitude}, ${location.longitude}).")

    if (apiKey)
    {
        // Use hub coordinates if user has not defined their own
        def _latitude = latitude ? latitude : location.latitude
        def _longitude = longitude ? longitude : location.longitude

        // Set up the WAQI API query
        def params = [uri: 'https://aqi.waqi.info/', path: "feed/geo:${_latitude};${_longitude}/", contentType: 'application/json', query: [format: 'application/json', token: apiKey]]

        try
        {
            httpGet(params) { resp ->
                if (resp.data.status == 'ok')
                {
                    log.debug("AQI for ${_latitude},${_longitude} retrieved: ${resp.data.data.aqi}.")
                    
                    // Dividing AQI by 10 to fit in the temperature range (-20 to 50) in Smart Things Automations
                    sendEvent(name: "temperature", value: resp.data.data.aqi / 10)
                }
                else
                {
                    log.error("Invalid API response received. Status: ${resp.data.status}.")
                    send(name: "reportingLocation", value: "Invalid API response received from WAQI")
                }
            }
        }
        catch (SocketTimeoutException e)
        {
            log.error("Connection to WAQI API timed out.")
            send(name: "reportingLocation", value: "Connection timed out while retrieving data from WAQI")
        }
        catch (e)
        {
            log.error("Could not retrieve WAQI data: ${e}")
            send(name: "reportingLocation", value: "Could not retrieve data: check API key in device settings")
        }
    }    
    else
    {
        log.warn("No WAQI API key specified.")
        send(name: "reportingLocation", value: "No WAQI API key specified in device settings")
    }
}

def refresh()
{
    poll()
}

def configure()
{
    poll()
} 
