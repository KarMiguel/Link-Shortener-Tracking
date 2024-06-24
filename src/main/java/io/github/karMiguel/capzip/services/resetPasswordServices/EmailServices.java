package io.github.karMiguel.capzip.services.resetPasswordServices;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServices {

	private final JavaMailSender mailSender;

	public void enviarPedidoRedefinicaoSenha(String destino, String verificador) throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper =
				new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

		helper.setTo(destino);
		helper.setSubject("Redefinição de Senha");
		helper.setFrom("no-reply@clinica.com.br");
		String texto = "<!DOCTYPE html>" +
				"<html lang=\"en\">" +
				"<head>" +
				"<meta charset=\"UTF-8\">" +
				"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
				"<title>Redefinição de Senha Cap Zip</title>" +
				"</head>" +
				"<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;\">" +
				"<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
				"<div style=\"background-color: #ffffff; padding: 30px; border-radius: 10px; text-align: center;\">" +
				"<h1>Redefinição de Senha!!</h1>" +
				"<div style=\"text-align: center; margin-bottom: 20px;\">" +
				"<p>Você solicitou a redefinição de senha para a sua conta.</p>" +
				"<img src='cid:logo' style=\"max-width: 50%; height: auto;\">" +
				"</div>" +
				"<p>Copie código abaixo e cole no campo código para redefinir sua senha.  </p>" +
				"<h3>Código verificador: <b>" + verificador + "</b></h3>" +
				"</div>" +
				"</div>" +
				"</body>" +
				"</html>";

		helper.setText(texto, true);

		helper.addInline("logo", new ClassPathResource("/templates/img/LogoOficialCut.png"));

		mailSender.send(message);
	}
}
