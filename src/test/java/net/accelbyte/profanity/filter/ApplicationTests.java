package net.accelbyte.profanity.filter;

import net.accelbyte.profanity.filter.config.MockedAppConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(
		classes = MockedAppConfiguration.class,
		properties = "spring.main.allow-bean-definition-overriding=true"
)
class ApplicationTests {

	@Test
	void contextLoads() {
		
	}

}
