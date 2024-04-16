package info.ragozin.demo.httpunlim;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class IOTest {
	
	@Test
	public void empty_to_void() throws IOException {
		
		RngInputStream data = new RngInputStream(1, 8l << 30);
		VoidOutputStream vos = new VoidOutputStream();
		IOUtils.copy(data, vos);
		
		Assertions.assertThat(vos.getSize()).isEqualTo(8l << 30);		
	}
	
	@Test
	public void empty_size() throws IOException {
		RngInputStream data = new RngInputStream(1, 8l << 20);

		Assertions.assertThat(IOUtils.consume(data)).isEqualTo(8l << 20);
	}

}
