package anti.drop.device.pojo;

public class DeviceBean {

	public int _id;
	public String address;// ������ַ
	public String name;// ��������
	public int status;// ����״̬[12 (0x0000000c):�Ѿ�ƥ�䣬11 (0x0000000b):ƥ�����ڽ�����;10
	public String bell = "����1";// Ĭ������

	// (0x0000000a):δ��ƥ��]
	public DeviceBean() {
	}

	public DeviceBean(String address, String name, int status,String bell) {
		this.address = address;
		this.name = name;
		this.status = status;
		this.bell = bell;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBell() {
		return bell;
	}

	public void setBell(String bell) {
		this.bell = bell;
	}

}
