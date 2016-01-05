/*
 * Copyright 2013 Ray Tsang
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdeferred.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javafx.scene.media.VideoTrack;
import org.jdeferred.*;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import static org.jdeferred.Promises.rejectedPromise;

public class PipedPromiseTest extends AbstractDeferredTest {
	@Test
	public void testDoneRewireFilter() {
		final ValueHolder<Integer> preRewireValue = new ValueHolder<Integer>();
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				return 100;
			}
		};
		
		deferredManager.when(task).then(new DonePipe<Integer, Integer, Void>() {
			@Override
			public Promise<Integer, Void> pipeDone(Integer result) {
				preRewireValue.set(result);
				return new DeferredObject<Integer, Void>().resolve(1000);
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer value) {
				postRewireValue.set(value);
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals(100);
		postRewireValue.assertEquals(1000);
	}
	
	@Test
	public void testFailRewireFilter() {
		final ValueHolder<String> preRewireValue = new ValueHolder<String>();
		final ValueHolder<String> postRewireValue = new ValueHolder<String>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				throw new RuntimeException("oops");
			}
		};
		
		deferredManager.when(task).then(null, new FailPipe<Integer, Void>() {
			@Override
			public Promise<Integer, Void> pipeFail(Throwable result) {
				preRewireValue.set(result.getMessage());
				return new DeferredObject<Integer, Void>().reject(new RuntimeException("ouch"));
			}
		}).fail(new FailCallback() {
			@Override
			public void onFail(Throwable result) {
				postRewireValue.set(result.getMessage());
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals("oops");
		postRewireValue.assertEquals("ouch");
	}
	
	@Test
	public void testNullDoneRewireFilter() {
		final ValueHolder<Boolean> failed = new ValueHolder<Boolean>(false);
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		
		Callable<Integer> task = new Callable<Integer>() {
			public Integer call() {
				return 100;
			}
		};
		
		deferredManager.when(task).then(null, new FailPipe<Integer, Void>() {
			@Override
			public Promise<Integer, Void> pipeFail(Throwable result) {
				return new DeferredObject<Integer, Void>().reject(new RuntimeException("ouch"));
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				postRewireValue.set(result);
			}
		}).fail(new FailCallback() {
			@Override
			public void onFail(Throwable result) {
				failed.set(true);
			}
		});
		
		waitForCompletion();
		failed.assertEquals(false);
		postRewireValue.assertEquals(100);
	}
	
	@Test
	public void testDoneRewireToFail() {
		final ValueHolder<Integer> preRewireValue = new ValueHolder<Integer>();
		final ValueHolder<Integer> postRewireValue = new ValueHolder<Integer>();
		final ValueHolder<String> failed = new ValueHolder<String>();
		
		deferredManager.when(new Callable<Integer>() {
			public Integer call() {
				return 10;
			}
		}).then(new DonePipe<Integer, Integer, Void>() {
			@Override
			public Promise<Integer, Void> pipeDone(Integer result) {
				preRewireValue.set(result);
				if (result < 100) {
					return new DeferredObject<Integer, Void>().reject(new RuntimeException("less than 100"));
				} else {
					return new DeferredObject<Integer, Void>().resolve(result);
				}
			}
		}).done(new DoneCallback<Integer>() {
			@Override
			public void onDone(Integer result) {
				postRewireValue.set(result);
			}
		}).fail(new FailCallback() {
			@Override
			public void onFail(Throwable result) {
				failed.set(result.getMessage());
			}
		});
		
		waitForCompletion();
		preRewireValue.assertEquals(10);
		postRewireValue.assertEquals(null);
		failed.assertEquals("less than 100");
	}

}
