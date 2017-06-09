/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 */

package au.com.tyo.android;


import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import au.com.tyo.android.services.HttpAndroid;
import au.com.tyo.services.HttpPool;

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

    private static final String LOG_TAG = CommonInitializer.class.getSimpleName();

    public static String appPackage = null;

	public static final String SETTINGS = "AppSettings";

	public static final String APP = "App";

	public static final String CONTROLLER = "Controller";

	public static final String APP_UI = "AppUI";

	public static final String UI = "UI";

	public static final String APP_ACTIVITY_MAIN = "ActivityApp";

    public static final String APP_ACTIVITY_PREFERENCE = "ActivitySettings";

	/**
	 *
	 */
	public static Class preferenceActivityClass;

    /**
     * The class of main activity
     */
	public static Class mainActivityClass;

	/**
	 *
	 */
	public static Class splashScreenClass;

	/**
	 *
	 */
	public static Class settingsClass;
	
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
     * UI interface
     */
    public static Class clsUiInterface = null;

	/**
	 *
	 */
	public static Class clsData; // normally we don't have this

	/**
	 * Anything that needs to be initialised before use can be put here
	 */
	static {
        String errorMessage = "Can't use HttpAndroid for http connection";
		try {
			HttpPool.initialize(HttpAndroid.class);
		} catch (IllegalAccessException e) {
			Log.e(LOG_TAG, errorMessage);
		} catch (InstantiationException e) {
            Log.e(LOG_TAG, errorMessage);
		}
	}

	public static void detectDefaultClasses(Context context) {
		String packageName = context.getPackageName();

		String appPackage = context.getResources().getString(R.string.tyodroid_app_package);
		if (null == appPackage || appPackage.length() == 0)
			appPackage = CommonInitializer.appPackage;
		CommonInitializer.detectDefaultClasses(appPackage == null ? packageName : appPackage);
	}

	/**
	 *
	 * @param packageName
	 */
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
                    	if (null == clsController)
                        	clsController = cls;
                    break;
                    case CONTROLLER:
                    	if (null == clsControllerInterface)
                        	clsControllerInterface = cls;
                        break;
                    case APP_UI:
						if (null == clsUi)
                        	clsUi = cls;
                        break;
                    case UI:
						if (null == clsUiInterface)
                        	clsUiInterface = cls;
                        break;
                    case APP_ACTIVITY_MAIN:
						if (null == mainActivityClass)
                        	mainActivityClass = cls;
                        break;
                    case APP_ACTIVITY_PREFERENCE:
						if (null == preferenceActivityClass)
                        	preferenceActivityClass = cls;
                        break;
                    case SETTINGS:
						if (null == settingsClass)
                        	settingsClass = cls;
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
        if (clsController == null)
            detectDefaultClasses(context);

		return clsController == null ? null : initializeInstance(clsController, context, initializeMain, initializeBackground);
	}

	public static Object newInstanceWithContext(Class cls, Context context) {
        Object instance = null;
        try {
            if (null != context) {
                Constructor ctor = cls.getConstructor(Context.class);
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
                    instance = cls.newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
        }
        return instance;
    }

    public static Object newSettings(Context context) {
        if (null == settingsClass)
            return null;

        return newInstanceWithContext(settingsClass, context);
    }
}
