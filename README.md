# Relational Entity

Generic class for maintaining the integrity of bi-directional relationships
between Java objects. For example, if a student is enrolled in a subject and
both the student and subject classes respetively store a reference to the other,
then if the student leaves the subject, the subject should be updated accordingly