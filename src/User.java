import java.io.Serializable;


public class User implements Serializable
{
	/**
	 * 
	 *  用户对象,包含用户账号、密码、性别、年龄、地区、以及图像和QQ秀等信息
	 *  作者:戴睿轩
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userID;
	private String passWord;
	private String userName;
	private String sexstr;
	private String age ;
	private String placestr;
	private String pic;
	private String show;
	private String signature;
	private String LOGINSTATE;
	private String group;
	private String groupin;
	private String recv;
	private QQchatframe talk;
	
	public User(){}
	
	
	public User(String userID, String passWord, String userName, String sexstr,
			String age, String placestr, String pic, String show,
			String signature) {
		super();
		this.userID = userID;
		this.passWord = passWord;
		this.userName = userName;
		this.sexstr = sexstr;
		this.age = age;
		this.placestr = placestr;
		this.pic = pic;
		this.show = show;
		this.signature = signature;
	}


	public User(String userID, String passWord, String userName, String sexstr,
			String age, String placestr, String pic, String show,
			String signature, String lOGINSTATE, String group) {
		super();
		this.userID = userID;
		this.passWord = passWord;
		this.userName = userName;
		this.sexstr = sexstr;
		this.age = age;
		this.placestr = placestr;
		this.pic = pic;
		this.show = show;
		this.signature = signature;
		this.LOGINSTATE = lOGINSTATE;
		this.group = group;
	}


	public User(String userID, String userName, String sexstr,
			String age, String placestr, String pic, String show,
			String signature, String lOGINSTATE,String groupin) {
		super();
		this.userID = userID;
		this.userName = userName;
		this.sexstr = sexstr;
		this.age = age;
		this.placestr = placestr;
		this.pic = pic;
		this.show = show;
		this.signature = signature;
		this.LOGINSTATE = lOGINSTATE;
		this.groupin = groupin;
	}




	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getSexstr() {
		return sexstr;
	}
	public void setSexstr(String sexstr) {
		this.sexstr = sexstr;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getPlacestr() {
		return placestr;
	}
	public void setPlacestr(String placestr) {
		this.placestr = placestr;
	}
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}


	public String getSignature() {
		return signature;
	}


	public void setSignature(String signature) {
		this.signature = signature;
	}


	@Override
	public String toString() {
		return "User [userID=" + userID + ", userName=" + userName
				+ ", sexstr=" + sexstr + ", age=" + age + ", placestr="
				+ placestr + ", pic=" + pic + ", show=" + show + ", signature="
				+ signature + ", groupin=" + groupin + "]";
	}


	
	public String getGroupin() {
		return groupin;
	}


	public void setGroupin(String groupin) {
		this.groupin = groupin;
	}


	public void setLOGINSTATE(String lOGINSTATE) {
		LOGINSTATE = lOGINSTATE;
	}


	public String getLOGINSTATE() {
		return LOGINSTATE;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public String getGroup() {
		return group;
	}


	public void setRecv(String recv) {
		this.recv = recv;
	}


	public String getRecv() {
		return recv;
	}


	public void setTalk(QQchatframe talk) {
		this.talk = talk;
	}


	public QQchatframe getTalk() {
		return talk;
	}
	
	

}
