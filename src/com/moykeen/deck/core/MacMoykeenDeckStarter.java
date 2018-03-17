package com.moykeen.deck.core;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.manisma.base.Starter;
import com.manisma.base.core.Application;
import com.manisma.base.core.Application.Platform;
import com.mcdermottroe.apple.OSXKeychain;
import com.mcdermottroe.apple.OSXKeychainException;


public class MacMoykeenDeckStarter extends Starter implements InvocationHandler {
	

	protected Object targetObject;
	protected Method targetMethod;
	protected String proxySignature;

	static Object macOSXApplication;
	
	
	/**
	 * This is the main method.
	 */
	public static void main(String [] args){

		Application app = MoykeenDeckApp.getInstance();
		app.setPlatform(Platform.MAC_OS_X);

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", app.getApplicationName());

		try {
            UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
		} catch (Exception error) {
			System.out.println("ui error");
		}
		
		// Install Mac OS X menu handlers
		try {
			MacMoykeenDeckStarter.setQuitHandler(app, MoykeenDeckApp.class.getDeclaredMethod("shutdown", (Class[]) null));
			MacMoykeenDeckStarter.setAboutHandler(app, MoykeenDeckApp.class.getDeclaredMethod("showAboutBox", (Class[]) null));
			MacMoykeenDeckStarter.setPreferencesHandler(app, MoykeenDeckApp.class.getDeclaredMethod("showPreferences", (Class[]) null));
			MacMoykeenDeckStarter.setFileHandler(app, MoykeenDeckApp.class.getDeclaredMethod("handleOpenFile", String.class));
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			e.printStackTrace();
		}		

		startApplication(app);

	}
	
	
	// Pass this method an Object and Method equipped to perform application
		// shutdown logic
		// The method passed should return a boolean stating whether or not the quit
		// should occur
		public static void setQuitHandler(Object target, Method quitHandler) {
			setHandler(new MacMoykeenDeckStarter("handleQuit", target, quitHandler));
		}

		// Pass this method an Object and Method equipped to display application
		// info
		// They will be called when the About menu item is selected from the
		// application menu
		public static void setAboutHandler(Object target, Method aboutHandler) {
			boolean enableAboutMenu = (target != null && aboutHandler != null);
			if (enableAboutMenu) {
				setHandler(new MacMoykeenDeckStarter("handleAbout", target, aboutHandler));
			}
			// If we're setting a handler, enable the About menu item by calling
			// com.apple.eawt.Application reflectively
			try {
				Method enableAboutMethod = macOSXApplication.getClass()
						.getDeclaredMethod("setEnabledAboutMenu",
								new Class[] { boolean.class });
				enableAboutMethod.invoke(macOSXApplication, new Object[] { Boolean
						.valueOf(enableAboutMenu) });
			} catch (Exception error) {
				System.out.println("MacMoykeenDeckStarter could not access the About Menu");
			}
		}

		// Pass this method an Object and a Method equipped to display application
		// options
		// They will be called when the Preferences menu item is selected from the
		// application menu
		public static void setPreferencesHandler(Object target, Method prefsHandler) {
			boolean enablePrefsMenu = (target != null && prefsHandler != null);
			if (enablePrefsMenu) {
				setHandler(new MacMoykeenDeckStarter("handlePreferences", target, prefsHandler));
			}
			// If we're setting a handler, enable the Preferences menu item by
			// calling
			// com.apple.eawt.Application reflectively
			try {
				Method enablePrefsMethod = macOSXApplication.getClass()
						.getDeclaredMethod("setEnabledPreferencesMenu",
								new Class[] { boolean.class });
				enablePrefsMethod.invoke(macOSXApplication, new Object[] { Boolean
						.valueOf(enablePrefsMenu) });
			} catch (Exception error) {
				System.out.println("MacMoykeenDeckStarter could not access the About Menu");
			}
		}

		// Pass this method an Object and a Method equipped to handle document
		// events from the Finder
		// Documents are registered with the Finder via the CFBundleDocumentTypes
		// dictionary in the
		// application bundle's Info.plist
		public static void setFileHandler(Object target, Method fileHandler) {
			setHandler(new MacMoykeenDeckStarter("handleOpenFile", target, fileHandler) {
				// Override MacMoykeenDeckStarter.callTarget to send information on the
				// file to be opened
				public boolean callTarget(Object appleEvent) {
					if (appleEvent != null) {
						try {
							Method getFilenameMethod = appleEvent.getClass()
									.getDeclaredMethod("getFilename",
											(Class[]) null);
							String filename = (String) getFilenameMethod.invoke(
									appleEvent, (Object[]) null);
							this.targetMethod.invoke(this.targetObject,
									new Object[] { filename });
						} catch (Exception ex) {

						}
					}
					return true;
				}
			});
		}

		// setHandler creates a Proxy object from the passed MacMoykeenDeckStarter and adds it
		// as an ApplicationListener
		public static void setHandler(MacMoykeenDeckStarter adapter) {
			try {
				Class<?> applicationClass = Class
						.forName("com.apple.eawt.Application");
				if (macOSXApplication == null) {
					macOSXApplication = applicationClass.getConstructor(
							(Class[]) null).newInstance((Object[]) null);
				}
				Class<?> applicationListenerClass = Class
						.forName("com.apple.eawt.ApplicationListener");
				Method addListenerMethod = applicationClass.getDeclaredMethod(
						"addApplicationListener",
						new Class[] { applicationListenerClass });
				// Create a proxy object around this handler that can be
				// reflectively added as an Apple ApplicationListener
				Object MacMoykeenDeckStarterProxy = Proxy.newProxyInstance(MacMoykeenDeckStarter.class
						.getClassLoader(),
						new Class[] { applicationListenerClass }, adapter);
				addListenerMethod.invoke(macOSXApplication,
						new Object[] { MacMoykeenDeckStarterProxy });
			} catch (ClassNotFoundException error) {
				System.out.println("error.");
			} catch (Exception error) { // Likely a NoSuchMethodException or an
				// IllegalAccessException loading/invoking
				// eawt.Application methods
				System.out.println("error.");
			}
		}
		
		// Each MacMoykeenDeckStarter has the name of the EAWT method it intends to listen for
		// (handleAbout, for example),
		// the Object that will ultimately perform the task, and the Method to be
		// called on that Object
		protected MacMoykeenDeckStarter(String proxySignature, Object target, Method handler) {
			this.proxySignature = proxySignature;
			this.targetObject = target;
			this.targetMethod = handler;
		}

		// Override this method to perform any operations on the event
		// that comes with the various callbacks
		// See setFileHandler above for an example
		public boolean callTarget(Object appleEvent)
				throws InvocationTargetException, IllegalAccessException {
			Object result = targetMethod.invoke(targetObject, (Object[]) null);
			if (result == null) {
				return true;
			}
			return Boolean.valueOf(result.toString()).booleanValue();
		}

		// InvocationHandler implementation
		// This is the entry point for our proxy object; it is called every time an
		// ApplicationListener method is invoked
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (isCorrectMethod(method, args)) {
				boolean handled = callTarget(args[0]);
				setApplicationEventHandled(args[0], handled);
			}
			// All of the ApplicationListener methods are void; return null
			// regardless of what happens
			return null;
		}

		// Compare the method that was called to the intended method when the
		// MacMoykeenDeckStarter instance was created
		// (e.g. handleAbout, handleQuit, handleOpenFile, etc.)
		protected boolean isCorrectMethod(Method method, Object[] args) {
			return (targetMethod != null && proxySignature.equals(method.getName()) && args.length == 1);
		}

		// It is important to mark the ApplicationEvent as handled and cancel the
		// default behavior
		// This method checks for a boolean result from the proxy method and sets
		// the event accordingly
		protected void setApplicationEventHandled(Object event, boolean handled) {
			if (event != null) {
				try {
					Method setHandledMethod = event.getClass().getDeclaredMethod(
							"setHandled", new Class[] { boolean.class });
					// If the target method returns a boolean, use that as a hint
					setHandledMethod.invoke(event, new Object[] { Boolean
							.valueOf(handled) });
				} catch (Exception error) {
					System.out.println("error");
				}
			}
		}


		

}
