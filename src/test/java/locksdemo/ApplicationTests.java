package locksdemo;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LocksApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class ApplicationTests {
	
	@Value("${local.server.port}")
	private int port = 0;

	@Test
	public void locksLoad() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<List> entity = new TestRestTemplate().getForEntity("http://localhost:" + port, List.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	@Test
	public void createLock() {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = new TestRestTemplate().postForEntity("http://localhost:" + port + "/foo", "bar", Map.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

}
