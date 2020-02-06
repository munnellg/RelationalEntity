package ie.munnellg;

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
	private void checkValidFieldType(Field field, RelationalEntity value) throws IllegalArgumentException
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

	private void applyRelationalUpdate(Field field, RelationalEntity newValue, RelationalFieldSetterMeta meta) throws IllegalAccessException, NoSuchFieldException
	{
		RelationalEntity oldValue = (RelationalEntity) field.get(this);
		
		if (!same(oldValue, newValue))
		{
			field.set(this, newValue);

			if (oldValue != null)
			{		
				Field inverseField = oldValue.getClass().getDeclaredField(meta.inverseField());

				if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					oldValue.removeFromField(inverseField, this);
				}
				else
				{
					oldValue.unsetField(inverseField, this);
				}
			}

			if (newValue != null)
			{
				Field inverseField = newValue.getClass().getDeclaredField(meta.inverseField());

				if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					newValue.addToField(inverseField, this);
				}
				else
				{
					newValue.setField(inverseField, this);
				}
			}
		}
	}

	protected void setField(String fieldName, RelationalEntity newValue)
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

	protected void setField(Field field, RelationalEntity newValue)
	{
		try
		{
			checkValidFieldType(field, newValue);

			// search for a meta tag that tells us how to do the assignment
			RelationalFieldSetterMeta meta = field.getAnnotation(RelationalFieldSetterMeta.class);

			// No meta, just assign the field
			if (meta == null)
			{
				field.set(this, newValue);
			}
			else
			{
				applyRelationalUpdate(field, newValue, meta);
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

	protected void unsetField(String fieldName, RelationalEntity expectedCurrentValue)
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

	protected void unsetField(Field field, RelationalEntity expectedCurrentValue)
	{
		try
		{
			RelationalEntity currentValue = (RelationalEntity) field.get(this);
			
			if (this.same(currentValue, expectedCurrentValue))
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

	protected void setListField(String fieldName, Collection collection)
	{
		try
		{
			Field field = this.getClass().getDeclaredField(fieldName);

			this.setListField(field, collection);
		}
		catch (NoSuchFieldException ex)
		{
			ex.printStackTrace();
		}
	}

	protected void setListField(Field field, Collection newCollection)
	{
		try
		{
			if (!Collection.class.isAssignableFrom(field.getType()))
			{
				throw new IllegalArgumentException("Field must be a Collection type");
			}

			Collection oldCollection = (Collection) field.get(this);

			RelationalFieldSetterMeta meta = field.getAnnotation(RelationalFieldSetterMeta.class);

			for (Iterator<RelationalEntity> it = oldCollection.iterator(); it.hasNext(); )
			{
				RelationalEntity e = it.next();

				Field inverseField = e.getClass().getDeclaredField(meta.inverseField());
                
                it.remove();

                if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					e.removeFromField(inverseField, this);
				}
				else
				{
					e.unsetField(inverseField, this);
				}
			}

			field.set(this, newCollection);

			for (Iterator<RelationalEntity> it = newCollection.iterator(); it.hasNext(); )
			{
				RelationalEntity e = it.next();

				Field inverseField = e.getClass().getDeclaredField(meta.inverseField());

				if (Collection.class.isAssignableFrom(inverseField.getType()))
				{
					e.addToField(inverseField, this);
				}
				else
				{
					e.setField(inverseField, this);
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

	protected boolean same(RelationalEntity a, RelationalEntity b)
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