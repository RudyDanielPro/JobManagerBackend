package Tablon.de.empleos.backend.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String RESEND_API_URL = "https://api.resend.com/emails";

    // Modificamos los parámetros para construir el asunto dinámicamente
    public void enviarCorreoResend(String destinatario, String tituloOferta, String nombreCandidato, String cuerpo, MultipartFile cv) {
        
        // Construcción del asunto según tu requerimiento
        String asuntoPersonalizado = String.format("Solicitud del puesto (%s), por parte de %s", 
                                     tituloOferta, nombreCandidato);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", fromEmail);
        body.put("to", destinatario);
        body.put("subject", asuntoPersonalizado); // Usamos el asunto generado
        body.put("text", cuerpo);

        if (cv != null && !cv.isEmpty()) {
            try {
                byte[] bytes = cv.getBytes();
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                
                Map<String, String> attachment = new HashMap<>();
                attachment.put("filename", cv.getOriginalFilename());
                attachment.put("content", base64Content);
                
                body.put("attachments", Collections.singletonList(attachment));
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el adjunto para Resend", e);
            }
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(RESEND_API_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al enviar email vía Resend: " + response.getBody());
        }
    }
}