/**     
 * @FileName: student.java   
 * @Package:com.test   
 * @Description: TODO  
 * @author: LUCKY    
 * @date:2016年1月11日 下午12:52:42   
 * @version V1.0     
 */
package com.test;

/**  
 * @ClassName: student   
 * @Description: TODO  
 * @author: LUCKY  
 * @date:2016年1月11日 下午12:52:42     
 */
public class student {

	private  String studentName;
	private int age;
	
	
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + age;
		result = prime * result
				+ ((studentName == null) ? 0 : studentName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		student other = (student) obj;
		if (age != other.age)
			return false;
		if (studentName == null) {
			if (other.studentName != null)
				return false;
		} else if (!studentName.equals(other.studentName))
			return false;
		return true;
	}
	
	
	
	
	
	
	@Override
	public String toString() {
		return "student [studentName=" + studentName + ", age=" + age + "]";
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
