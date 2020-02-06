package ie.munnellg;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;

public class Person extends RelationalEntity
{
	private String name;

	@RelationalFieldSetterMeta(inverseField = "teacher")
	protected Subject teachesSubject;

	@RelationalFieldSetterMeta(inverseField = "teachingAssistants")
	protected Subject teachingAssistantFor;

	@RelationalFieldSetterMeta(inverseField = "students")
	protected List<Subject> enrolledInSubjects;

	public Person(String name)
	{
		this.name = name;
		teachesSubject       = null;
		teachingAssistantFor = null;
		enrolledInSubjects   = new ArrayList<Subject>();
	}

	public void setTeachesSubject(Subject subject)
	{
		this.setField("teachesSubject", subject);
	}

	public Subject getTeachesSubject()
	{
		return this.teachesSubject;
	}

	public void setTeachingAssistantFor(Subject subject)
	{
		this.setField("teachingAssistantFor", subject);	
	}

	public Subject getTeachingAssistantFor()
	{
		return this.teachingAssistantFor;
	}

	public List<Subject> getEnrolledInSubjects()
	{
		return this.enrolledInSubjects;
	}

	public void enrollInSubject(Subject subject)
	{
		this.addToField("enrolledInSubjects", subject);
	}

	public void leaveSubject(Subject subject)
	{
		this.removeFromField("enrolledInSubjects", subject);
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