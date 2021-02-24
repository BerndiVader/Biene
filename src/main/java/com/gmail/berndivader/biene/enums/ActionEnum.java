package com.gmail.berndivader.biene.enums;

public
enum
ActionEnum
{
	UPDATE_BIENE_DATABASE("MSSQL Query Winline zu XTC"),
	
	UPLOAD_CSV_FILE("csv_upload"),
	IMPORT_CSV_FILE("csv_import"),
	UPDATE_PICTURES("img_process"),
	IMAGE_UPLOAD("img_upload"),
	IMAGE_VALIDATE("img_validate"),
	IMAGE_VALIDATE_FILE("img_validate_file"),
	
	ERROR("error");
	
	private String action;
	
	private ActionEnum(String action) {
		this.action=action;
	}
	
	public String action() {
		return action;
	}
	
	public static ActionEnum valueOfIgnoreCase(String name) {
		for (ActionEnum e:ActionEnum.values()) {
			if (e.action().equalsIgnoreCase(name)) return e;
		}
	    throw new IllegalArgumentException(String.format("There is no value with name '%s' in Enum %s",name,ActionEnum.class.getName()));
	}	
	
}
