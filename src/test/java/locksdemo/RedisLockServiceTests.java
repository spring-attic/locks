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

import org.junit.Before;
import org.junit.ClassRule;
import org.springframework.data.redis.core.RedisOperations;


/**
 * @author Dave Syer
 *
 */
public class RedisLockServiceTests extends AbstractLockServiceTests {

	private RedisLockService service;
	
	@ClassRule
	public static RedisServer server = RedisServer.running();
	
	@Before
	public void init() {
		super.init();
		RedisOperations<String, String> template = RedisUtils.stringTemplate(server.getResource());
		template.delete("spring.lock.foo");
		template.delete("spring.lock.bar");
	}
	
	@Override
	protected LockService getLockService() {
		if (service==null) {			
			service = new RedisLockService(server.getResource());
		}
		return service;
	}
	
	@Override
	protected void setExpiry(long expiry) {
		getLockService();
		service.setExpiry(expiry);
	}	

}
