package applet.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.tree.DefaultMutableTreeNode;

import org.swixml.Localizer;

import applet.config.annotations.ConfigClass;
import applet.config.annotations.ConfigMethod;

public class ConfigNode extends DefaultMutableTreeNode {
	private Object objectToInvokeMethod;
	private Localizer localizer;

	/**
	 * A config node is displayed in the config viewer.
	 * 
	 * @param methodObject
	 *            object to invoke getter/setter methods on.
	 * @param userObject
	 *            method, field or object
	 * @param localizer
	 *            localizer to localize language dependent messages
	 */
	public ConfigNode(Object methodObject, Object userObject,
			Localizer localizer) {
		// while user object is a field or method or object (List item)...
		super(userObject);
		// ...this object can be used to invoke methods
		this.objectToInvokeMethod = methodObject;
		// localizer for bundle keys
		this.localizer = localizer;
	}

	public Object getMethodObject() {
		return objectToInvokeMethod;
	}

	/**
	 * Set a field value of the configuration. A setter method is invoked for
	 * that field and setting the parameterized value.
	 * 
	 * @param value
	 *            value to set
	 */
	public <T> void setValue(T value) {
		Field field = (Field) getUserObject();
		String getterMethod = getGetterMethod(field, false);
		try {
			Method method = objectToInvokeMethod.getClass().getMethod(
					getterMethod, field.getType());
			method.invoke(objectToInvokeMethod, value);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Could not set Value: %s for field %s", value,
					field.getName()));
		}
	}

	/**
	 * Get a fields value of the configuration. A getter method is invoked for
	 * that field.
	 * 
	 * @return value returned by the getter method
	 */
	public Object getValue() {
		Field field = (Field) getUserObject();
		String getterMethod = getGetterMethod(field, true);
		try {
			Method method = objectToInvokeMethod.getClass().getMethod(
					getterMethod);
			return method.invoke(objectToInvokeMethod);
		} catch (Exception e) {
			throw new RuntimeException(String.format(
					"Could not get Value: for field %s", field.getName()));
		}
	}

	/**
	 * Get the getter/setter methods name (getXXX or isXXX or setXXX) based on
	 * the fields type.
	 * 
	 * @param field
	 *            field to obtain a getter method for.
	 * @param isGetter
	 *            obtain getter method (true) or setter method (false)
	 * @return getter/setter methods name
	 */
	static String getGetterMethod(Field field, boolean isGetter) {
		String methodName = Character.toUpperCase(field.getName().charAt(0))
				+ field.getName().substring(1);
		if (!isGetter) {
			return "set" + methodName;
		}
		if (field.getType().getName().equals("boolean")
				|| (field.getType().getPackage() != null && field.getType()
						.getPackage().getName().equals("java.lang.Boolean"))) {
			return "is" + methodName;
		} else {
			return "get" + methodName;
		}
	}

	@Override
	public String toString() {
		if (getUserObject() instanceof String) {
			// Strings are displayed as is
			return (String) getUserObject();
		} else if (getUserObject() instanceof Field) {
			// Fields are displayed using field name and value
			Field field = (Field) getUserObject();
			return field.getName() + "=" + getValue();
		} else if (getUserObject() instanceof Method) {
			// Methods are displayed using annotation at getter method level
			Method method = (Method) getUserObject();
			ConfigMethod uiConfig = method.getAnnotation(ConfigMethod.class);
			if (uiConfig != null) {
				return localizer.getString(uiConfig.getBundleKey());
			}
		} else {
			// Other objects are displayed using annotation at class level
			ConfigClass uiConfig = getUserObject().getClass().getAnnotation(
					ConfigClass.class);
			if (uiConfig != null) {
				return localizer.getString(uiConfig.getBundleKey());
			}
		}
		// Annotation is missing!
		return "?" + getMethodObject().getClass().getSimpleName() + "_"
				+ getUserObject().getClass().getName() + "?";
	}

}
