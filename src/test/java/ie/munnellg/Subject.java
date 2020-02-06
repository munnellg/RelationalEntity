package ie.munnellg;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;

public class Subject extends RelationalEntity
{
	private String name;

	@RelationalFieldSetterMeta(inverseField = "teachesSubject")
	protected Person teacher;

	@RelationalFieldSetterMeta(inverseField = "teachingAssistantFor")
	protected List<Person> teachingAssistants;

	@RelationalFieldSetterMeta(inverseField = "enrolledInSubjects")
	protected List<Person> students;

	public Subject(String name)
	{
		this.name = name;
		
		this.teacher = null;

		this.teachingAssistants = new ArrayList<Person>();

		this.students = new ArrayList<Person>();
	}
	
	public void setTeacher(Person teacher)
	{
		this.setField("teacher", teacher);
	}

	public Person getTeacher()
	{
		return this.teacher;
	}

	public List<Person> getTeachingAssistants()
	{
		return this.teachingAssistants;
	}

	public void setTeachingAssistants(List<Person> teachingAssistants)
	{
		this.setListField("teachingAssistants", teachingAssistants);
	}

	public void addTeachingAssistant(Person teachingAssistant)
	{
		this.addToField("teachingAssistants", teachingAssistant);	
	}

	public void removeTeachingAssistant(Person teachingAssistant)
	{
		this.removeFromField("teachingAssistants", teachingAssistant);	
	}

	public List<Person> getStudents()
	{
		return this.students;
	}

	public void setStudents(List<Person> students)
	{
		this.setListField("students", students);
	}

	public void addStudent(Person student)
	{
		this.addToField("students", student);
	}

	public void removeStudent(Person student)
	{
		this.removeFromField("students", student);	
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}