package org.jdeferred;

import org.jdeferred.impl.DeferredObject;

/**
 * Created by kdubb on 1/4/16.
 */
public class Promises {

	public static <D, P> Promise<D, P> resolvedPromise() {
		return resolvedPromise(null);
	}

	public static <D, P> Promise<D, P> resolvedPromise(D resolved) {
		return new DeferredObject<D, P>().resolve(resolved);
	}

	public static <D, P> Promise<D, P> rejectedPromise(Throwable resolved) {
		return new DeferredObject<D, P>().reject(resolved);
	}

	public static <D, P> Promise<D, P> promiseWith(Object value) {
		if (value instanceof Throwable) {
			return rejectedPromise((Throwable) value);
		}
		else {
			//noinspection unchecked
			return resolvedPromise((D)value);
		}
	}

	public static <D, P> Promise<D, P> cancelledPromise() {
		return new DeferredObject<D, P>().cancel();
	}

}
