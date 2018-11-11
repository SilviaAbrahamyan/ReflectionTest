import java.lang.reflect.Field;

public class ReflectionTest {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        Person p = new Person();
        System.out.println(serialize(p));
    }

    static String serialize(Object obj) throws IllegalAccessException, InstantiationException, NoSuchFieldException {

        Class<Object> objectClass = (Class<Object>) obj.getClass();
        Field[] fields = objectClass.getDeclaredFields();
     
        String result = "";
        result = toJsonString(fields, obj);
        return result;
    }

    private static String toJsonString(Field[] fields, Object object) throws IllegalAccessException {
		int size = fields.length;
        StringBuilder builder = new StringBuilder();
        builder.append("{").append("\n");
        for (Field field : fields) {
            String name = field.getName();
            field.setAccessible(true);
            Object value = getSerializedValue(field, object);
            if (size != 1) {
                builder.append("\t").append("\"" + name + "\"" + ": " + value + ",").append("\n");
            } else {
                builder.append("\t").append("\"" + name + "\"" + ": " + value).append("\n");
            }
            size--;
            field.setAccessible(false);
        }
        builder.append("}");
        return builder.toString();
    }

    private static Object getSerializedValue(Field field, Object obj) throws IllegalAccessException {
        if (field.isAnnotationPresent(Transient.class)) {
            boolean annotationEnabled = field.getAnnotation(Transient.class).enabled();
            if (annotationEnabled) {
                if (!field.getType().isPrimitive()) {
                    return null;
                } else if (field.getType().equals(boolean.class)) {
                    return String.valueOf(false);
                } else if (field.getType().equals(long.class) || field.getType().equals(int.class) || field.getType().equals(short.class) || field.getType().equals(byte.class)) {
                    return String.valueOf(0);
                } else {
                    return String.valueOf(0.0);
                }
            }
        }
        return field.get(obj);
    }
}

class Person {
    @Transient
    private String name = "Anna";
	@Transient
    private String email = "anna@mail.ru";
	@Transient
    private long phone = 98787675;
  
    public Person() {

    }

    public Person(String name, String email, long phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}