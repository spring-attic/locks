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

import java.util.Date;
import java.util.PriorityQueue;
import java.util.UUID;

import lombok.Setter;

import org.springframework.boot.actuate.metrics.util.SimpleInMemoryRepository;
import org.springframework.boot.actuate.metrics.util.SimpleInMemoryRepository.Callback;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author Dave Syer
 *
 */
@Service
@ConfigurationProperties("spring.platform.lock")
public class SimpleLockService implements LockService {

	private final SimpleInMemoryRepository<Lock> locks = new SimpleInMemoryRepository<Lock>();

	private final PriorityQueue<Lock> ordered = new PriorityQueue<Lock>();

	@Setter
	private long expiry = 30000; // 30 seconds

	@Override
	public Iterable<Lock> findAll() {
		reap();
		return locks.findAll();
	}

	@Override
	public Lock create(final String name) {
		reap(name);
		return locks.update(name, new Callback<Lock>() {
			@Override
			public Lock modify(Lock current) {
				if (locks.findOne(name) != null) {
					throw new LockExistsException();
				}
				return add(name, UUID.randomUUID().toString());
			}
		});
	}

	@Override
	public boolean release(final String name, final String value) {
		reap(name);
		final Lock lock = locks.findOne(name);
		if (lock == null) {
			return false;
		}
		locks.remove(name);
		ordered.remove(lock);
		return true;

	}

	@Override
	public Lock refresh(final String name, final String value) {
		return locks.update(name, new Callback<Lock>() {
			@Override
			public Lock modify(Lock current) {
				Lock lock = reap(name);
				if (lock != null) {
					throw new LockNotHeldException();
				}
				if (!current.getValue().equals(value)) {
					throw new LockNotHeldException();
				}
				lock = add(name, value);
				return lock;
			}
		});
	}

	private Lock add(String name, String value) {
		Lock lock;
		lock = new Lock(name, value, new Date(System.currentTimeMillis() + expiry));
		locks.set(name, lock);
		ordered.add(lock);
		return lock;
	}

	private synchronized void reap() {
		Lock lock = ordered.poll();
		while (lock != null && reap(lock.getName()) != null) {
			lock = ordered.poll();
		}
		if (lock != null) {
			ordered.add(lock);
		}
	}

	private Lock reap(String name) {
		Lock lock = locks.findOne(name);
		if (lock == null) {
			return null;
		}
		Date now = new Date();
		if (lock.getExpires().before(now)) {
			locks.remove(lock.getName());
			ordered.remove(lock);
			return lock;
		}
		return null;
	}

}
