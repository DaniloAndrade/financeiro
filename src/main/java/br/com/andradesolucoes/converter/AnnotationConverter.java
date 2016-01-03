package br.com.andradesolucoes.converter;

import java.lang.reflect.Field;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value="annotationConverter")
public class AnnotationConverter implements Converter {
	


	
	@Override
	public Object getAsObject(FacesContext ctx, UIComponent ui, String value) {
		if (value != null) { 
			Object object = this.getAttributesFrom(ui).get(value);
			 return object;  
	     }  
		return null;  
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent ui, Object value) {
		 if (value != null  
	                && !"".equals(value)) {  
	  
			 	String key = getValueKey(value);
	            // add item como atributo do componente
	            this.addAttribute(ui, key, value);  
	  
	            return key;
	        }  
		
		return null;
	}

	
	
	private String getValueKey(Object value) {
		String key = "" ;
		
		
		
		if(value instanceof String){
			return (String) value;
		}
		
		Class<? extends Object> classe = value.getClass();
		Field[] declaredFields = classe.getDeclaredFields();
		for (Field field : declaredFields) {
			if(field.isAnnotationPresent(ConverterID.class)){
				field.setAccessible(true);
				try {
					key = field.get(value).toString();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		return key;
	}

	protected Map<String, Object> getAttributesFrom(UIComponent component) {  
        return component.getAttributes();  
    }  
	
	
	
	 protected void addAttribute(UIComponent component,String key, Object o) {  
	     if(key !=null ){
	    	 this.getAttributesFrom(component).put(key, o);  
	     }
	  }  
}
