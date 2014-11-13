/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package locksdemo;

import javax.sql.DataSource;

import locksdemo.JdbcLockServiceTests.TestConfiguration;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author Dave Syer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestConfiguration.class)
@IntegrationTest("spring.datasource.schema=classpath:/locks-schema.sql")
public class JdbcLockServiceTests extends AbstractLockServiceTests {

	@Autowired
	private JdbcLockService service;
	
	@Autowired
	private DataSource dataSource;
	
	@Before
	public void init() {
		super.init();
		service.setExpiry(30000);
		new JdbcTemplate(dataSource).update("DELETE FROM LOCKS");
	}
	
	@Override
	protected LockService getLockService() {
		return service;
	}
	
	@Override
	protected void setExpiry(long expiry) {
		getLockService();
		service.setExpiry(expiry);
	}	

	@Configuration
	@Import({DataSourceAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class})
	protected static class TestConfiguration {
		@Bean
		public JdbcLockService lockService(DataSource dataSource) {
			return new JdbcLockService(dataSource);
		}
	}
	
}
