package com.swatt.blockchain.entity;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

import org.mockito.Mock;

public class EntityTest {

    private Class<?> classUnderTest;

    @Mock
    private Connection connection;
    
    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;
    
    public EntityTest(Class<?> clazz) {
        this.classUnderTest = clazz;
    }

    public Object createInstance() throws Exception {
        Object instance = classUnderTest.newInstance();

        Arrays.stream(classUnderTest.getDeclaredFields()).forEach(f -> {
            try {
                String setMethodName = format("set%s", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                Method setMethod = classUnderTest.getDeclaredMethod(setMethodName, f.getType());

                String getMethodName = format("get%s", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                Method getMethod = classUnderTest.getDeclaredMethod(getMethodName);
                
                Method setScaleMethod = null;
                String setScaleMethodName = format("set%sScale", LOWER_CAMEL.to(UPPER_CAMEL, f.getName()));
                try {
                    setScaleMethod = classUnderTest.getDeclaredMethod(setScaleMethodName, int.class);
                } catch (NoSuchMethodException e) {
                }

                if (f.getType() == String.class) {
                    setMethod.invoke(instance, f.getName());
                    assertThat(getMethod.invoke(instance), is(f.getName()));
                }

                if (f.getType() == int.class) {
                    int x = (int)System.currentTimeMillis();
                    setMethod.invoke(instance, x);
                    assertThat(getMethod.invoke(instance), is(x));
                }

                if (f.getType() == boolean.class) {
                    boolean b = System.currentTimeMillis() % 2 == 0;
                    setMethod.invoke(instance, b);
                    assertThat(getMethod.invoke(instance), is(b));
                }

                if (f.getType() == long.class) {
                    long x = System.currentTimeMillis();
                    setMethod.invoke(instance, x);
                    
                    if (setScaleMethod != null) {
                        setScaleMethod.invoke(instance, 0);
                        
                        double result = (double)getMethod.invoke(instance);
                        assertThat(Double.valueOf(result).longValue(), is(x));
                    } else {
                        assertThat(getMethod.invoke(instance), is(x));
                    }
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            }
        });
        
        return instance;
    }
    
    public void testConstructorGettersAndSetters() throws Exception {
        try {
            createInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
