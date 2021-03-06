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

import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;

import java.util.concurrent.CancellationException;

public class PipedPromise<D, P, D_OUT, P_OUT> extends DeferredObject<D_OUT, P_OUT> implements Promise<D_OUT, P_OUT>{
	public PipedPromise(final Promise<D, P> promise, final DonePipe<D, D_OUT, P_OUT> donePipe, final FailPipe<D_OUT, P_OUT> failPipe, final ProgressPipe<P, D_OUT, P_OUT> progressPipe) {
		promise.done(new DoneCallback<D>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onDone(D result) {
		        if (donePipe != null)
					try {
						pipe(donePipe.pipeDone(result));
					} catch (CancellationException e) {
						PipedPromise.this.cancel();
					} catch (Exception e) {
						PipedPromise.this.reject(e);
					}
	            else PipedPromise.this.resolve((D_OUT) result);
			}
		}).fail(new FailCallback() {
			@Override
			public void onFail(Throwable result) {
				if (failPipe != null)
					try {
						pipe(failPipe.pipeFail(result));
					} catch (CancellationException e) {
						PipedPromise.this.cancel();
					}
				else PipedPromise.this.reject(result);
			}
		}).progress(new ProgressCallback<P>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onProgress(P progress) {
				if (progressPipe != null)
					try {
						pipe(progressPipe.pipeProgress(progress));
					} catch (CancellationException e) {
						PipedPromise.this.cancel();
					}
				else PipedPromise.this.notify((P_OUT) progress);
			}
		});
	}
	
	protected Promise<D_OUT, P_OUT> pipe(Promise<D_OUT, P_OUT> promise) {
		if (promise.isCancelled()) {
			cancel();
		}
		else {
			promise.done(new DoneCallback<D_OUT>() {
				@Override
				public void onDone(D_OUT result) {
					PipedPromise.this.resolve(result);
				}
			}).fail(new FailCallback() {
				@Override
				public void onFail(Throwable result) {
					PipedPromise.this.reject(result);
				}
			}).progress(new ProgressCallback<P_OUT>() {
				@Override
				public void onProgress(P_OUT progress) {
					PipedPromise.this.notify(progress);
				}
			});
		}
		return promise;
	}
}
