package info.ragozin.demo.httpunlim;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class HttpUnlimService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpUnlimService.class);
	
	@GetMapping("ping")
	public String ping() {
		return "pong";
	}

	@GetMapping("binary")
	public void getBinaryUnlim(@RequestParam long size, HttpServletResponse response) throws IOException {
		response.setStatus(200);
		OutputStream os = response.getOutputStream();
		generateGarbage(size, os);
		os.close();
	}

	@PostMapping("blackhole")
	public ResponseEntity<String> blackHole(HttpServletRequest request) throws IOException {
		long size = IOUtils.consume(request.getInputStream());
		LOGGER.info("Voided " + size + " bytes");
		return ResponseEntity.ok("Done");
	}
	
	private void generateGarbage(long size, OutputStream os) throws IOException {
		long sent = 0;
		Random rnd = new Random(1);
		byte[] buf = new byte[1024];
		while (sent < size) {
			rnd.nextBytes(buf);
			int l = (int) Math.min(size - sent, buf.length);
			os.write(buf, 0, l);
			sent += l;
			if ((sent % (100l << 20)) == 0) {
				LOGGER.info("Sent " + (sent >> 20) + "m of data");
			}
		}
	}
}
