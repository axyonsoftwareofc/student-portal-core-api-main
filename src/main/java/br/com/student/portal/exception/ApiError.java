package br.com.student.portal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {
    private Integer statusCode;
    private String error;
    private Long timestamp;
}
