package com.is4tech.base.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.is4tech.base.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class Utilities {

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	private static final Logger logger = LoggerFactory.getLogger("APP");

	public static final Map<Integer, ErrorDTO.ApiError> ERRORS_CODES = Map.of(
			400, new ErrorDTO.ApiError(400, "MSJ", "002", "Error en mensaje de entrada"),
			401, new ErrorDTO.ApiError(401, "SEG", "001", "Token inválido de autorización"),
			403, new ErrorDTO.ApiError(403, "SEG", "001", "Permiso denegado"),
			404, new ErrorDTO.ApiError(404, "MSJ", "002", "Tratando de acceder a recurso que no existe"),
			405, new ErrorDTO.ApiError(405, "MSJ", "002", "Método no soportado"),
			409, new ErrorDTO.ApiError(409, "NEG", "003", "Conflicto"),
			503, new ErrorDTO.ApiError(503, "COM", "003", "No hay comunicación con el servicio"),
			504, new ErrorDTO.ApiError(504, "COM", "003", "Timeout en request al servicio legado"),
			500, new ErrorDTO.ApiError(500, "COM", "003", "Error interno del servidor")
	);

	private Utilities() {
		super();
	}

	public static void infoLog(HttpServletRequest request, HttpStatus status, Object description) {
		log(request, status, description, "info", null);
	}

	public static void debugLog(Object description) {
		log(null, null, description, "debug", null);
	}

	public static void errorLog(HttpServletRequest request, HttpStatus status, Object description, Exception detail) {
		log(request, status, description, "error", detail);
	}

	private static void log(HttpServletRequest request, HttpStatus status, Object object, String type, Exception exceptionDetail) {
		try {
			var jsonLog = new JsonObject();
			jsonLog.addProperty("name", "base-java-api");
			jsonLog.addProperty("hostname", InetAddress.getLocalHost().toString());
			jsonLog.addProperty("uri", "");
			if (status != null) {
				jsonLog.addProperty("responseCode", status.value());
			}
			jsonLog.addProperty("pid", ProcessHandle.current().pid());
			jsonLog.addProperty("level", type);
			jsonLog.addProperty("msg", object.toString());
			jsonLog.addProperty("time", formatoFecha(new Date()));
			jsonLog.addProperty("v", "1");
			if (request != null) {
				jsonLog.addProperty("uri", request.getRequestURI());
				Date startTime = (Date) request.getAttribute("startTime");
				if (startTime != null) {
					jsonLog.addProperty("responseTime", new Date().getTime() - startTime.getTime());
				}
				jsonLog.addProperty("clientIp", request.getRemoteAddr());
				String apiKey = request.getHeader(HttpHeaders.AUTHORIZATION);
				if (apiKey == null) {
					apiKey = "";
				}
				jsonLog.addProperty("apiKey", apiKey);
			}
			if (object.getClass().getPackageName().startsWith("com.is4tech")) {
				jsonLog.add("msg", gson.toJsonTree(object));
			} else {
				jsonLog.addProperty("msg", object.toString());
			}

			if (exceptionDetail != null && exceptionDetail.getStackTrace() != null) {
				String detail = Arrays.stream(exceptionDetail.getStackTrace()).limit(5).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
				jsonLog.addProperty("exceptionDetail",  detail);
			}

			String json = gson.toJson(jsonLog);
			switch (type) {
				case "error" -> logger.error(json);
				case "info" -> logger.info(json);
				default -> logger.debug(json);
			}
		} catch (Exception e) {
			var logger = LoggerFactory.getLogger(Utilities.class);
			logger.error("Error imprimiendo LOG", e);
		}
	}

	public static String formatoFecha(Date fecha) {
		DateFormat formato = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		return formato.format(fecha);
	}

	public static String formatoFechaSimple(Date fecha) {
		if (fecha != null) {
			DateFormat formato = new SimpleDateFormat("MM/dd/yyyy");
			return formato.format(fecha);
		}
		return "";
	}

	public static ErrorDTO getError(Integer code, String description) {
		var apiError = ERRORS_CODES.get(code);
		if (apiError == null) {
			apiError = ERRORS_CODES.get(500);
		}
		if (description != null && !description.isEmpty()) {
			apiError.setDescription(description);
		}
		return new ErrorDTO(apiError);
	}

}
