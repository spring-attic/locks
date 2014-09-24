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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

/**
 * @author Dave Syer
 *
 */
public abstract class AbstractLockServiceTests {

	private LockService service;

	protected abstract LockService getLockService();
	
	protected abstract void setExpiry(long expires);
	
	@Before
	public void init() {
		service = getLockService();
	}
	
	@Test
	public void createLock() {
		assertNotNull(service.create("foo"));
		try {
			service.create("foo");
			fail("Expected LockExistsException");
		}
		catch (LockExistsException e) {
			// expected
		}
		service.create("bar");
		assertEquals(2, Iterables.size(service.findAll()));
	}

	@Test
	public void releaseLock() {
		Lock lock = service.create("foo");
		service.release("foo", lock.getValue());
		service.create("foo");
	}

	@Test
	public void expireLock() throws Exception {
		setExpiry(100);
		service.create("foo");
		Thread.sleep(200L);
		service.create("foo");
	}

	@Test
	public void refreshExpiredLock() throws Exception {
		setExpiry(100);
		Lock lock = service.create("foo");
		Thread.sleep(200L);
		try {
			service.refresh("foo", lock.getValue());
			fail("Expected LockNotHeldException");
		}
		catch (LockNotHeldException e) {
			// expected
		}
	}

}
