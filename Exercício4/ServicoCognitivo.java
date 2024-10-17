import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServicoCognitivo {
    private static final String subscriptionKey = "SUA_CHAVE_DE_ASSINATURA";
    private static final String endpoint = "https://<seu-endpoint>.cognitiveservices.azure.com/face/v1.0/detect";

    public static void main(String[] args) throws Exception {
        // Caminho para a imagem a ser enviada
        Path imagePath = Paths.get("caminho/para/sua/imagem.jpg");

        // Montar a requisição HTTP
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(endpoint + "?returnFaceAttributes=emotion"))
            .header("Ocp-Apim-Subscription-Key", subscriptionKey)
            .header("Content-Type", "application/octet-stream")
            .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(imagePath)))
            .build();

        // Enviar a requisição e receber a resposta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Processar a resposta JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        if (rootNode.isArray()) {
            for (JsonNode face : rootNode) {
                JsonNode emotions = face.get("faceAttributes").get("emotion");
                System.out.println("Emotions detected:");
                System.out.println("Happiness: " + emotions.get("happiness").asDouble());
                System.out.println("Sadness: " + emotions.get("sadness").asDouble());
                System.out.println("Anger: " + emotions.get("anger").asDouble());
                System.out.println("Surprise: " + emotions.get("surprise").asDouble());
                // Outras emoções...
            }
        } else {
            System.out.println("Nenhum rosto detectado.");
        }
    }
}
