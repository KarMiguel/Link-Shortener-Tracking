package io.github.karMiguel.capzip.controllers;

import io.github.karMiguel.capzip.dtos.usersDto.AccountCredentialsDto;
import io.github.karMiguel.capzip.exceptions.ResponseSuccess;
import io.github.karMiguel.capzip.services.usersServices.AuthServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthServices authServices;

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Authenticates a user and returns a token")
	@PostMapping("/signin")
	public ResponseEntity signin(@RequestBody AccountCredentialsDto data) {
		if (authServices.checkIfParamsIsNotNull(data)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}

		var token = authServices.signin(data);

		if (token == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid client request!");
		} else {
			return ResponseEntity.ok(token);
		}
	}

	@SuppressWarnings("rawtypes")
	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping("/refresh/{username}")
	public ResponseEntity refreshToken(@PathVariable("username") String username,
									   @RequestHeader("Authorization") String refreshToken) {
		if (authServices.checkIfParamsIsNotNull(username, refreshToken))
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		var token = authServices.refreshToken(username, refreshToken);
		if (token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		return token;
	}
	@Operation(summary = "Logs out an authenticated user")
	@PostMapping("/logout")
	public ResponseEntity logout(@RequestHeader("Authorization") String token) {
		boolean isLoggedOut = authServices.logout(token);
		if (!isLoggedOut) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("Logout failed!"));
		}
		return ResponseEntity.ok("Successfully logged out!");
	}
}
