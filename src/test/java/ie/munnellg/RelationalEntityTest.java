package ie.munnellg;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class RelationalEntityTest 
{
    @Test
    public void testOneToOneMappings()
    {
        Person person   = new Person("John");
    	Subject subject = new Subject("Computer Science");

    	person.setTeachesSubject(subject);

    	assertTrue(subject.equals(person.getTeachesSubject()));
        assertTrue(person.equals(subject.getTeacher()));

        subject.setTeacher(null);

        assertTrue(null == person.getTeachesSubject());
        assertTrue(null == subject.getTeacher());
    }

    @Test
    public void testManyToOneMappings()
    {
        try
        {
            Person  person  = new Person("John");
            Subject subject = new Subject("Computer Science");

            person.setTeachingAssistantFor(subject);

            assertTrue(subject.equals(person.getTeachingAssistantFor()));
            assertTrue(subject.getTeachingAssistants().contains(person));
            assertTrue(subject.getTeachingAssistants().size() == 1);

            person.setTeachingAssistantFor(null);

            assertTrue(!subject.equals(person.getTeachingAssistantFor()));
            assertTrue(!subject.getTeachingAssistants().contains(person));
            assertTrue(subject.getTeachingAssistants().size() == 0);    
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Test
    public void testOneToManyMappings()
    {
        Person[] people = {
            new Person("Jason"),
            new Person("Aimee"),
            new Person("Clare"),
            new Person("James")
        };

        Subject subject = new Subject("Computer Science");

        for (int i = 0; i < people.length; i++)
        {
            subject.addTeachingAssistant(people[i]);

            assertTrue(subject.equals(people[i].getTeachingAssistantFor()));
            assertTrue(subject.getTeachingAssistants().contains(people[i]));
            assertTrue(subject.getTeachingAssistants().size() == i + 1);
        }

        for (int i = 0; i < people.length; i++)
        {
            // prior to removal, ensure the addition of students didn't cause
            // a student to become dissociated from the course
            assertTrue(subject.getTeachingAssistants().contains(people[i]));
            assertTrue(subject.equals(people[i].getTeachingAssistantFor()));

            subject.removeTeachingAssistant(people[i]);

            assertTrue(!subject.equals(people[i].getTeachingAssistantFor()));
            assertTrue(!subject.getTeachingAssistants().contains(people[i]));
            assertTrue(subject.getTeachingAssistants().size() == people.length - (i + 1));
        }
    }

    @Test
    public void testTransferOneToManyMappings()
    {
        Person[] people = {
            new Person("Jason"),
            new Person("Aimee"),
            new Person("Clare"),
            new Person("James")
        };

        Subject subject1 = new Subject("Computer Science");
        Subject subject2 = new Subject("Engineering");

        for (int i = 0; i < people.length; i++)
        {
            subject1.addTeachingAssistant(people[i]);

            // ensure student was added
            assertTrue(subject1.equals(people[i].getTeachingAssistantFor()));
            assertTrue(subject1.getTeachingAssistants().contains(people[i]));
            assertTrue(subject1.getTeachingAssistants().size() == i + 1);
        }

        for (int i = 0; i < people.length; i++)
        {
            subject2.addTeachingAssistant(people[i]);

            assertTrue(subject2.equals(people[i].getTeachingAssistantFor()));
            assertTrue(subject2.getTeachingAssistants().contains(people[i]));
            assertTrue(subject2.getTeachingAssistants().size() == i + 1);

            assertTrue(!subject1.equals(people[i].getTeachingAssistantFor()));
            assertTrue(!subject1.getTeachingAssistants().contains(people[i]));
            assertTrue(subject1.getTeachingAssistants().size() == people.length - (i + 1));
        }
    }

    @Test
    public void testTransferManyToOneMappings()
    {
        Person person = new Person("Jason");

        Subject subject1 = new Subject("Computer Science");
        Subject subject2 = new Subject("Engineering");

        person.setTeachingAssistantFor(subject1);

        assertTrue(subject1.equals(person.getTeachingAssistantFor()));
        assertTrue(subject1.getTeachingAssistants().contains(person));
        assertTrue(subject1.getTeachingAssistants().size() == 1);

        person.setTeachingAssistantFor(subject2);

        assertTrue(subject2.equals(person.getTeachingAssistantFor()));
        assertTrue(subject2.getTeachingAssistants().contains(person));
        assertTrue(subject2.getTeachingAssistants().size() == 1);

        assertTrue(!subject1.equals(person.getTeachingAssistantFor()));
        assertTrue(!subject1.getTeachingAssistants().contains(person));
        assertTrue(subject1.getTeachingAssistants().size() == 0);
    }

    @Test
    public void testManyToManyMappings()
    {
        Person[] people = {
            new Person("Jason"),
            new Person("Aimee"),
            new Person("Clare"),
            new Person("James")
        };

        Subject[] subjects = {
            new Subject("Computer Science"),
            new Subject("Engineering")
        };

        for (int i = 0; i < people.length; i++)
        {
            for (int j = 0; j < subjects.length; j++)
            {
                people[i].enrollInSubject(subjects[j]);
            }
        }

        for (int i = 0; i < people.length; i++)
        {
            for (int j = 0; j < subjects.length; j++)
            {
                assertTrue(subjects[j].getStudents().contains(people[i]));
                assertTrue(people[i].getEnrolledInSubjects().contains(subjects[j]));
                assertTrue(subjects[j].getStudents().size() == people.length);
            }

            assertTrue(people[i].getEnrolledInSubjects().size() == subjects.length);
        }

        for (int i = 0; i < people.length; i++)
        {
            for (int j = 0; j < subjects.length; j++)
            {
                people[i].leaveSubject(subjects[j]);

                assertTrue(!subjects[j].getStudents().contains(people[i]));
                assertTrue(!people[i].getEnrolledInSubjects().contains(subjects[j]));
                assertTrue(subjects[j].getStudents().size() == people.length - (i + 1));
            }
        }
    }

    @Test
    public void testReplaceOneToManyMappings()
    {
        ArrayList<Person> people1 = new ArrayList<Person>();
        people1.add(new Person("Jason"));
        people1.add(new Person("Aimee"));

        ArrayList<Person> people2 = new ArrayList<Person>();
        people2.add(new Person("Clare"));
        people2.add(new Person("James"));

        Subject subject = new Subject("Computer Science");

        for (int i = 0; i < people1.size(); i++)
        {
            subject.addTeachingAssistant(people1.get(i));
        }

        subject.setTeachingAssistants(people2);

        for (int i = 0; i < people1.size(); i++)
        {
            assertTrue(!subject.getTeachingAssistants().contains(people1.get(i)));
            assertTrue(!subject.equals(people1.get(i).getTeachingAssistantFor()));
        }

        for (int i = 0; i < people2.size(); i++)
        {
            assertTrue(subject.getTeachingAssistants().contains(people2.get(i)));
            assertTrue(subject.equals(people2.get(i).getTeachingAssistantFor()));
        }
    }

    @Test
    public void testReplaceManyToManyMappings()
    {
        ArrayList<Person> people1 = new ArrayList<Person>();
        people1.add(new Person("Jason"));
        people1.add(new Person("Aimee"));

        ArrayList<Person> people2 = new ArrayList<Person>();
        people2.add(new Person("Clare"));
        people2.add(new Person("James"));

        ArrayList<Subject> subjects = new ArrayList<Subject>();
        subjects.add(new Subject("Computer Science"));
        subjects.add(new Subject("Engineering"));

        for (int i = 0; i < people1.size(); i++)
        {
            for (int j = 0; j < subjects.size(); j++)
            {
                people1.get(i).enrollInSubject(subjects.get(j));
            }
        }

        subjects.get(0).setStudents(people2);

        for (int i = 0; i < people1.size(); i++)
        {
            assertTrue(!subjects.get(0).getStudents().contains(people1.get(i)));
            assertTrue(!people1.get(i).getEnrolledInSubjects().contains(subjects.get(0)));
            assertTrue(subjects.get(1).getStudents().contains(people1.get(i)));
            assertTrue(people1.get(i).getEnrolledInSubjects().contains(subjects.get(1)));
        }

        for (int i = 0; i < people1.size(); i++)
        {
            assertTrue(subjects.get(0).getStudents().contains(people2.get(i)));
            assertTrue(people2.get(i).getEnrolledInSubjects().contains(subjects.get(0)));
            assertTrue(!subjects.get(1).getStudents().contains(people2.get(i)));
            assertTrue(!people2.get(i).getEnrolledInSubjects().contains(subjects.get(1)));
        }
    }
}
