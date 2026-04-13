// Controller/AprobacionController.java
package Tablon.de.empleos.backend.Controller;

import Tablon.de.empleos.backend.DTO.response.AprobacionResponseDTO;
import Tablon.de.empleos.backend.Services.AprobacionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aprobacion")
public class AprobacionController {

    private final AprobacionService aprobacionService;

    @Value("${app.base-url}")
    private String baseUrl; // ← AÑADIR ESTA LÍNEA

    public AprobacionController(AprobacionService aprobacionService) {
        this.aprobacionService = aprobacionService;
    }

    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmarAprobacion(@RequestParam String token) {
        try {
            AprobacionResponseDTO response = aprobacionService.aprobarPostulacion(token);
            // Devolver HTML bonito
            String html = String.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>Postulación Aprobada | DevJobs</title>
                                <style>
                                    * { margin: 0; padding: 0; box-sizing: border-box; }
                                    body {
                                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                                        display: flex;
                                        justify-content: center;
                                        align-items: center;
                                        min-height: 100vh;
                                        margin: 0;
                                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                                        padding: 20px;
                                    }
                                    .card {
                                        background: white;
                                        padding: 40px;
                                        border-radius: 24px;
                                        text-align: center;
                                        box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
                                        max-width: 500px;
                                        width: 100%%;
                                        animation: slideUp 0.5s ease;
                                    }
                                    @keyframes slideUp {
                                        from { opacity: 0; transform: translateY(20px); }
                                        to { opacity: 1; transform: translateY(0); }
                                    }
                                    .success-icon {
                                        background: #10b981;
                                        width: 80px;
                                        height: 80px;
                                        border-radius: 50%%;
                                        display: flex;
                                        align-items: center;
                                        justify-content: center;
                                        margin: 0 auto 24px;
                                        box-shadow: 0 10px 20px rgba(16, 185, 129, 0.3);
                                    }
                                    .success-icon span {
                                        color: white;
                                        font-size: 48px;
                                        font-weight: bold;
                                    }
                                    h1 {
                                        color: #1f2937;
                                        margin-bottom: 12px;
                                        font-size: 28px;
                                        font-weight: 700;
                                    }
                                    .message {
                                        color: #6b7280;
                                        margin-bottom: 24px;
                                        font-size: 16px;
                                        line-height: 1.5;
                                    }
                                    .info {
                                        background: #f3f4f6;
                                        padding: 20px;
                                        border-radius: 16px;
                                        margin: 24px 0;
                                        text-align: left;
                                    }
                                    .info-item {
                                        display: flex;
                                        padding: 8px 0;
                                    }
                                    .info-label {
                                        font-weight: 600;
                                        color: #4b5563;
                                        width: 100px;
                                    }
                                    .info-value {
                                        color: #1f2937;
                                        flex: 1;
                                    }
                                    .btn {
                                        background: #667eea;
                                        color: white;
                                        padding: 14px 32px;
                                        border-radius: 50px;
                                        text-decoration: none;
                                        display: inline-block;
                                        font-weight: 600;
                                        font-size: 16px;
                                        transition: all 0.2s ease;
                                        box-shadow: 0 4px 6px rgba(102, 126, 234, 0.3);
                                        border: none;
                                        cursor: pointer;
                                    }
                                    .btn:hover {
                                        background: #5a67d8;
                                        box-shadow: 0 6px 8px rgba(102, 126, 234, 0.4);
                                        transform: translateY(-2px);
                                    }
                                    .footer {
                                        margin-top: 24px;
                                        font-size: 14px;
                                        color: #9ca3af;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="card">
                                    <div class="success-icon">
                                        <span>✓</span>
                                    </div>
                                    <h1>¡Postulación Aprobada!</h1>
                                    <p class="message">%s</p>
                                    <div class="info">
                                        <div class="info-item">
                                            <span class="info-label">👤 Candidato:</span>
                                            <span class="info-value"><strong>%s</strong></span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">💼 Oferta:</span>
                                            <span class="info-value"><strong>%s</strong></span>
                                        </div>
                                    </div>
                                    <p style="color: #6b7280; margin-bottom: 24px; font-size: 14px;">
                                        ✅ La oferta ha sido cerrada automáticamente.<br>
                                        El candidato será notificado de la aprobación.
                                    </p>
                                    <a href="%s" class="btn">Ir al panel de control</a>
                                    <div class="footer">
                                        DevJobs · Conectando talento tech
                                    </div>
                                </div>
                            </body>
                            </html>
                            """,
                    response.getMensaje(),
                    response.getNombreCandidato(),
                    response.getTituloOferta(),
                    baseUrl);
            return ResponseEntity.ok().contentType(org.springframework.http.MediaType.TEXT_HTML).body(html);
        } catch (Exception e) {
            String errorHtml = String.format(
                    """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>Error | DevJobs</title>
                                <style>
                                    * { margin: 0; padding: 0; box-sizing: border-box; }
                                    body {
                                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                                        display: flex;
                                        justify-content: center;
                                        align-items: center;
                                        min-height: 100vh;
                                        background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%);
                                        padding: 20px;
                                    }
                                    .card {
                                        background: white;
                                        padding: 40px;
                                        border-radius: 24px;
                                        text-align: center;
                                        box-shadow: 0 25px 50px -12px rgba(0,0,0,0.25);
                                        max-width: 450px;
                                        width: 100%%;
                                        animation: slideUp 0.5s ease;
                                    }
                                    @keyframes slideUp {
                                        from { opacity: 0; transform: translateY(20px); }
                                        to { opacity: 1; transform: translateY(0); }
                                    }
                                    .error-icon {
                                        background: #ef4444;
                                        width: 80px;
                                        height: 80px;
                                        border-radius: 50%%;
                                        display: flex;
                                        align-items: center;
                                        justify-content: center;
                                        margin: 0 auto 24px;
                                        box-shadow: 0 10px 20px rgba(239, 68, 68, 0.3);
                                    }
                                    .error-icon span {
                                        color: white;
                                        font-size: 48px;
                                        font-weight: bold;
                                    }
                                    h1 {
                                        color: #1f2937;
                                        margin-bottom: 16px;
                                        font-size: 28px;
                                        font-weight: 700;
                                    }
                                    .message {
                                        color: #6b7280;
                                        margin-bottom: 24px;
                                        font-size: 16px;
                                        line-height: 1.5;
                                        background: #fef2f2;
                                        padding: 16px;
                                        border-radius: 12px;
                                        border: 1px solid #fecaca;
                                    }
                                    .btn {
                                        background: #6b7280;
                                        color: white;
                                        padding: 14px 32px;
                                        border-radius: 50px;
                                        text-decoration: none;
                                        display: inline-block;
                                        font-weight: 600;
                                        font-size: 16px;
                                        transition: all 0.2s ease;
                                        border: none;
                                        cursor: pointer;
                                    }
                                    .btn:hover {
                                        background: #4b5563;
                                        transform: translateY(-2px);
                                    }
                                    .footer {
                                        margin-top: 24px;
                                        font-size: 14px;
                                        color: #9ca3af;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="card">
                                    <div class="error-icon">
                                        <span>✕</span>
                                    </div>
                                    <h1>Error</h1>
                                    <p class="message">%s</p>
                                    <p style="color: #9ca3af; margin-bottom: 24px; font-size: 14px;">
                                        El enlace puede haber expirado o ya fue utilizado.
                                    </p>
                                    <a href="%s" class="btn">Ir al inicio</a>
                                    <div class="footer">
                                        DevJobs · Si el problema persiste, contacta con soporte
                                    </div>
                                </div>
                            </body>
                            </html>
                            """,
                    e.getMessage(),
                    baseUrl);
            return ResponseEntity.badRequest().contentType(org.springframework.http.MediaType.TEXT_HTML)
                    .body(errorHtml);
        }
    }
}