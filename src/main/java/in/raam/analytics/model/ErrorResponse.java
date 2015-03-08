package in.raam.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

/**
 * Data model object for sending custom error response in lieu of validation errors, functional errors etc.
 * @author ramasubramanian on 07/03/15.
 */
public class ErrorResponse {
    public final long timestamp;
    public final String path;
    public final String httpMethod;
    public final List<String> errors;

    public ErrorResponse(List<String> errors, HttpServletRequest request) {
        this.errors = errors;
        this.timestamp = System.currentTimeMillis()/1000;
        this.path = request.getRequestURI();
        this.httpMethod = request.getMethod();
    }

}
