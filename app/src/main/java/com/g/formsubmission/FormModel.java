package com.g.formsubmission;

public class FormModel {
	
	private int _id;
	private String _id_type;
	private String _id_no;
	private String _name;
	private String _mobile_number;
	private String _email_id;

	public FormModel(){
		
	}


	public FormModel(String _id_type, String _id_no,String name,String mob,String email){
		this._id_type = _id_type;
		this._id_no = _id_no;
		this._name = name;
		this._email_id  = email;
		this._mobile_number = mob;
	}

	public FormModel(int _id,String _id_type, String _id_no,String name,String mob,String email)
	{
		this._id = _id;
		this._id_type = _id_type;
		this._id_no = _id_no;
		this._name = name;
		this._email_id  = email;
		this._mobile_number = mob;
	}
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String get_id_type() {
		return _id_type;
	}

	public void set_id_type(String _id_type) {
		this._id_type = _id_type;
	}

	public String get_id_no() {
		return _id_no;
	}

	public void set_id_no(String _id_no) {
		this._id_no = _id_no;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_mobile_number() {
		return _mobile_number;
	}

	public void set_mobile_number(String _mobile_number) {
		this._mobile_number = _mobile_number;
	}

	public String get_email_id() {
		return _email_id;
	}

	public void set_email_id(String _email_id) {
		this._email_id = _email_id;
	}
}
