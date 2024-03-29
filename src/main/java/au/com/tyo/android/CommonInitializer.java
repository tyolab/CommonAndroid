/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 */

package au.com.tyo.android;


import android.content.Context;
import android.util.Log;

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
	public static Class classMainActivity;

	/**
	 *
	 */
	public static Class splashScreenClass;

	/**
	 *
	 */
	public static Class clsSettings;
	
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
		// nothing yet
	}

	public static void detectDefaultClasses(Context context) {
		Log.i(LOG_TAG, "Detecting classes...");

		String appPackage = context.getResources().getString(R.string.tyodroid_app_package);
		if (null == appPackage || appPackage.length() == 0)
			appPackage = CommonInitializer.appPackage;
		CommonInitializer.detectDefaultClasses(appPackage == null ? context.getPackageName() : appPackage);
	}

	/**
	 * WARN:
	 *
	 * Due to obfuscation during generating the production code
	 * The class name will be changed
	 *
	 *
	 * @param packageName
	 */
    public static void detectDefaultClasses(String packageName) {
        String[] classNames = createDefaultClassNames(packageName);

        for (String clsName : classNames) {
            Class cls = null;

			String classStr = packageName + "." + clsName;
            try {
                cls = Class.forName(classStr);
            }
            catch (Exception ex) {
            	Log.w(LOG_TAG, "Failed to get class definition from: " + classStr);
			}

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
						if (null == classMainActivity)
                        	classMainActivity = cls;
                        break;
                    case APP_ACTIVITY_PREFERENCE:
						if (null == preferenceActivityClass)
                        	preferenceActivityClass = cls;
                        break;
                    case SETTINGS:
						if (null == clsSettings)
                        	clsSettings = cls;
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
	public static <T extends CommonController> T initializeInstance(Class<T> theClass) {
		return initializeInstance(theClass, null);
	}

	/**
	 * Initialize the static instance, no dat initialization for those should be left till in a background thread
	 *
	 * @param theClass
	 * @param context
	 */
	public static <T extends CommonController> T  initializeInstance(Class<T> theClass, Context context) {
		return initializeInstance(theClass, context, true, false);
	}

	/**
	 * @param theClass
	 * @param context
	 * @param initializeBackground
	 */
	public static <T extends CommonController> T initializeInstance(Class<T> theClass, Context context, boolean initializeBackground) {
		return initializeInstance(theClass, context, true, initializeBackground);
	}

	/**
	 * Be very careful, optimization will renamed all the methods including constructors
	 *
	 * @param theClass
	 * @param context
	 * @param initializeMain
	 * @param initializeBackground
	 */
	public static <T extends CommonController> T initializeInstance(Class<T> theClass, Context context, boolean initializeMain, boolean initializeBackground) {
		T instance = null;
		String errorMessage = "Failed to create class instance: " + theClass.getName();

		Log.i(LOG_TAG, "Creating controller's instance by calling the constructor, main: " + initializeMain + ", background: " + initializeBackground);

		try {
			if (null != context) {
				Constructor<T> ctor = theClass.getConstructor(Context.class/*Classes.clsController*/);
				instance = ctor.newInstance(new Object[]{context});
			}
		} catch (InstantiationException e) {
			Log.e(LOG_TAG, errorMessage, e);
		} catch (IllegalAccessException e) {
            Log.e(LOG_TAG, errorMessage, e);
		} catch (NoSuchMethodException e) {
            Log.e(LOG_TAG, errorMessage, e);
		} catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, errorMessage, e);
		} catch (InvocationTargetException e) {
            Log.e(LOG_TAG, errorMessage, e);
		} finally {
			if (instance == null)
				try {
					instance = theClass.newInstance();
				} catch (InstantiationException e) {
                    Log.e(LOG_TAG, errorMessage, e);
				} catch (IllegalAccessException e) {
                    Log.e(LOG_TAG, errorMessage, e);
				}
		}

		if (null != instance) {
			CommonController ca = (CommonController) instance;
			if (context != null) {
				if (ca.getContext() == null)
					ca.setContext(context);

				if (initializeMain)
					ca.initializeInMainThread(context);

				if (initializeBackground)
					ca.initializeInBackgroundThread(context);
			}
		}
		else
			Log.e(LOG_TAG, "Initialise controller implementation class error: " + theClass.getName());

		return instance;
	}

	public static Object initializeController(Context context) {
		return initializeController(context, true, false);
	}

	public static Object initializeController(Context context, boolean initializeMain, boolean initializeBackground) {
        if (clsController == null)
            detectDefaultClasses(context);

        if (clsController == null)
        	throw new IllegalStateException("Cannot find the class type of controller interface");

		return initializeInstance(clsController, context, initializeMain, initializeBackground);
	}

	public static Object newInstanceWithContext(Class cls, Context context) {
        Object instance = null;
        Exception exception = null;
        try {
            if (null != context) {
                Constructor ctor = cls.getConstructor(Context.class);
                instance = ctor.newInstance(new Object[]{context});
            }
        }
        catch (InstantiationException e) {
            exception = e;
        }
        catch (IllegalAccessException e) {
            exception = e;
        }
        catch (NoSuchMethodException e) {
            exception = e;
        }
        catch (IllegalArgumentException e) {
            exception = e;
        }
        catch (InvocationTargetException e) {
            exception = e;
        }
        finally {
            if (null != exception)
                Log.e(LOG_TAG, "Failed to create instance (" + cls.getName() + ")", exception);

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
        if (null == clsSettings)
            return null;

        return newInstanceWithContext(clsSettings, context);
    }
}
