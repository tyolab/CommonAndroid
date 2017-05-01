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
 *
 *
 *     Class will be package + "Convention" Name;
 *
 *     e.g.
 *
 *     package: com.example.app
 *     Setting class will be com.example.app.AppSettings;
 *
 */


public class CommonInitializer {

	public static final String SETTINGS = "AppSettings";

	public static final String APP = "App";

	public static final String CONTROLLER = "Controller";

	public static final String APP_UI = "AppUi";

	public static final String UI = "UI";

	public static final String APP_ACTIVITY_MAIN = "ActivityApp";

    public static final String APP_ACTIVITY_PREFERENCE = "ActivitySettings";

	/**
	 *
	 */
	public Class preferenceActivityClass;

	/**
	 *
	 */
	public Class mainActivityClass;

	/**
	 *
	 */
	public Class splashScreenClass;

	/**
	 *
	 */
	public Class settingsClass;
	
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

	/**
	 * The class of main activity
	 */
	public static Class clsActivityMain = null;

	//public static Class clsData; // normally we don't have this

    public static void detectDefaultClasses(String packageName) {
        String[] classNames = createDefaultClassNames(packageName);

        for (String clsName : classNames) {
            Class cls = null;

            try {
                cls = Class.forName(packageName + "." + clsName);
            }
            catch (Exception ex) {}

            if (null != cls)
                switch (clsName) {
                    case APP:
                        clsController = cls;
                    break;
                    case CONTROLLER:
                        clsControllerInterface = cls;
                        break;
                    case APP:

                        break;
                    case APP:

                        break;
                    case APP:

                        break;
                    case APP:

                        break;
                    case APP:

                        break;
                }
        }
    }

    private static String[] createDefaultClassNames(String packageName) {
        String[] names = new String[] {
                CONTROLLER,
                APP,
                UI,
                APP_UI,
                SETTINGS,
                APP_ACTIVITY_MAIN,
                APP_ACTIVITY_PREFERENCE
        };

        return names;
    }

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
