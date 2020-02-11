package ie.beyond2022.util;

import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;
import java.lang.annotation.Annotation;
import java.lang.IllegalAccessException;
import java.lang.IllegalArgumentException;

public abstract class RelationalEntity
{
	private <T> void checkValidFieldType(Field field, T value) throws IllegalArgumentException
	{
		// make sure the assignment being attempted is valid
		if (value != null)
		{	
			if (!field.getType().isAssignableFrom(value.getClass()))
			{
				String msg = String.format(
					"Cannot set field %s from object of type %s",
						field.getType().getName(),
						value.getClass().getName()
				);

				throw new IllegalArgumentException(msg);
			}
		}
	}

	private void makeAccessible(Field field)
	{
		if (!field.isAccessible())
		{
			field.setAccessible(true);	
		}
	}

	private void unsetOrRemoveThisFromInverseField(Field field, RelationalEntity entity)
	{
		if (Collection.class.isAssignableFrom(field.getType()))
		{
			entity.removeFromField(field, this);
		}
		else
		{
			entity.unsetField(field, this);
		}
	}

	private void setOrAddThisToInverseField(Field field, RelationalEntity entity)
	{
		if (Collection.class.isAssignableFrom(field.getType()))
		{
			entity.addToField(field, this);
		}
		else
		{
			entity.setField(field, this);
		}
	}

	private void applyRelationalUpdate(Field field, RelationalEntity newValue, RelationalFieldSetterMeta meta) throws IllegalAccessException, NoSuchFieldException
	{
		RelationalEntity oldValue = (RelationalEntity) field.get(this);
		
		if (!same(oldValue, newValue))
		{
			field.set(this, newValue);

			if (oldValue != null)
			{		
				Field inverseField = oldValue.getClass().getDeclaredField(meta.inverseField());

				unsetOrRemoveThisFromInverseField(inverseField, oldValue);
			}

			if (newValue != null)
			{
				Field inverseField = newValue.getClass().getDeclaredField(meta.inverseField());

				setOrAddThisToInverseField(inverseField, newValue);
			}
		}
	}

	private void applyRelationalUpdate(Field field, Collection newCollection, RelationalFieldSetterMeta meta) throws IllegalAccessException, NoSuchFieldException
	{
		Collection oldCollection = (Collection) field.get(this);

		if (!same(oldCollection, newCollection))
		{
			if (oldCollection != null)
			{
				for (Iterator<RelationalEntity> it = oldCollection.iterator(); it.hasNext(); )
				{
					RelationalEntity e = it.next();

					Field inverseField = e.getClass().getDeclaredField(meta.inverseField());
	                
	                it.remove();

	                unsetOrRemoveThisFromInverseField(inverseField, e);
				}
			}
			
			field.set(this, newCollection);

			if (newCollection != null)
			{
				for (Iterator<RelationalEntity> it = newCollection.iterator(); it.hasNext(); )
				{
					RelationalEntity e = it.next();

					Field inverseField = e.getClass().getDeclaredField(meta.inverseField());

					setOrAddThisToInverseField(inverseField, e);
				}
			}
		}
	}

	protected <T> void setField(String fieldName, T newValue)
	{
		try
		{
			Field field = this.getClass().getDeclaredField(fieldName);

			this.setField(field, newValue);
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected <T> void setField(Field field, T newValue)
	{
		try
		{
			checkValidFieldType(field, newValue);

			makeAccessible(field);

			// search for a meta tag that tells us how to do the assignment
			RelationalFieldSetterMeta meta = field.getAnnotation(RelationalFieldSetterMeta.class);

			if (meta == null)
			{
				// No meta, just assign the field
				field.set(this, newValue);
			}
			else if (Collection.class.isAssignableFrom(field.getType()))
			{
				this.applyRelationalUpdate(field, (Collection) newValue, meta);
			}
			else if (RelationalEntity.class.isAssignableFrom(field.getType()))
			{
				this.applyRelationalUpdate(field, (RelationalEntity) newValue, meta);
			}
			else
			{
				field.set(this, newValue);
			}
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected <T> void unsetField(String fieldName, T expectedCurrentValue)
	{
		try
		{
			Field field = this.getClass().getDeclaredField(fieldName);

			this.unsetField(field, expectedCurrentValue);
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected <T> void unsetField(Field field, T expectedCurrentValue)
	{
		try
		{
			checkValidFieldType(field, expectedCurrentValue);

			makeAccessible(field);
			
			if (this.same((T) field.get(this), expectedCurrentValue))
			{
				this.setField(field, null);
			}
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void addToField(String fieldName, RelationalEntity item)
	{
		try
		{
			Field field = this.getClass().getDeclaredField(fieldName);

			this.addToField(field, item);
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void addToField(Field field, RelationalEntity item)
	{
		try
		{
			if (!Collection.class.isAssignableFrom(field.getType()))
			{
				throw new IllegalArgumentException("Field must be a Collection type");
			}

			makeAccessible(field);

			Collection c = (Collection) field.get(this);

			if (c.contains(item) || item == null)
			{
				return;
			}

			c.add(item);

			RelationalFieldSetterMeta meta = field.getAnnotation(RelationalFieldSetterMeta.class);

			if (meta != null)
			{
				Field inverseField = item.getClass().getDeclaredField(meta.inverseField());

				if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					item.addToField(inverseField, this);
				}
				else
				{
					item.setField(inverseField, this);
				}
			}
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void removeFromField(String fieldName, RelationalEntity item)
	{
		try
		{
			Field field = this.getClass().getDeclaredField(fieldName);

			this.removeFromField(field, item);
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void removeFromField(Field field, RelationalEntity item) 
	{
		try
		{
			if (!Collection.class.isAssignableFrom(field.getType()))
			{
				throw new IllegalArgumentException("Field must be a Collection type");
			}

			makeAccessible(field);

			Collection c = (Collection) field.get(this);

			if (!c.contains(item) || item == null)
			{
				return;
			}

			c.remove(item);

			RelationalFieldSetterMeta meta = field.getAnnotation(RelationalFieldSetterMeta.class);

			if (meta != null)
			{
				Field inverseField = item.getClass().getDeclaredField(meta.inverseField());

				if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					item.removeFromField(inverseField, this);
				}
				else
				{
					item.unsetField(inverseField, this);
				}
			}
		}
		catch (IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected <T> boolean same(T a, T b)
	{
		if (a == null)
		{
			return b == null;
		}
		else
		{
			return a.equals(b);
		}
	}
}