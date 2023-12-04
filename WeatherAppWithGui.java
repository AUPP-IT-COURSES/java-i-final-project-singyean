import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherAppWithGui extends Application {
    private static final String API_KEY = "32ec4a5e0eddf8c5d5a2edee6c85471c";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather App");

        // Create UI components
        Label cityLabel = new Label("Enter the city name:");
        TextField cityInput = new TextField();
        Button fetchButton = new Button("Fetch Weather");

        VBox layout = new VBox(10); // 10 pixels spacing
        layout.getChildren().addAll(cityLabel, cityInput, fetchButton);

        // Set up event handler for the button
        fetchButton.setOnAction(e -> {
            String city = cityInput.getText();
            try {
                String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY;
                String jsonResponse = getApiResponse(apiUrl);
                displayWeatherInfo(jsonResponse);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(layout, 300, 150);
        primaryStage.setScene(scene);

        primaryStage.show();
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
                // Read the response using try-with-resources
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
            // Close the connection in the finally block
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
            double temperature = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("temp").getAsDouble();
            int humidity = jsonObject.getAsJsonObject("main").getAsJsonPrimitive("humidity").getAsInt();

            // Display relevant weather information
            System.out.println("Temperature: " + temperature);
            System.out.println("Humidity: " + humidity);
            // ... (display other relevant information)

            // For simplicity, print the information to the console. You can update this part to display information in the GUI.
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
        }
    }
}
