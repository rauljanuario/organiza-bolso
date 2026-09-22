package com.rauljanuario.organiza_bolso.exception;

import org.springframework.http.HttpStatus;

public record ErrorMessage(HttpStatus status, String message) {
}
