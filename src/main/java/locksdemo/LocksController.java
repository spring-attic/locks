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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dave Syer
 *
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor(onConstructor=@_(@Autowired))
public class LocksController {
	
	private final LockService service;

	@RequestMapping(method=RequestMethod.GET)
	public Iterable<Lock> locks() {
		return service.findAll();
	}

	@RequestMapping(value="{name}", method=RequestMethod.POST)
	public Lock create(@PathVariable String name) {
		return service.create(name);
	}

	@RequestMapping(value="{name}/{value}", method=RequestMethod.DELETE)
	public Map<String, Object> release(@PathVariable String name, @PathVariable String value) {
		if (!service.release(name, value)) {
			throw new NoSuchLockException();
		}
		return Collections.singletonMap("status", (Object) "OK");
	}

	@RequestMapping(value="{name}/{value}", method=RequestMethod.PUT)
	public Lock refresh(@PathVariable String name, @PathVariable String value) {
		return service.refresh(name, value);
	}
	
	@ExceptionHandler(LockExistsException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> lockExists() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("status", "INVALID");
		body.put("description", "Lock already exists");
		return new ResponseEntity<Map<String, Object>>(body, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NoSuchLockException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> noSuchLock() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("status", "INVALID");
		body.put("description", "Lock not found");
		return new ResponseEntity<Map<String, Object>>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(LockNotHeldException.class)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> lockNotHeld() {
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("status", "INVALID");
		body.put("description", "Lock not held (values do not match)");
		return new ResponseEntity<Map<String, Object>>(body, HttpStatus.NOT_FOUND);
	}

}
