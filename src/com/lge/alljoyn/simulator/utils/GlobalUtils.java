package com.lge.alljoyn.simulator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.services.common.PropertyStore;

import com.lge.alljoyn.simulator.about.DeviceAboutObject;

public class GlobalUtils {
	
	
	public static PropertyStore getAboutData(DeviceAboutObject aboutObj) {
		Map<String, List<PropertyStoreImpl.Property>> data = new HashMap<String, List<PropertyStoreImpl.Property>>();

		// String name = "Plug-Dryer";

		data.put(AboutKeys.ABOUT_DEFAULT_LANGUAGE, new ArrayList<PropertyStoreImpl.Property>(Arrays
				.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_DEFAULT_LANGUAGE, "en", true, true, true))));
		PropertyStoreImpl.Property deviceName = new PropertyStoreImpl.Property(AboutKeys.ABOUT_DEVICE_NAME,
				aboutObj.getDeviceName(), "en", true, false, true);
		data.put(AboutKeys.ABOUT_DEVICE_NAME, new ArrayList<PropertyStoreImpl.Property>(Arrays.asList(deviceName)));

		data.put(AboutKeys.ABOUT_DEVICE_ID, new ArrayList<PropertyStoreImpl.Property>(
				Arrays.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_DEVICE_ID,
						aboutObj.getDeviceName() + aboutObj.get_id(), true, false, true))));//

		data.put(AboutKeys.ABOUT_DESCRIPTION, new ArrayList<PropertyStoreImpl.Property>(Arrays
				.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_DESCRIPTION, "test", true, false, false))));

		final UUID uid = UUID.randomUUID();// .fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
		data.put(AboutKeys.ABOUT_APP_ID, new ArrayList<PropertyStoreImpl.Property>(
				Arrays.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_APP_ID, uid, true, false, true))));

		PropertyStoreImpl.Property appName1 = new PropertyStoreImpl.Property(AboutKeys.ABOUT_APP_NAME,
				aboutObj.getDeviceId(), "en", true, false, true);
		data.put(AboutKeys.ABOUT_APP_NAME, new ArrayList<PropertyStoreImpl.Property>(Arrays.asList(appName1)));

		PropertyStoreImpl.Property manufacture = new PropertyStoreImpl.Property(AboutKeys.ABOUT_MANUFACTURER, "LGE",
				"en", true, false, true);
		data.put(AboutKeys.ABOUT_MANUFACTURER, new ArrayList<PropertyStoreImpl.Property>(Arrays.asList(manufacture)));

		data.put(AboutKeys.ABOUT_MODEL_NUMBER, new ArrayList<PropertyStoreImpl.Property>(
				Arrays.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_MODEL_NUMBER, aboutObj.getDeviceId(), true,
						false, false))));

		data.put(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new ArrayList<PropertyStoreImpl.Property>(Arrays
				.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_SUPPORTED_LANGUAGES, new HashSet<String>() {
					{
						add("en");
					}
				}, true, false, false))));

		PropertyStoreImpl.Property description = new PropertyStoreImpl.Property(AboutKeys.ABOUT_DESCRIPTION,
				"test simulator", "en", true, false, false);
		data.put(AboutKeys.ABOUT_DESCRIPTION, new ArrayList<PropertyStoreImpl.Property>(Arrays.asList(description)));

		data.put(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, new ArrayList<PropertyStoreImpl.Property>(
				Arrays.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_DATE_OF_MANUFACTURE, "19/02/2016", true,
						false, false))));

		data.put(AboutKeys.ABOUT_SOFTWARE_VERSION, new ArrayList<PropertyStoreImpl.Property>(Arrays
				.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_SOFTWARE_VERSION, "1.0", true, false, false))));

		data.put(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, new ArrayList<PropertyStoreImpl.Property>(Arrays.asList(
				new PropertyStoreImpl.Property(AboutKeys.ABOUT_AJ_SOFTWARE_VERSION, "14.12", true, false, false))));

		data.put(AboutKeys.ABOUT_HARDWARE_VERSION, new ArrayList<PropertyStoreImpl.Property>(Arrays
				.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_HARDWARE_VERSION, "1.0", true, false, false))));

		data.put(AboutKeys.ABOUT_SUPPORT_URL, new ArrayList<PropertyStoreImpl.Property>(
				Arrays.asList(new PropertyStoreImpl.Property(AboutKeys.ABOUT_SUPPORT_URL, "", true, false, false))));

		PropertyStore propertyStore = new PropertyStoreImpl(data);
		return propertyStore;
	}
}
