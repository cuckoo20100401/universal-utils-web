package org.cuckoo.universal.utils.db;

import org.cuckoo.universal.utils.StringUtils;

public class EntityTableTransformUtils {
	
	public static String fromTableNameToEntityName(String tableName) {
		return StringUtils.fromKebabCaseToPascalCase(tableName);
	}
	
	public static String fromTableColumnNameToEntityPropertyName(String tableColumnName) {
		String result = StringUtils.fromKebabCaseToPascalCase(tableColumnName);
		return StringUtils.makeFirstCharToLowerCase(result);
	}

	public static String fromEntityNameToTableName(String entityName) {
		return StringUtils.fromPascalCaseToKebabCase(entityName);
	}
	
	public static String fromEntityPropertyNameToTableColumnName(String entityPropertyName) {
		return StringUtils.fromPascalCaseToKebabCase(entityPropertyName);
	}
	
	public static String fromEntityPropertyNameToGetMethodName(String entityPropertyName) {
		return "get" + StringUtils.makeFirstCharToUpperCase(entityPropertyName);
	}
	
	public static String fromEntityPropertyNameToSetMethodName(String entityPropertyName) {
		return "set" + StringUtils.makeFirstCharToUpperCase(entityPropertyName);
	}
}
