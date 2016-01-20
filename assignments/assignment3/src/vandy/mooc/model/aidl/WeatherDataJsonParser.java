package vandy.mooc.model.aidl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import vandy.mooc.model.aidl.WeatherData.Main;
import vandy.mooc.model.aidl.WeatherData.Sys;
import vandy.mooc.model.aidl.WeatherData.Weather;
import vandy.mooc.model.aidl.WeatherData.Wind;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of WeatherData objects that contain this data.
 */
public class WeatherDataJsonParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG = this.getClass().getCanonicalName();

    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<WeatherData> parseJsonStream(InputStream inputStream)
        throws IOException {

        // TODO -- you fill in here.
    	// Create a JsonReader for the inputStream.
        try (JsonReader reader =
             new JsonReader(new InputStreamReader(inputStream, "UTF-8"))) {

            return parseJsonWeatherDataArray(reader);
        }
    }

    /**
     * Parse a Json stream and convert it into a List of WeatherData objects.
     */
    public List<WeatherData> parseJsonWeatherDataArray(JsonReader reader)
        throws IOException {
        
		 // Unsure if this can ever begin with an array "[".
		 // It can definitely begin with an object "{".
		 if (reader.peek() == JsonToken.BEGIN_ARRAY) {
			 reader.beginArray();
			 
			 // If no weather data, return null
			 if (reader.peek() == JsonToken.END_ARRAY)
			     return null;
		 } else {
			 reader.beginObject();
		 }
		
		 // Create a WeatherData object for each element in the Json array.
		 List<WeatherData> weatherData = new ArrayList<WeatherData>();
		 
		 while (reader.hasNext()) {
			 WeatherData weatherDataObj = parseJsonWeatherData(reader);
			 weatherData.add(weatherDataObj);
		 }
		
		 if (reader.peek() == JsonToken.END_ARRAY) {
			 reader.endArray();
		 } else {
			 reader.endObject();
		 }
		 
		 return weatherData;
    }

    /**
     * Parse a Json stream and return a WeatherData object.
     * { 
     * 	 "coord":{ "lon":-86.78, "lat":36.17 },
	 * 	 "sys":{ "message":0.0138, "country":"United States of America", "sunrise":1431427373,
	 * 			 "sunset":1431477841 },
	 * 	 "weather":[ { "id":802, "main":"Clouds", "description":"scattered clouds", "icon":"03d" } ],
	 * 	 "base":"stations",
	 * 	 "main":{ "temp":289.847, "temp_min":289.847, "temp_max":289.847,
	 * 			  "pressure":1010.71, "sea_level":1035.76, "grnd_level":1010.71, "humidity":76},
	 * 	 "wind":{ "speed":2.42, "deg":310.002 },
	 * 	 "clouds":{ "all":36 },
	 * 	 "dt":1431435983, "id":4644585, "name":"Nashville", "cod":200
	 * }
     */
    public WeatherData parseJsonWeatherData(JsonReader reader) throws IOException {

        // TODO -- you fill in here.
    	WeatherData weatherData = new WeatherData();
    	
    	while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
            case WeatherData.weather_JSON:
            	if (reader.peek() == JsonToken.BEGIN_ARRAY) {
	            	Log.d(TAG, "reading weathers field");
	                weatherData.setWeathers(parseWeathers(reader));
            	}
                break;
                
            case WeatherData.main_JSON:
                Log.d(TAG, "reading main field");
                weatherData.setMain(parseMain(reader));
                break;
                
            case WeatherData.wind_JSON:
            	Log.d(TAG, "reading wind field");
            	weatherData.setWind(parseWind(reader));
            	break;
            	
            case WeatherData.sys_JSON:
            	Log.d(TAG, "reading sys field");
            	weatherData.setSys(parseSys(reader));
            	break;
            	
            case WeatherData.name_JSON:
            	Log.d(TAG, "reading name field");
            	weatherData.setName(reader.nextString());
            	break;
            
            case WeatherData.dt_JSON:
            	Log.d(TAG, "reading date field");
            	weatherData.setDate(reader.nextLong());
            	break;
            	
            case WeatherData.cod_JSON:
            	Log.d(TAG, "reading cod field");
            	weatherData.setCod(reader.nextLong());
            	break;
            	
            default:
            	Log.d(TAG, "Skipping: " + name + " field");
                reader.skipValue();
                break;
            }
        }
    	
    	return weatherData;
    }
    
    /**
     * Parse a Json stream and return a List of Weather objects.
     * ex: weather":[ { "id":802, "main":"Clouds", "description":"scattered clouds", "icon":"03d" } ]
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
    	List<Weather> weathers = new ArrayList<Weather>();
    	
    	reader.beginArray();
    	
    	while (reader.peek() != JsonToken.END_ARRAY) {
    		weathers.add(parseWeather(reader));
    	}
    	
    	reader.endArray();
    	
    	return weathers;
    }

    /**
     * Parse a Json stream and return a Weather object.
     * ex: { "id":802, "main":"Clouds", "description":"scattered clouds", "icon":"03d" }
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
    	Weather weather = new Weather();
    	
    	reader.beginObject();
    	
    	while (reader.peek() != JsonToken.END_OBJECT) {
    		
    		String name = reader.nextName();
    		switch(name) {
    		case Weather.id_JSON:
    			weather.setId(reader.nextLong());
    			break;
    		case Weather.main_JSON:
    			weather.setMain(reader.nextString());
    			break;
    		case Weather.description_JSON:
    			weather.setDescription(reader.nextString());
    			break;
    		case Weather.icon_JSON:
    			weather.setIcon(reader.nextString());
    			break;
    		default:
    			Log.d(TAG, "Did not parse: " + name + " in parseWeather()");
    			reader.skipValue();
    			break;
    		}
    	}

    	reader.endObject();
    	return weather;
    }

    /**
     * Parse a Json stream and return a Main Object.
     * ex: "main":{ "temp":289.847, "temp_min":289.847, "temp_max":289.847,
     * 			  "pressure":1010.71, "sea_level":1035.76, "grnd_level":1010.71, "humidity":76}
     * 
     * Only worry about temp, pressure and humidity.
     */
    public Main parseMain(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
    	Main main = new Main();
    	reader.beginObject();
    	
    	while (reader.peek() != JsonToken.END_OBJECT) {
    		
    		String name = reader.nextName();
    		switch(name) {
    		case Main.temp_JSON:
        		main.setTemp(reader.nextDouble());
        		break;
    		case Main.pressure_JSON:
            	main.setPressure(reader.nextDouble());
            	break;
    		case Main.humidity_JSON:
            	main.setHumidity(reader.nextLong());
            	break;
            //skip the values below
    		case Main.tempMin_JSON:
    		case Main.tempMax_JSON:
    		case Main.seaLevel_JSON:
    		case Main.grndLevel_JSON:
    		default:
    			Log.d(TAG, "Did not parse: " + name + " in parseMain()");
    			reader.skipValue();
    			break;
        	}
    	}
    	
    	reader.endObject();
    	return main;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     * ex: "wind":{ "speed":2.42, "deg":310.002 }
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
    	Wind wind = new Wind();
    	reader.beginObject();
    	
    	while (reader.peek() != JsonToken.END_OBJECT) {
    		
    		String name = reader.nextName();
    		switch(name) {
    		case Wind.speed_JSON:
    			wind.setSpeed(reader.nextDouble());
        		break;
    		case Wind.deg_JSON:
    			wind.setDeg(reader.nextDouble());
            	break;
    		default:
    			Log.d(TAG, "Did not parse: " + name + " in parseWind()");
    			reader.skipValue();
    			break;
        	}
    	}
    	
    	reader.endObject();
    	return wind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     * ex: "sys":{ "message":0.0138, "country":"United States of America", "sunrise":1431427373, "sunset":1431477841 }
     */
    public Sys parseSys(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
    	Sys sys = new Sys();
    	reader.beginObject();
    	
    	while (reader.peek() != JsonToken.END_OBJECT) {
    		
    		String name = reader.nextName();
    		switch(name) {
    		case Sys.message_JSON:
    			sys.setMessage(reader.nextDouble());
    			break;
    		case Sys.country_JSON:
    			sys.setCountry(reader.nextString());
    			break;
    		case Sys.sunrise_JSON:
    			sys.setSunrise(reader.nextLong());
    			break;
    		case Sys.sunset_JSON:
    			sys.setSunset(reader.nextLong());
    			break;
    		default:
    			Log.d(TAG, "Did not parse: " + name + " in parseSys()");
    			reader.skipValue();
    			break;
    		}
    	}
    	
    	reader.endObject();
    	return sys;
    }
}
