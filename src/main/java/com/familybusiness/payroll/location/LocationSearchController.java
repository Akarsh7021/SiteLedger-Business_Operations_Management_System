package com.familybusiness.payroll.location;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class LocationSearchController {

    private static final String GOOGLE_PLACES_AUTOCOMPLETE_URL =
            "https://places.googleapis.com/v1/places:autocomplete";
    private static final double SURREY_LATITUDE = 49.1913;
    private static final double SURREY_LONGITUDE = -122.8490;
    private static final double SEARCH_RADIUS_METERS = 45_000;
    private static final Pattern PLACE_TEXT_PATTERN = Pattern.compile(
            "\"text\"\\s*:\\s*\\{\\s*\"text\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\""
    );

    private final String googleApiKey;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public LocationSearchController(
            @Value("${app.maps.google-api-key:}") String googleApiKey
    ) {
        this.googleApiKey = googleApiKey == null ? "" : googleApiKey.trim();
    }

    @GetMapping("/api/locations/search")
    public ResponseEntity<LocationSearchResponse> search(@RequestParam String query) {
        if (query == null || query.isBlank() || query.trim().length() < 3 || googleApiKey.isBlank()) {
            return ResponseEntity.ok(new LocationSearchResponse("fallback", List.of()));
        }

        try {
            String body = googleAutocompleteRequestBody(query.trim());
            HttpRequest request = HttpRequest.newBuilder(URI.create(GOOGLE_PLACES_AUTOCOMPLETE_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Goog-Api-Key", googleApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return ResponseEntity.ok(new LocationSearchResponse("fallback", List.of()));
            }
            return ResponseEntity.ok(new LocationSearchResponse("google", parseGoogleLocations(response.body())));
        } catch (Exception exception) {
            return ResponseEntity.ok(new LocationSearchResponse("fallback", List.of()));
        }
    }

    private String googleAutocompleteRequestBody(String query) {
        return """
                {
                  "input": "%s",
                  "includedRegionCodes": ["ca"],
                  "locationRestriction": {
                    "circle": {
                      "center": {
                        "latitude": %.4f,
                        "longitude": %.4f
                      },
                      "radius": %.0f
                    }
                  },
                  "languageCode": "en-CA",
                  "regionCode": "CA"
                }
                """.formatted(jsonEscape(query), SURREY_LATITUDE, SURREY_LONGITUDE, SEARCH_RADIUS_METERS);
    }

    private List<String> parseGoogleLocations(String body) {
        List<String> locations = new ArrayList<>();
        Matcher matcher = PLACE_TEXT_PATTERN.matcher(body);
        while (matcher.find()) {
            String text = jsonUnescape(matcher.group(1));
            if (!text.isBlank() && !locations.contains(text)) {
                locations.add(text);
            }
            if (locations.size() == 8) {
                break;
            }
        }
        return locations;
    }

    private String jsonEscape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String jsonUnescape(String value) {
        StringBuilder result = new StringBuilder();
        boolean escaping = false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (escaping) {
                result.append(switch (character) {
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    case 't' -> '\t';
                    case '"' -> '"';
                    case '\\' -> '\\';
                    default -> character;
                });
                escaping = false;
            } else if (character == '\\') {
                escaping = true;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    public record LocationSearchResponse(String source, List<String> locations) {
    }

}
