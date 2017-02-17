/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 */

package au.com.tyo.android;


import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Eric Tang <eric.tang@tyo.com.au>
 */


public class CommonInitializer {

	/**
	 * The class of controller / not the controller interface
	 */

	public static Class clsController = null;

	/**
	 * The class of controller interface
	 */

	public static Class clsControllerInterface = null;

	/**
	 * let CommonApp class to deal with empty ui class
	 */
	public static Class clsUi = null; // UIBase.class;

	//public static Class clsData; // normally we don't have this

	/**
	 * @param theClass
	 */
	public static Object initializeInstance(Class<?> theClass) {
		return initializeInstance(theClass, null);
	}

	/**
	 * Initialize the static instance, no dat initialization for those should be left till in a background thread
	 *
	 * @param theClass
	 * @param context
	 */
	public static Object initializeInstance(Class<?> theClass, Context context) {
		return initializeInstance(theClass, context, true, false);
	}

	/**
	 * @param theClass
	 * @param context
	 * @param initializeBackground
	 */
	public static Object initializeInstance(Class<?> theClass, Context context, boolean initializeBackground) {
		return initializeInstance(theClass, context, true, initializeBackground);
	}

	/**
	 * @param theClass
	 * @param context
	 * @param initializeMain
	 * @param initializeBackground
	 */
	public static Object initializeInstance(Class<?> theClass, Context context, boolean initializeMain, boolean initializeBackground) {
		Object instance = null;

		try {
			if (null != context) {
				Constructor ctor = theClass.getConstructor(Context.class/*Classes.clsController*/);
				instance = ctor.newInstance(new Object[]{context});
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} finally {
			if (instance == null)
				try {
					instance = theClass.newInstance();
				} catch (InstantiationException e) {
				} catch (IllegalAccessException e) {
				}
		}

		CommonController ca = (CommonController) instance;
		if (context != null) {
			if (ca.getContext() == null)
				ca.setContext(context);

			if (initializeMain)
				ca.initializeInMainThread(context);

			if (initializeBackground)
				ca.initializeInBackgroundThread(context);
		}

		return instance;
	}

	public static Object initializeController(Context context) {
		return initializeController(context, true, false);
	}

	public static Object initializeController(Context context, boolean initializeMain, boolean initializeBackground) {
		return initializeInstance(clsController, context, initializeMain, initializeBackground);
	}
}
