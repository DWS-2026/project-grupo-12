package es.codeurjc.web.controller.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        
        // We extract the error details from the request attributes that Spring sets when an error occurs
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        // Sentences shown if an error occurs, we will customize them later based on the status code
        String errorTitle = "¡Ups! Algo salió mal en tu viaje.";
        String errorSubtitle = "Hemos encontrado un problema inesperado. Nuestro equipo ya está avisado.";

        // Customization of the sentence deppending on the status error code
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);

            if (statusCode == 404) {
                errorTitle = "¡Ups! Parece que te has perdido en tu viaje.";
                errorSubtitle = "No pudimos encontrar tu destino. La ruta solicitada no existe o el hotel ha sido movido.";
            } else if (statusCode == 500) {
                errorTitle = "¡Problemas técnicos en recepción!";
                errorSubtitle = "Nuestros servidores están teniendo un pequeño problema interno. Por favor, inténtalo de nuevo en unos minutos.";
            } else if (statusCode == 403) {
                errorTitle = "Acceso Restringido";
                errorSubtitle = "No tienes las llaves o los permisos necesarios para entrar a esta zona del hotel.";
            }
        } else {
            model.addAttribute("status", "Error Desconocido");
        }

        // We put the variables in the model for Mustache to be able to show them in the error.html template
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorSubtitle", errorSubtitle);
        model.addAttribute("path", path != null ? path : "Ruta desconocida");
        model.addAttribute("error", message != null && !message.toString().isEmpty() ? message : "");

        // We call the error.html template to show the error page to the user
        return "error";
    }
}
