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

    public void enviarCorreoResend(String destinatario, String tituloOferta, String nombreCandidato, 
                                   String emailCandidato, String cuerpo, MultipartFile cv) {
        
        String asuntoPersonalizado = String.format("Solicitud del puesto (%s), por parte de %s", 
                                     tituloOferta, nombreCandidato);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", fromEmail);
        body.put("to", destinatario);
        // CLAVE: Esto permite que la empresa responda directamente al candidato
        body.put("reply_to", emailCandidato); 
        body.put("subject", asuntoPersonalizado);
        
        // Mejoramos el cuerpo del texto para que sea más profesional
        String textoFinal = String.format(
            "Has recibido una nueva postulación para: %s\n\n" +
            "Candidato: %s\n" +
            "Email de contacto: %s\n\n" +
            "Mensaje:\n%s",
            tituloOferta, nombreCandidato, emailCandidato, cuerpo
        );
        body.put("text", textoFinal);

        if (cv != null && !cv.isEmpty()) {
            try {
                byte[] bytes = cv.getBytes();
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                Map<String, String> attachment = new HashMap<>();
                attachment.put("filename", (cv.getOriginalFilename() != null) ? cv.getOriginalFilename() : "CV_Candidato.pdf");
                attachment.put("content", base64Content);
                body.put("attachments", Collections.singletonList(attachment));
            } catch (Exception e) {
                throw new RuntimeException("Error al procesar el CV", e);
            }
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(RESEND_API_URL, request, String.class);
    }

}