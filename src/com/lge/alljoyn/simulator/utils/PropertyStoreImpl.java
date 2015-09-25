package com.lge.alljoyn.simulator.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.services.common.PropertyStore;
import org.alljoyn.services.common.PropertyStoreException;


public class PropertyStoreImpl  implements PropertyStore {

	public static class Property {
		private final boolean m_isLocalized;
		private final boolean m_isWritable;
		private final boolean m_isAnnounced;
		private final boolean m_isPublic;
		private final String m_name;
		private Object m_object = null;
		private Map<String, String> m_localizable_value = null;
		// public.write.announce

		public Property(String m_name, Object value, boolean isPublic, boolean isWritable, boolean isAnnounced) {
			super();
			this.m_isLocalized = false;
			this.m_isWritable = isWritable;
			this.m_isAnnounced = isAnnounced;
			this.m_isPublic = isPublic;
			this.m_name = m_name;
			this.m_object = value;
		}

		/*
		 * localizable property
		 */
		public Property(String m_name, String value, String languageTag, boolean isPublic, boolean isWritable,
				boolean isAnnounced) {
			this.m_isLocalized = true;
			this.m_isWritable = isWritable;
			this.m_isAnnounced = isAnnounced;
			this.m_isPublic = isPublic;
			this.m_name = m_name;
			if (this.m_localizable_value == null) {
				this.m_localizable_value = new HashMap<String, String>();
			}
			this.m_localizable_value.put(languageTag, value);
		}

		public void addLocalizedValue(String value, String languageTag) {
			if (this.m_isLocalized == false) {
				return;
			}
			if (this.m_localizable_value == null) {
				this.m_localizable_value = new HashMap<String, String>();
			}
			this.m_localizable_value.put(languageTag, value);
		}

		public boolean isLocalized() {
			return m_isLocalized;
		}

		public boolean isWritable() {
			return m_isWritable;
		}

		public boolean isAnnounced() {
			return m_isAnnounced;
		}

		public boolean isPublic() {
			return m_isPublic;
		}

		public String getName() {
			return m_name;
		}

		public Object getObject() {
			return m_object;
		}

		public Object getObject(String languageTag) {
			return (Object) m_localizable_value.get(languageTag);
		}
	}

	private Map<String, List<Property>> m_internalMap = null;

	public PropertyStoreImpl(Map<String, List<Property>> dataMap) {
		m_internalMap = dataMap;
	}

	public void readAll(String languageTag, Filter filter, Map<String, Object> dataMap)
			throws PropertyStoreException {
		if (filter == Filter.ANNOUNCE) {
			if (m_internalMap != null) {
				List<Property> langauge = m_internalMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
				if (langauge != null) {
					languageTag = (String) langauge.get(0).getObject();
				} else {
					throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
				}

				Set<Map.Entry<String, List<Property>>> entries = m_internalMap.entrySet();
				for (Map.Entry<String, List<Property>> entry : entries) {
					String key = entry.getKey();
					List<Property> properyList = entry.getValue();
					for (int i = 0; i < properyList.size(); i++) {
						Property property = properyList.get(i);
						if (!property.isAnnounced())
							continue;
						if (property.isLocalized()) {
							dataMap.put(key, property.getObject(languageTag));
						} else {
							dataMap.put(key, property.getObject());
						}
					}
				}
			} else {
				throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_KEY);
			}
		} else if (filter == Filter.READ) {
			if (languageTag != null && languageTag.length() > 1) {
				List<Property> supportedLanguages = m_internalMap.get(AboutKeys.ABOUT_SUPPORTED_LANGUAGES);
				if (supportedLanguages == null)
					throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_KEY);
				if (!(supportedLanguages.get(0).getObject() instanceof Set<?>)) {
					throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
				} else {
					@SuppressWarnings("unchecked")
					Set<String> languages = (Set<String>) supportedLanguages.get(0).getObject();
					if (!languages.contains(languageTag)) {
						throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
					}
				}
			} else {

				List<Property> langauge = m_internalMap.get(AboutKeys.ABOUT_DEFAULT_LANGUAGE);
				if (langauge != null) {
					languageTag = (String) langauge.get(0).getObject();
				} else {
					throw new PropertyStoreException(PropertyStoreException.UNSUPPORTED_LANGUAGE);
				}
			}
			Set<Map.Entry<String, List<Property>>> entries = m_internalMap.entrySet();
			for (Map.Entry<String, List<Property>> entry : entries) {
				String key = entry.getKey();
				List<Property> properyList = entry.getValue();
				for (int i = 0; i < properyList.size(); i++) {
					Property property = properyList.get(i);
					if (!property.isPublic())
						continue;
					if (property.isLocalized()) {
						dataMap.put(key, property.getObject(languageTag));
					} else {
						dataMap.put(key, property.getObject());
					}
				}
			}
		} // end of read.
		else
			throw new PropertyStoreException(PropertyStoreException.ILLEGAL_ACCESS);
	}

	public void update(String key, String languageTag, Object newValue) throws PropertyStoreException {
	}

	public void reset(String key, String languageTag) throws PropertyStoreException {
	}

	public void resetAll() throws PropertyStoreException {
	}
}
