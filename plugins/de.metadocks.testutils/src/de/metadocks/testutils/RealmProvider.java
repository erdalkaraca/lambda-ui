/*******************************************************************************
 * Copyright (c) 2016 Erdal Karaca and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Erdal Karaca - initial API and implementation
 *******************************************************************************/
package de.metadocks.testutils;

import org.eclipse.core.databinding.observable.Realm;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RealmProvider implements TestRule {

	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				// create a realm per test
				DefaultRealm realm = new DefaultRealm();
				
				try {
					base.evaluate();
				} finally {
					realm.dispose();
				}
			}
		};
	}

	/**
	 * Simple realm implementation that will set itself as default when
	 * constructed. Invoke {@link #dispose()} to remove the realm from being the
	 * default. Does not support asyncExec(...).
	 * 
	 * Class copied from https://wiki.eclipse.org/JFace_Data_Binding/Realm
	 */
	private static class DefaultRealm extends Realm {
		private Realm previousRealm;

		public DefaultRealm() {
			previousRealm = super.setDefault(this);
		}

		/**
		 * @return always returns true
		 */
		public boolean isCurrent() {
			return true;
		}

		protected void syncExec(Runnable runnable) {
			runnable.run();
		}

		/**
		 * @throws UnsupportedOperationException
		 */
		public void asyncExec(Runnable runnable) {
			throw new UnsupportedOperationException("asyncExec is unsupported");
		}

		/**
		 * Removes the realm from being the current and sets the previous realm
		 * to the default.
		 */
		public void dispose() {
			if (getDefault() == this) {
				setDefault(previousRealm);
			}
		}
	}
}
