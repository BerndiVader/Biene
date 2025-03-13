package com.gmail.berndivader.biene.enums;

public
enum
Tasks
{
	UPDATE_BIENE_DATABASE("MSSQL Query Winline zu XTC"),
	
	HTTP_POST_UPLOAD_CSV_FILE("csv_upload"),
	HTTP_POST_IMPORT_CSV_FILE("csv_import"),
	HTTP_POST_UPDATE_PICTURES("img_process"),
	HTTP_POST_IMAGE_UPLOAD("img_upload"),
	HTTP_POST_IMAGE_VALIDATE("img_validate"),
	HTTP_POST_IMAGE_VALIDATE_FILE("img_validate_file"),
	
	HTTP_GET_VERSION("version"),
	HTTP_GET_PRODUCTS_PRINTOUT("products_export"),
	HTTP_GET_ORDERS_PRINTOUT("orders_export"),
	HTTP_GET_CUSTOMERS_PRINTOUT("customers"),
		
	DB_GET_WINLINE("get_winline"),
	DB_VALIDATE_PICTURE("validate_picture"),
	DB_STEUERCODE("get_steuercode"),
	
	DB_QUERY_OK("db_query_ok"),
	DB_QUERY_FAILED("db_query_failed"),
	
	VARIOUS("various"),
    UNKOWN("unkown"),
	ERROR("error"),
	INVALID("invalid");
	

	private static final String GET="?action=";
	private String command;
	
	private Tasks(String command) {
		this.command=command;
	}
	
	public String action() {
		return command;
	}
	
	public String command() {
		return GET.concat(command);
	}
	
	/**
	 * Get Task for String name. Ignores case sensitive.<br>
	 * Throws IllegalArgumentException if name not in Enumeration.
	 * 
	 * @param name
	 * @return Task Object for string name.
	 * 
	 */
	
	public static Tasks valueOfIgnoreCase(String name) {
		for (Tasks e:Tasks.values()) {
			if(e.action().equalsIgnoreCase(name)) return e;
		}
	    throw new IllegalArgumentException(String.format("There is no value with name '%s' in Enum %s",name,Tasks.class.getName()));
	}	
	
	
}
