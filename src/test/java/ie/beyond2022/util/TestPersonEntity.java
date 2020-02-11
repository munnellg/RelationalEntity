package ie.beyond2022.util;

import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;

public class TestPersonEntity extends RelationalEntity
{
	protected String name;

	@RelationalFieldSetterMeta(inverseField = "teacher")
	protected TestSubjectEntity teachesSubject;

	@RelationalFieldSetterMeta(inverseField = "teachingAssistants")
	protected TestSubjectEntity teachingAssistantFor;

	@RelationalFieldSetterMeta(inverseField = "students")
	protected List<TestSubjectEntity> enrolledInSubjects;

	public TestPersonEntity(String name)
	{
		this.setName(name);
		this.setTeachesSubject(null);
		this.setTeachingAssistantFor(null);
		this.setEnrolledInSubjects(new ArrayList<TestSubjectEntity>());
	}

	public void setName(String name)
	{
		this.setField("name", name);
	}

	public String getName()
	{
		return this.name;
	}

	public void setTeachesSubject(TestSubjectEntity subject)
	{
		this.setField("teachesSubject", subject);
	}

	public TestSubjectEntity getTeachesSubject()
	{
		return this.teachesSubject;
	}

	public void setTeachingAssistantFor(TestSubjectEntity subject)
	{
		this.setField("teachingAssistantFor", subject);	
	}

	public TestSubjectEntity getTeachingAssistantFor()
	{
		return this.teachingAssistantFor;
	}

	public List<TestSubjectEntity> getEnrolledInSubjects()
	{
		return this.enrolledInSubjects;
	}

	public void setEnrolledInSubjects(List<TestSubjectEntity> enrolledInSubjects)
	{
		this.setField("enrolledInSubjects", enrolledInSubjects);
	}

	public void enrollInSubject(TestSubjectEntity subject)
	{
		this.addToField("enrolledInSubjects", subject);
	}

	public void leaveSubject(TestSubjectEntity subject)
	{
		this.removeFromField("enrolledInSubjects", subject);
	}
}