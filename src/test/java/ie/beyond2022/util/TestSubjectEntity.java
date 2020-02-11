package ie.beyond2022.util;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;

public class TestSubjectEntity extends RelationalEntity
{
	// name is primitive, so meta tag should be ignored
	@RelationalFieldSetterMeta(inverseField = "noInverseForThisOne")	
	protected String name;

	@RelationalFieldSetterMeta(inverseField = "teachesSubject")
	protected TestPersonEntity teacher;

	@RelationalFieldSetterMeta(inverseField = "teachingAssistantFor")
	protected List<TestPersonEntity> teachingAssistants;

	@RelationalFieldSetterMeta(inverseField = "enrolledInSubjects")
	protected List<TestPersonEntity> students;

	public TestSubjectEntity(String name)
	{
		this.setName(name);

		this.setTeacher(null);

		this.setTeachingAssistants(new ArrayList<TestPersonEntity>());
		
		this.setStudents(new ArrayList<TestPersonEntity>());
	}
	
	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.setField("name", name);
	}

	public void setTeacher(TestPersonEntity teacher)
	{
		this.setField("teacher", teacher);
	}

	public TestPersonEntity getTeacher()
	{
		return this.teacher;
	}

	public List<TestPersonEntity> getTeachingAssistants()
	{
		return this.teachingAssistants;
	}

	public void setTeachingAssistants(List<TestPersonEntity> teachingAssistants)
	{
		this.setField("teachingAssistants", teachingAssistants);
	}

	public void addTeachingAssistant(TestPersonEntity teachingAssistant)
	{
		this.addToField("teachingAssistants", teachingAssistant);	
	}

	public void removeTeachingAssistant(TestPersonEntity teachingAssistant)
	{
		this.removeFromField("teachingAssistants", teachingAssistant);	
	}

	public List<TestPersonEntity> getStudents()
	{
		return this.students;
	}

	public void setStudents(List<TestPersonEntity> students)
	{
		this.setField("students", students);
	}

	public void addStudent(TestPersonEntity student)
	{
		this.addToField("students", student);
	}

	public void removeStudent(TestPersonEntity student)
	{
		this.removeFromField("students", student);	
	}
}