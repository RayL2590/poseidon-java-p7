package com.nnk.springboot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestionnaire global d'exceptions et d'erreurs pour l'application
 */
@ControllerAdvice
@Controller
public class GlobalExceptionHandler implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Value("${server.error.include-stacktrace:never}")
    private String includeStacktrace;
    
    @Value("${server.error.include-message:never}")
    private String includeMessage;

    /**
     * Gestion des erreurs HTTP générales
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Récupération des attributs d'erreur
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        logger.error("Error occurred - Status: {}, URI: {}, Message: {}", 
                    status, requestUri, errorMessage, (Throwable) exception);
        
        // Ajout des informations au modèle
        if (status != null) {
            model.addAttribute("status", status.toString());
            
            Integer statusCode = Integer.valueOf(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "Page not found");
                model.addAttribute("error", "Not Found");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorMessage", "Internal server error occurred");
                model.addAttribute("error", "Internal Server Error");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("errorMessage", "Access denied");
                model.addAttribute("error", "Forbidden");
            } else {
                model.addAttribute("errorMessage", "An error occurred");
                model.addAttribute("error", "Error " + statusCode);
            }
        }
        
        if (errorMessage != null) {
            model.addAttribute("message", errorMessage.toString());
        }
        
        if (exception != null && "always".equals(includeStacktrace)) {
            model.addAttribute("exception", exception.getClass().getSimpleName());
            model.addAttribute("trace", getStackTrace((Throwable) exception));
        }
        
        model.addAttribute("path", requestUri);
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "error";
    }
    
    /**
     * Gestion des exceptions non capturées dans les contrôleurs
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletRequest request) {
        logger.error("Unhandled exception in application", e);
        
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        model.addAttribute("error", e.getClass().getSimpleName());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        if ("always".equals(includeStacktrace)) {
            model.addAttribute("trace", getStackTrace(e));
        }
        
        return "error";
    }
    
    /**
     * Gestion spécifique des erreurs de validation
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleValidationException(IllegalArgumentException e, Model model, HttpServletRequest request) {
        logger.warn("Validation error: {}", e.getMessage());
        
        model.addAttribute("errorMessage", "Validation error: " + e.getMessage());
        model.addAttribute("error", "Validation Error");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "error";
    }
    
    /**
     * Gestion des erreurs de base de données
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public String handleDataAccessException(org.springframework.dao.DataAccessException e, Model model, HttpServletRequest request) {
        logger.error("Database error", e);
        
        model.addAttribute("errorMessage", "Database error occurred. Please try again later.");
        model.addAttribute("error", "Database Error");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        if ("always".equals(includeStacktrace)) {
            model.addAttribute("trace", getStackTrace(e));
        }
        
        return "error";
    }
    
    private String getStackTrace(Throwable e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}