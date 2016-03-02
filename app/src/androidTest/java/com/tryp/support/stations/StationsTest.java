package com.tryp.support.stations;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by cliffroot on 02.03.16.
 */

@RunWith(AndroidJUnit4.class)
@SmallTest
public class StationsTest {

    private static final String PACKAGE = "com.tryp.support";
    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(PACKAGE);
        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    @Test
    public void showsMap () {
        assertTrue(mDevice.findObject(new UiSelector()
                .descriptionContains("Google Map")).exists());
    }

    @Test
    public void hasMyLocation () throws UiObjectNotFoundException {
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("me"));
        marker.click();
    }

    @Test
    public void rotatesCorrectly () throws RemoteException {
        mDevice.setOrientationLeft();

        assertTrue(mDevice.findObject(new UiSelector()
                .descriptionContains("Google Map")).exists());

        mDevice.unfreezeRotation();
        mDevice.setOrientationRight();

        assertTrue(mDevice.findObject(new UiSelector()
                .descriptionContains("Google Map")).exists());

        mDevice.unfreezeRotation();
        mDevice.setOrientationNatural();
        assertTrue(mDevice.findObject(new UiSelector()
                .descriptionContains("Google Map")).exists());
        mDevice.unfreezeRotation();
    }

    @Test
    public void showsBriefDetails () throws  UiObjectNotFoundException {
        UiObject marker = mDevice.findObject(new UiSelector().descriptionContains("Газовик"));
        marker.click();
        assertTrue(mDevice.findObject(new UiSelector().textContains("SEE MORE")).exists());
        mDevice.findObject(new UiSelector().descriptionContains("Google Map")).clickTopLeft();
        mDevice.findObject(new UiSelector().textContains("SEE MORE")).waitUntilGone(1000);
        assertFalse(mDevice.findObject(new UiSelector().textContains("SEE MORE")).exists());
    }

    @After
    public void cleanup () throws RemoteException {
        mDevice.unfreezeRotation();
    }


}
