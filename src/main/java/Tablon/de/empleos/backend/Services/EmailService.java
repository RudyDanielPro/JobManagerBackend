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

    /**
     * Envía un correo electrónico con los detalles de la postulación
     * @param destinatario Email del reclutador/destinatario
     * @param tituloOferta Título de la oferta
     * @param nombreCandidato Nombre completo del candidato
     * @param emailCandidato Email del candidato (para reply-to)
     * @param cuerpo Mensaje/carta de presentación
     * @param cv Archivo CV adjunto
     * @param urlAprobacion URL para aprobar la postulación con un clic
     */
    public void enviarCorreoResend(String destinatario, String tituloOferta, String nombreCandidato, 
                                   String emailCandidato, String cuerpo, MultipartFile cv, 
                                   String urlAprobacion) {
        
        String asuntoPersonalizado = String.format("📋 Nueva postulación: %s - %s", 
                                     tituloOferta, nombreCandidato);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("from", fromEmail);
        body.put("to", Arrays.asList(destinatario));  // Resend espera un array
        body.put("reply_to", emailCandidato);
        body.put("subject", asuntoPersonalizado);
        
        // Construir HTML con botón de aprobación
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; 
                        line-height: 1.6; 
                        color: #1f2937; 
                        margin: 0; 
                        padding: 0; 
                        background-color: #f3f4f6;
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 20px auto; 
                        background: white; 
                        border-radius: 16px; 
                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                        overflow: hidden;
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                        color: white; 
                        padding: 30px 20px; 
                        text-align: center;
                    }
                    .header h2 {
                        margin: 0;
                        font-size: 24px;
                        font-weight: 600;
                    }
                    .content { 
                        padding: 30px 25px; 
                    }
                    .info-card { 
                        background: #f9fafb; 
                        padding: 20px; 
                        border-radius: 12px; 
                        margin: 20px 0;
                        border-left: 4px solid #667eea;
                    }
                    .info-item {
                        margin: 8px 0;
                    }
                    .info-label {
                        font-weight: 600;
                        color: #4b5563;
                        display: inline-block;
                        width: 120px;
                    }
                    .info-value {
                        color: #1f2937;
                    }
                    .message-box {
                        background: white;
                        padding: 20px;
                        border-radius: 12px;
                        margin: 20px 0;
                        border: 1px solid #e5e7eb;
                        font-style: italic;
                        color: #4b5563;
                    }
                    .btn-container {
                        text-align: center;
                        margin: 30px 0 20px;
                    }
                    .btn { 
                        display: inline-block; 
                        background: #10b981; 
                        color: white !important; 
                        padding: 16px 40px; 
                        border-radius: 50px; 
                        text-decoration: none; 
                        font-weight: 600;
                        font-size: 16px;
                        box-shadow: 0 4px 6px rgba(16, 185, 129, 0.3);
                        transition: all 0.2s ease;
                        border: none;
                    }
                    .btn:hover { 
                        background: #059669; 
                        box-shadow: 0 6px 8px rgba(16, 185, 129, 0.4);
                        transform: translateY(-2px);
                    }
                    .btn-note {
                        font-size: 13px;
                        color: #6b7280;
                        margin-top: 12px;
                    }
                    .footer { 
                        margin-top: 30px; 
                        padding-top: 20px;
                        border-top: 1px solid #e5e7eb;
                        font-size: 12px; 
                        color: #9ca3af; 
                        text-align: center;
                    }
                    .footer a {
                        color: #667eea;
                        text-decoration: none;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>✨ Nueva Postulación Recibida</h2>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px; margin-bottom: 20px;">
                            Has recibido una nueva postulación para tu oferta. Revisa los detalles a continuación:
                        </p>
                        
                        <div class="info-card">
                            <div class="info-item">
                                <span class="info-label">🏢 Oferta:</span>
                                <span class="info-value"><strong>%s</strong></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">👤 Candidato:</span>
                                <span class="info-value"><strong>%s</strong></span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">📧 Email:</span>
                                <span class="info-value"><a href="mailto:%s" style="color: #667eea;">%s</a></span>
                            </div>
                        </div>
                        
                        <h3 style="margin-bottom: 10px; color: #374151;">📝 Mensaje del candidato:</h3>
                        <div class="message-box">
                            %s
                        </div>
                        
                        <div class="btn-container">
                            <a href="%s" class="btn">✅ APROBAR POSTULACIÓN</a>
                            <p class="btn-note">
                                Al hacer clic, la postulación será aprobada,<br>
                                la oferta se cerrará automáticamente y se notificará al candidato.
                            </p>
                        </div>
                        
                        <p style="font-size: 14px; color: #6b7280; margin-top: 20px;">
                            💡 También puedes responder directamente a este correo para contactar al candidato.
                        </p>
                    </div>
                    <div class="footer">
                        <p>
                            Este es un correo automático de <strong>DevJobs</strong>.<br>
                            El enlace de aprobación expirará en 7 días.<br>
                            <a href="%s">Ir al panel de control</a>
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """,
            tituloOferta,
            nombreCandidato,
            emailCandidato,
            emailCandidato,
            escapeHtml(cuerpo),
            urlAprobacion,
            "https://jobmanagerbackend.onrender.com"
        );
        
        body.put("html", htmlContent);

        // Versión texto plano (fallback)
        String textoFinal = String.format(
            "========================================\n" +
            "     NUEVA POSTULACIÓN RECIBIDA\n" +
            "========================================\n\n" +
            "🏢 Oferta: %s\n" +
            "👤 Candidato: %s\n" +
            "📧 Email de contacto: %s\n\n" +
            "📝 Mensaje:\n%s\n\n" +
            "----------------------------------------\n" +
            "✅ Para APROBAR esta postulación, visita:\n%s\n" +
            "----------------------------------------\n\n" +
            "Este enlace expirará en 7 días.\n\n" +
            "Saludos,\nEquipo de DevJobs",
            tituloOferta, nombreCandidato, emailCandidato, cuerpo, urlAprobacion
        );
        body.put("text", textoFinal);

        // Adjuntar CV si existe
        if (cv != null && !cv.isEmpty()) {
            try {
                byte[] bytes = cv.getBytes();
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                
                Map<String, String> attachment = new HashMap<>();
                String filename = cv.getOriginalFilename() != null ? cv.getOriginalFilename() : "CV_" + nombreCandidato.replace(" ", "_") + ".pdf";
                attachment.put("filename", filename);
                attachment.put("content", base64Content);
                
                body.put("attachments", Collections.singletonList(attachment));
            } catch (Exception e) {
                System.err.println("Error al procesar el CV adjunto: " + e.getMessage());
                // Continuar sin el adjunto si hay error
            }
        }

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(RESEND_API_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Email enviado exitosamente a: " + destinatario);
            } else {
                System.err.println("⚠️ Error al enviar email. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            // No lanzamos excepción para no interrumpir el flujo de postulación
        }
    }

    /**
     * Escapa caracteres HTML para prevenir inyección
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("\n", "<br>");
    }
}