package br.com.student.portal.exception;

public class ApiError {
    private Integer statusCode;
    private String error;
    private Long timestamp;

    public ApiError(Integer statusCode, String error, Long timestamp) {
        this.statusCode = statusCode;
        this.error = error;
        this.timestamp = timestamp;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
