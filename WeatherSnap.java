import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherSnap {
    private static final String API_KEY = "32ec4a5e0eddf8c5d5a2edee6c85471c";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and set up the GUI
            JFrame frame = new JFrame("WeatherSnap");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setLayout(new FlowLayout());

            JLabel label = new JLabel("Enter the city name: ");
            JTextField textField = new JTextField(20);
            JButton submitButton = new JButton("Submit");

            // Add action listener to the submit button
            submitButton.addActionListener(e -> {
                String city = textField.getText();
                try {
                    String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
                    String jsonResponse = getApiResponse(apiUrl);
                    displayWeatherInfo(jsonResponse);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            // Add components to the frame
            frame.getContentPane().add(label);
            frame.getContentPane().add(textField);
            frame.getContentPane().add(submitButton);

            // Set frame properties
            frame.setSize(400, 200);
            frame.setVisible(true);
        });
    }

    private static String getApiResponse(String apiUrl) throws IOException {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(apiUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Set the request method
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    return response.toString();
                }
            } else {
                throw new IOException("API request failed. Response Code: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void displayWeatherInfo(String jsonResponse) {
        try {
            // Use Gson to parse JSON
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Extract relevant information from the JSON object
            JsonObject main = jsonObject.getAsJsonObject("main");
            double temperatureKelvin = main.getAsJsonPrimitive("temp").getAsDouble();
            int humidity = main.getAsJsonPrimitive("humidity").getAsInt();
            double pressure = main.getAsJsonPrimitive("pressure").getAsDouble();

            // Convert temperature to Celsius
            double temperatureCelsius = temperatureKelvin - 273.15;

            // Extract additional information
            JsonObject wind = jsonObject.getAsJsonObject("wind");
            double windSpeed = wind.getAsJsonPrimitive("speed").getAsDouble();

            JsonObject weatherArray = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
            String description = weatherArray.getAsJsonPrimitive("description").getAsString();

            // Visibility (in meters)
            double visibility = jsonObject.getAsJsonPrimitive("visibility").getAsDouble();

            // Sunrise and Sunset times
            JsonObject sys = jsonObject.getAsJsonObject("sys");
            long sunriseTimestamp = sys.getAsJsonPrimitive("sunrise").getAsLong() * 1000; // Convert to milliseconds
            long sunsetTimestamp = sys.getAsJsonPrimitive("sunset").getAsLong() * 1000; // Convert to milliseconds

            // Format timestamps as HH:mm:ss
            String sunriseTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(sunriseTimestamp));
            String sunsetTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(sunsetTimestamp));

            // Display relevant weather information
            String weatherInfo = String.format("Temperature: %.2f°C\nHumidity: %d%%\nPressure: %.2f hPa\nWind Speed: %.2f m/s" +
                            "\nVisibility: %.2f meters\nDescription: %s\nSunrise: %s\nSunset: %s",
                    temperatureCelsius, humidity, pressure, windSpeed, visibility, description, sunriseTime, sunsetTime);

            JOptionPane.showMessageDialog(null, weatherInfo, "Weather Information", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error parsing JSON response", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
