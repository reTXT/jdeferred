/*******************************************************************************
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
 ******************************************************************************/
package org.jdeferred.impl;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DoneFilter;
import org.jdeferred.DonePipe;
import org.jdeferred.FailCallback;
import org.jdeferred.FailFilter;
import org.jdeferred.FailPipe;
import org.jdeferred.ProgressCallback;
import org.jdeferred.ProgressFilter;
import org.jdeferred.ProgressPipe;
import org.jdeferred.Promise;

public class DeferredPromise<D, P> implements Promise<D, P> {
	private final Promise<D, P> promise;
	protected final Deferred<D, P> deferred;
	
	public DeferredPromise(Deferred<D, P> deferred) {
		this.deferred = deferred;
		this.promise = deferred.promise();
	}
	
	public org.jdeferred.Promise.State state() {
		return promise.state();
	}

	public boolean isPending() {
		return promise.isPending();
	}

	public boolean isResolved() {
		return promise.isResolved();
	}

	public boolean isRejected() {
		return promise.isRejected();
	}

	public boolean isCancelled() {
		return promise.isCancelled();
	}

	public Promise<D, P> then(DoneCallback<D> doneCallback) {
		return promise.then(doneCallback);
	}

	public Promise<D, P> then(DoneCallback<D> doneCallback,
			FailCallback failCallback) {
		return promise.then(doneCallback, failCallback);
	}

	public Promise<D, P> then(DoneCallback<D> doneCallback,
			FailCallback failCallback, ProgressCallback<P> progressCallback) {
		return promise.then(doneCallback, failCallback, progressCallback);
	}

	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter) {
		return promise.then(doneFilter);
	}

	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter failFilter) {
		return promise.then(doneFilter, failFilter);
	}

	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DoneFilter<D, D_OUT> doneFilter, FailFilter failFilter,
			ProgressFilter<P, P_OUT> progressFilter) {
		return promise.then(doneFilter, failFilter, progressFilter);
	}

	public Promise<D, P> done(DoneCallback<D> callback) {
		return promise.done(callback);
	}

	public Promise<D, P> fail(FailCallback callback) {
		return promise.fail(callback);
	}

	public Promise<D, P> always(AlwaysCallback<D> callback) {
		return promise.always(callback);
	}

	public Promise<D, P> always(Runnable runnable) {
		return promise.always(runnable);
	}

	public Promise<D, P> progress(ProgressCallback<P> callback) {
		return promise.progress(callback);
	}

	@Override
	public void waitSafely() throws InterruptedException {
		promise.waitSafely();
		
	}

	@Override
	public void waitSafely(long timeout) throws InterruptedException {
		promise.waitSafely(timeout);
	}

	@Override
	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DonePipe<D, D_OUT, P_OUT> doneFilter) {
		return promise.then(doneFilter);
	}

	@Override
	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DonePipe<D, D_OUT, P_OUT> doneFilter,
			FailPipe<D_OUT, P_OUT> failFilter) {
		return promise.then(doneFilter, failFilter);
	}

	@Override
	public <D_OUT, P_OUT> Promise<D_OUT, P_OUT> then(
			DonePipe<D, D_OUT, P_OUT> doneFilter,
			FailPipe<D_OUT, P_OUT> failFilter,
			ProgressPipe<P, D_OUT, P_OUT> progressFilter) {
		return promise.then(doneFilter, failFilter, progressFilter);
	}
}
